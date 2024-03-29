package com.cspup.notespro.service;

import com.cspup.notespro.DTO.WsNote;
import com.cspup.notespro.delta.Delta;
import com.cspup.notespro.delta.NoteDelta;
import com.cspup.notespro.entity.Lock;
import com.cspup.notespro.entity.Note;
import com.cspup.notespro.mapper.NoteMapper;
import com.cspup.notespro.utils.SqlUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author csp
 * @date 2022/2/22 16:36
 * @description
 */
@Service
@Slf4j
public class NoteService {

    private final DataSource dataSource;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${noteFilePath}")
    private String noteFilePath;


    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private LockService lockService;

    @Autowired
    private RedisTemplate redisTemplate;

    // 锁的过期时间
    private static final long LOCK_EXPIRE_TIME = 30000;
    // 获取锁的最大重试次数
    private static final int MAX_RETRY_COUNT = 5;
    // 重试间隔
    private static final long RETRY_INTERVAL = 100;

    public NoteService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    //    TODO:之后将存储方式改为一个文档一个大字符串文件进行存储，不再存到数据库中
    public void createNote(String content, String label) {
        Note note = new Note();
        note.setContent(content);
        note.setLabel(label);
        noteMapper.addNote(note);
    }

    public void createNote(Note note) {
        noteMapper.addNote(note);
    }

    public int updateNote(Note note) {
        if (note.getId() == 0) {
            return noteMapper.addNote(note);
        } else if (lockService.getLockStatus(note.getId())) {
            throw new RuntimeException("已加锁");
        }
        return noteMapper.updateNote(note);
    }

    public String getLastContent(String label) {
        String sql = "SELECT content FROM note WHERE label = ? ORDER BY id desc limit 1";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sql = SqlUtil.toSql(sql, label);
        try {
            return jdbcTemplate.queryForObject(sql, String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Note getLastNoteByLabel(String label) {
        return noteMapper.selectNoteByLabel(label);
    }

    public int deleteNote(String label) {
        Note note = getLastNoteByLabel(label);
        note.setContent("{\"ops\":[{\"insert\":\"Hello World!\\n\"}]}");
        Lock lock = new Lock();
        lock.setNoteId(note.getId());
        lock.setLocked(false);
        lockService.updateLock(lock);
        return updateNote(note);
    }


    /**
     * @param wsNote 带客户端id的数据
     * @return 成功条数
     */
    public int updateNote(WsNote wsNote) {
        return noteMapper.updateNote(wsNote.getNote());
    }

    /**
     * 保存文档
     *
     * @param label    标号
     * @param noteJson 内容json字符串
     */
    public void saveNote(String label, String noteJson) {
        // 获取文件路径
        String filePath = noteMapper.getFilePath(label);
        // 新建文件
        if (filePath == null || filePath.isEmpty()) {
            UUID uuid = UUID.randomUUID();
            filePath = noteFilePath + uuid;
            noteMapper.addFilePath(label, filePath);
        }

        String lockKey = "file_lock_" + label;
        UUID uuid = UUID.randomUUID();

        // 续期线程
        Thread renewThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // 比原锁时间短一些，定期续期锁
                    redisTemplate.expire(lockKey, LOCK_EXPIRE_TIME - 2, TimeUnit.MILLISECONDS);
                    Thread.sleep((LOCK_EXPIRE_TIME - 3) * 1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });


        for (int attempt = 0; attempt < MAX_RETRY_COUNT; attempt++) {
            // 创建一个带有唯一编号和过期时间的锁
            Boolean isSuccess = redisTemplate.opsForValue().setIfAbsent(lockKey, uuid.toString(), Duration.ofMillis(LOCK_EXPIRE_TIME));

            if (Boolean.TRUE.equals(isSuccess)) {
                // 启动续期线程
                renewThread.start();
                // 进行文件相关操作
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                    writer.write(noteJson);
                    noteMapper.updateFilePath(label, filePath);
                    log.info("文件被保存在：" + filePath);
                    return;
                } catch (Exception e) {
                    log.error(e.getMessage());
                } finally {
                    // 结束续期线程
                    renewThread.interrupt();
                    // 释放锁，避免删除其它线程设置的锁
                    String uuid1 = (String) redisTemplate.opsForValue().get(lockKey);
                    if (uuid1 != null && uuid1.equals(uuid.toString())) {
                        redisTemplate.delete(lockKey);
                    }
                }
            } else {
                // 通过休眠线程实现重试
                try {
                    Thread.sleep(RETRY_INTERVAL);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Interrupted during sleep between lock retries", e);
                }

            }
        }

    }


    /**
     * 获取文档内容Delta
     *
     * @param label 标号
     * @return 内容JSON字符串
     */
    public Delta getNoteContent(String label) {
        NoteDelta noteDelta = getServerDelta(label);
        return noteDelta.getDelta();
    }

    /**
     * 获取服务端文档NoteDelta
     *
     * @param label 标号
     * @return NoteDelta
     */
    public NoteDelta getServerDelta(String label) {
        String filePath = noteMapper.getFilePath(label);
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String res;
            while ((res = reader.readLine()) != null) {
                content.append(res);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        NoteDelta noteDelta;
        if (content.isEmpty()) {
            noteDelta = new NoteDelta(true, label, 1, new Delta().insert("Hello World!"));
            saveNote(label, noteDelta.toString());
        } else {
            try {
                noteDelta = objectMapper.readValue(content.toString(), NoteDelta.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return noteDelta;
    }
}

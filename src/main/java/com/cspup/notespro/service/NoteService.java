package com.cspup.notespro.service;

import com.cspup.notespro.entity.Lock;
import com.cspup.notespro.entity.Note;
import com.cspup.notespro.mapper.NoteMapper;
import com.cspup.notespro.utils.SqlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

/**
 * @author csp
 * @date 2022/2/22 16:36
 * @description
 */
@Service
public class NoteService {

    private final DataSource dataSource;

    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private LockService lockService;

    @Autowired
    private RedisTemplate redisTemplate;

    public NoteService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void createNote(String content, String label){
        Note note = new Note();
        note.setContent(content);
        note.setLabel(label);
        noteMapper.addNote(note);
    }

    public void createNote(Note note){
        noteMapper.addNote(note);
    }

    public int updateNote(Note note){
        if (note.getId()==0){
            return noteMapper.addNote(note);
        }else if (lockService.getLockStatus(note.getId())){
            throw new RuntimeException("已加锁");
        }
        return noteMapper.updateNote(note);
    }

    public String getLastContent(String label){
        String sql = "SELECT content FROM note WHERE label = ? ORDER BY id desc limit 1";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sql = SqlUtil.toSql(sql,label);
        try{
            return jdbcTemplate.queryForObject(sql,String.class);
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    public Note getLastNoteByLabel(String label){
        return noteMapper.selectNoteByLabel(label);
    }

    public int deleteNote(String label){
        Note note = getLastNoteByLabel(label);
        note.setContent("{\"ops\":[{\"insert\":\"Hello World!\\n\"}]}");
        Lock lock = new Lock();
        lock.setNoteId(note.getId());
        lock.setLocked(false);
        lockService.updateLock(lock);
        return updateNote(note);
    }
}

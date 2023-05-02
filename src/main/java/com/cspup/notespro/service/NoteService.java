package com.cspup.notespro.service;

import com.cspup.notespro.entity.Note;
import com.cspup.notespro.mapper.NoteMapper;
import com.cspup.notespro.utils.SqlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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

    public NoteService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Note createNote(String content, String label){
        Note note = new Note();
        note.setContent(content);
        note.setLabel(label);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        String sql = "insert into note(content,label) values(?,?)";
        Object[] objects = new Object[]{content,label};
        jdbcTemplate.update(sql,objects);
        return note;
    }

    public int updateNote(Note note){
        if (note.getId()==0){
            return noteMapper.initNote(note);
        }else{
//            判断是否加锁
            if (lockService.getLockStatus((long) note.getId())){
                return -1;
            }
            return noteMapper.updateNote(note);
        }
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
}

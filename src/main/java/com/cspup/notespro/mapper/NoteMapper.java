package com.cspup.notespro.mapper;

import com.cspup.notespro.entity.Note;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author csp
 * @date 2023/4/24 19:58
 * @description
 */
@Mapper
public interface NoteMapper {
    public Note selectNote(Note note);

    public Note selectNoteById(Long id);

    public Note selectNoteByLabel(String label);
    int addNote(Note note);
    public int updateNote(Note note);
}

package com.cspup.notespro.mapper;

import com.cspup.notespro.entity.Note;

/**
 * @author csp
 * @date 2023/4/24 19:58
 * @description
 */
public interface NoteMapper {
    public Note selectNote(Note note);

    public Note selectNoteById(Long id);

    public Note selectNoteByLabel(String label);

    public int initNote(Note note);
    public int updateNote(Note note);
}

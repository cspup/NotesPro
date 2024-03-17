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
    Note selectNote(Note note);

    Note selectNoteById(Long id);

    Note selectNoteByLabel(String label);
    int addNote(Note note);
    int updateNote(Note note);

    String getFilePath(String label);

    int addFilePath(String label, String path);
    int updateFilePath(String label, String path);

}

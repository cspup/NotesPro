package com.cspup.notespro.controller;

import com.cspup.notespro.delta.Delta;
import com.cspup.notespro.delta.NoteDelta;
import com.cspup.notespro.entity.Note;
import com.cspup.notespro.service.NoteService;
import com.cspup.notespro.utils.R;
import org.springframework.web.bind.annotation.*;

/**
 * @author csp
 * @date 2022/2/22 16:30
 * @description
 */
@RestController
public class NoteController {

    private final NoteService noteService;

    NoteController(NoteService noteService) {

        this.noteService = noteService;
    }


    @RequestMapping(value = "/note", method = RequestMethod.POST)
    public Object updateNote(@RequestBody Note note) {
        if (noteService.updateNote(note) == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 根据label获取note
     *
     * @param label label
     * @return Note
     */
    @RequestMapping(value = "/note/{label}", method = RequestMethod.GET)
    public Note getNote(@PathVariable(value = "label") String label) {
        Note note = noteService.getLastNoteByLabel(label);
        if (note == null) {
            note = new Note();
            note.setLabel(label);
            note.setContent("{\"ops\":[{\"insert\":\"Hello World!\\n\"}]}");
            noteService.updateNote(note);
        }
        return note;
    }

    @GetMapping(value = "/getContent/{label}")
    public String getNoteContent(@PathVariable(value = "label") String label) {
        Delta delta = noteService.getNoteContent(label);
        return delta.toString();
    }

    @GetMapping(value = "/getServerDelta/{label}")
    public NoteDelta getServerDelta(@PathVariable(value = "label") String label) {
        return noteService.getServerDelta(label);
    }


    /**
     * 接收application/x-www-form-urlencoded和multipart/form-data格式的参数，可不用@RequestParam
     * 前端axios需要用qs.stringify处理一下
     *
     * @param content 内容
     */
    @PostMapping("/createNote")
    public void CreateNote(@RequestParam String content, @RequestParam String label) {
        noteService.createNote(content, label);

    }

    /**
     * 接受application/json格式参数，要用@RequestBody
     *
     * @param note 封装的对象
     */
    @PostMapping("/createNote2")
    public R<?> CreateNote(@RequestBody Note note) {
        noteService.createNote(note);
        return R.ok("创建成功");
    }

    @PostMapping("/deleteNote")
    public R<?> deleteNote(@RequestBody Note note) {
        noteService.deleteNote(note.getLabel());
        return R.ok();
    }
}

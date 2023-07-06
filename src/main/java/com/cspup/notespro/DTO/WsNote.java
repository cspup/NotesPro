package com.cspup.notespro.DTO;

import com.cspup.notespro.entity.Note;

import java.sql.Timestamp;

/**
 * @author csp
 * @date 2023/7/6 9:22
 * @description
 */
public class WsNote {
    private String wsId;
    private Note note;
    private long arrivalTime;

    public WsNote(){

    }

    public WsNote(String wsId, Note note, long arrivalTime){
        this.wsId = wsId;
        this.note = note;
        this.arrivalTime = arrivalTime;
    }

    public String getWsId() {
        return wsId;
    }

    public void setWsId(String wsId) {
        this.wsId = wsId;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(long arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}

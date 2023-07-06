package com.cspup.notespro.DTO;

/**
 * @author csp
 * @date 2023/7/5 15:08
 * @description
 */
public class NoteDTO {
    private Long id;
    private String label;
    private String content;

    private long logicTime;

    public String getLabel() {
        return label;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getLogicTime() {
        return logicTime;
    }

    public void setLogicTime(long logicTime) {
        this.logicTime = logicTime;
    }
}

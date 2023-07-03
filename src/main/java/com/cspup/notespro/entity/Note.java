package com.cspup.notespro.entity;

/**
 * @author csp
 * @date 2022/2/22 16:28
 * @description
 */
public class Note {
    private long id;
    private String createTime;
    private String updateTime;
    private String content;
    private String label;

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}

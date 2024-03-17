package com.cspup.notespro.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Operation {


    // 3种操作：插入、删除、保持
    enum Type {
        INSERT, DELETE, RETAIN
    }


    private Type type;
    private int position;
    private String content;

    private List<Operation> ops;


    public Operation(Type type, int position, String content) {
        this.type = type;
        this.position = position;
        this.content = content;
    }

    public Operation(Type type, int position) {
        this.type = type;
        this.position = position;
    }

    public Operation(Map<String, Object> map) {
        if (map.containsKey("retain")) {
            this.type = Type.RETAIN;
        } else if (map.containsKey("insert")) {
            this.type = Type.INSERT;
        } else if (map.containsKey("delete")) {
            this.type = Type.DELETE;
        }

//        this.type = type;
//        this.position = position;
//        this.content = content;
    }


    public Operation insert(String content){

        return this;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "type=" + type +
                ", position=" + position +
                ", content='" + content + '\'' +
                '}';
    }
}

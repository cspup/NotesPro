package com.cspup.notespro.delta;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * @author csp
 * @date 2024/3/15 21:18
 * @description 包含版本号、标签的Delta
 */

@JsonInclude(value = NON_NULL)
public class NoteDelta {
    // 全量标记，标记数据是文档的全量Delta还是操作序列
    @JsonProperty("full")
    boolean full = false;
    @JsonProperty("label")
    String label;
    @JsonProperty("version")
    long version;
    @JsonProperty("delta")
    Delta delta;

    public NoteDelta() {
    }

    public NoteDelta(String label, long version, Delta delta) {
        this.label = label;
        this.version = version;
        this.delta = delta;
    }

    public NoteDelta(boolean full,String label, long version, Delta delta) {
        this.full = full;
        this.label = label;
        this.version = version;
        this.delta = delta;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Delta getDelta() {
        return delta;
    }

    public void setDelta(Delta delta) {
        this.delta = delta;
    }

    public void setFull(boolean full) {
        this.full = full;
    }

    public boolean getFull(){
        return this.full;
    }


    public long increaseVersion() {
        return this.version++;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        try {
            return writer.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "Error while generating json:\n" + e.getMessage();
        }
    }
}

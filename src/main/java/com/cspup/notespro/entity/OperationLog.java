package com.cspup.notespro.entity;

/**
 * @author csp
 * @date 2024/3/16 17:11
 * @description
 */
public class OperationLog {
    private String label;
    private long version;
    private String op;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }
}

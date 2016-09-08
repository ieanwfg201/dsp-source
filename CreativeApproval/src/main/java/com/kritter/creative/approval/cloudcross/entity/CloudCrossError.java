package com.kritter.creative.approval.cloudcross.entity;

/**
 * Created by hamlin on 16-9-8.
 */
public class CloudCrossError {
    private Integer index;
    private Integer code;
    private String message;
    private String field;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}

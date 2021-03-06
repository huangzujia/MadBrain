package com.unipad.brain.absPic.bean;

/**
 * Created by gongkan on 2016/4/12.
 */
public class Figure {

    private String path;

    private int rawId;

    private int answerId;

    public Figure(String path, int rawId) {
        this.path = path;
        this.rawId = rawId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getRawId() {
        return rawId;
    }

    public void setRawId(int rawId) {
        this.rawId = rawId;
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }
}

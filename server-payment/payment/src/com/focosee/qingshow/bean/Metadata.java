package com.focosee.qingshow.bean;

public class Metadata {
    private int error;
    private Object devInfo;
    public int getError() {
        return error;
    }
    public void setError(int error) {
        this.error = error;
    }
    public Object getDevInfo() {
        return devInfo;
    }
    public void setDevInfo(Object message) {
        this.devInfo = message;
    }
}

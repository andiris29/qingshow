package com.focosee.qingshow.bean;

public class Metadata {
    private String error;
    private Object devInfo;
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
    public Object getDevInfo() {
        return devInfo;
    }
    public void setDevInfo(Object message) {
        this.devInfo = message;
    }
}

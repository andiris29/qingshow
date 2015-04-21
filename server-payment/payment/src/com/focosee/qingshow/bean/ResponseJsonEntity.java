package com.focosee.qingshow.bean;

public class ResponseJsonEntity {
    private Object data;
    private Metadata metadata;
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }
    public Metadata getMetadata() {
        return metadata;
    }
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }
}

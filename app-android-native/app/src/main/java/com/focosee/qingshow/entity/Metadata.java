package com.focosee.qingshow.entity;

public class Metadata {
    public int invalidateTime;
    public String error;
    public DevInfo devInfo;

    public class DevInfo {
        public String errorCode;
        public String description;
    }
}

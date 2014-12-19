package com.focosee.qingshow.entity;

// TODO ?
public class Metadata {
    public int invalidateTime;
    public int error;
    public DevInfo devInfo;

    public class DevInfo {
        public String errorCode;
        public String description;
    }
}

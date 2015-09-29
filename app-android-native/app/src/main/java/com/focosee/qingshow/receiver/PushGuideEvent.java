package com.focosee.qingshow.receiver;

/**
 * Created by Administrator on 2015/9/18.
 */
public class PushGuideEvent {

    public boolean unread = true;
    public String command;
    public String id;

    public PushGuideEvent(boolean unread, String command){
        this.unread = unread;
        this.command = command;
    }

}

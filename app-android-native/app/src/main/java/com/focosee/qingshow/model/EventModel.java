package com.focosee.qingshow.model;

/**
 * Created by Administrator on 2015/7/10.
 */
public class EventModel<T> {
    public String tag;
    public T msg;
    public String from;

    public EventModel(){}

    public EventModel(String tag, T msg){
        this.tag = tag;
        this.msg = msg;
    }

    public void setFrom(String from){
        this.from = from;
    }
}

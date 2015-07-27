package com.focosee.qingshow.model;

/**
 * Created by Administrator on 2015/7/10.
 */
public class EventModel<T> {
    public Class tag;
    public T msg;
    public Class from;

    public EventModel(){}

    public EventModel(Class tag, T msg){
        this.tag = tag;
        this.msg = msg;
    }

    public void setFrom(Class from){
        this.from = from;
    }
}

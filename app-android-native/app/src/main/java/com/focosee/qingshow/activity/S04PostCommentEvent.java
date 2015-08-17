package com.focosee.qingshow.activity;

/**
 * Created by Administrator on 2015/8/17.
 */
public class S04PostCommentEvent {

    public static final int addComment = 0;
    public static final int delComment = 1;

    public int action = 0;

    public S04PostCommentEvent(int action){
        this.action = action;
    }

}

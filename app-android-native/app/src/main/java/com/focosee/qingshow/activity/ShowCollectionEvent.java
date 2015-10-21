package com.focosee.qingshow.activity;

import com.focosee.qingshow.model.vo.mongo.MongoShow;

/**
 * Created by Administrator on 2015/8/19.
 */
public class ShowCollectionEvent {

    public MongoShow show;

    public ShowCollectionEvent(MongoShow show){
        this.show = show;
    }

}

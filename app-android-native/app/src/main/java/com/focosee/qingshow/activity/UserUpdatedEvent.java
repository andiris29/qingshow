package com.focosee.qingshow.activity;

import com.focosee.qingshow.model.vo.mongo.MongoPeople;

/**
 * Created by chen on 2015/8/29.
 */
public class UserUpdatedEvent {

    public MongoPeople user;

    public UserUpdatedEvent(MongoPeople user){
        this.user = user;
    }
}

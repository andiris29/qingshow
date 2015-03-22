package com.focosee.qingshow.activity.fragment;

import com.focosee.qingshow.model.vo.mongo.MongoOrder;

/**
 * Created by Administrator on 2015/3/17.
 */
public class S11DetailsEvent {

    private MongoOrder order;
    private boolean exists = false;

    public S11DetailsEvent(MongoOrder order, boolean exists) {
        this.order = order;
        this.exists = exists;
    }
    public MongoOrder getOrder() {
        return order;
    }

    public boolean isExists() {
        return exists;
    }
}

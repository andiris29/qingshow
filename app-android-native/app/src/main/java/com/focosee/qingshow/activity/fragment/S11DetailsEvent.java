package com.focosee.qingshow.activity.fragment;

import com.focosee.qingshow.model.vo.mongo.MongoOrder;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;

/**
 * Created by Administrator on 2015/3/17.
 */
public class S11DetailsEvent {

    private MongoOrder order;
    private boolean exists = false;
    private MongoPeople.MeasureInfo measureInfo;

    public S11DetailsEvent(MongoOrder order, boolean exists, MongoPeople.MeasureInfo measureInfo) {
        this.order = order;
        this.exists = exists;
        this.measureInfo = measureInfo;
    }
    public MongoOrder getOrder() {
        return order;
    }

    public boolean isExists() {
        return exists;
    }

    public MongoPeople.MeasureInfo getMeasureInfo() {
        return measureInfo;
    }
}

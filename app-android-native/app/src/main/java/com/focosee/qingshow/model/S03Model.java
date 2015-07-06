package com.focosee.qingshow.model;

import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoShow;

/**
 * Created by i068020 on 2/21/15.
 */
public enum S03Model {
    INSTANCE;

    private MongoShow show;

    public MongoShow getShow() {
        return show;
    }

    public void setShow(MongoShow _show) {
        this.show = _show;
    }
}

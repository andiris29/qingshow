package com.focosee.qingshow.model;

import com.focosee.qingshow.model.vo.mongo.MongoTrade;

/**
 * Created by i068020 on 2/21/15.
 */
public enum TradeModel {
    INSTANCE;

    private MongoTrade trade;

    public MongoTrade getTrade() {
        return trade;
    }

    public void setTrade(MongoTrade trade) {
        this.trade = trade;
    }
}

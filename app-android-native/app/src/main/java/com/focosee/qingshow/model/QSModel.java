package com.focosee.qingshow.model;

import com.focosee.qingshow.model.vo.mongo.MongoPeople;

/**
 * Created by i068020 on 2/21/15.
 */
public enum QSModel {
    INSTANCE;

    private MongoPeople user;

    public boolean loggedin() {
        return user != null;
    }

    public MongoPeople getUser() {
        return user;
    }

    public void setUser(MongoPeople _user) {
        this.user = _user;
    }
}

package com.focosee.qingshow.model;

import android.content.SharedPreferences;

import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;

/**
 * Created by i068020 on 2/21/15.
 */
public enum U01Model {
    INSTANCE;

    private MongoPeople user;

    public MongoPeople getUser() {
        return user;
    }

    public void setUser(MongoPeople _user) {
        this.user = _user;
    }
}

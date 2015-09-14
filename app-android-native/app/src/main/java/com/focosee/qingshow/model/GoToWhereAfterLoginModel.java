package com.focosee.qingshow.model;

import com.focosee.qingshow.activity.U01UserActivity;

/**
 * Created by Administrator on 2015/7/7.
 */
public enum GoToWhereAfterLoginModel {
    INSTANCE;

    private Class _class;

    public Class get_class() {
        return _class;
    }

    public void set_class(Class _class) {
        this._class = _class;
    }

}

package com.focosee.qingshow.model;

import android.content.SharedPreferences;

import com.focosee.qingshow.QSApplication;

/**
 * Created by Administrator on 2015/7/24.
 */
public enum PushModel {
    INSTANCE;

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        SharedPreferences.Editor editor = QSApplication.instance().getPreferences().edit();
        editor.putString("regId", regId);
        editor.commit();
        this.regId = regId;
    }

    private String regId;

}

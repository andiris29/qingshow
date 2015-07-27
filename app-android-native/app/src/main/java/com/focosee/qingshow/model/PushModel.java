package com.focosee.qingshow.model;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.focosee.qingshow.QSApplication;

/**
 * Created by Administrator on 2015/7/24.
 */
public enum PushModel {
    INSTANCE;
    public static final String KEYWORD = "reg_id";

    public String getRegId() {
        if (TextUtils.isEmpty(regId)) {
            return QSApplication.instance().getPreferences().getString(KEYWORD, "");
        }
        return regId;
    }

    public void setRegId(String regId) {
        SharedPreferences.Editor editor = QSApplication.instance().getPreferences().edit();
        editor.putString(KEYWORD, regId);
        editor.commit();
        this.regId = regId;
    }

    private String regId;

}

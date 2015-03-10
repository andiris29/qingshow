package com.focosee.qingshow.persist;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.focosee.qingshow.QSApplication;

/**
 * Created by i068020 on 2/24/15.
 */
public enum CookieSerializer {
    INSTANCE;

    private static final String KEYWORD = "qs-cookie";
    
    private SharedPreferences _preferences;
    
    CookieSerializer() {
        _preferences = PreferenceManager.getDefaultSharedPreferences(QSApplication.instance());
    }

    public void saveCookie(String cookie) {
        SharedPreferences.Editor editor = _preferences.edit();
        editor.putString(KEYWORD, cookie);
        editor.commit();
    }

    public String loadCookie() {
        return _preferences.getString(KEYWORD, "");
    }

}

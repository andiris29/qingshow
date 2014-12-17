package com.focosee.qingshow.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppUtil {

    public static String getAppUserName(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context) ;
        String userId = prefs.getString("id", null);
        return userId;
    }

    public static boolean getAppUserLoginStatus(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context) ;
        String userId = prefs.getString("id", null);
        return (null == userId) ? false : true;
    }
}

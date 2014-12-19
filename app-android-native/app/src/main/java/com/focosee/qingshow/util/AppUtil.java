package com.focosee.qingshow.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppUtil {

    // TODO
    public static String getAppUserName(Context context) {
        SharedPreferences prefs;// = PreferenceManager.getDefaultSharedPreferences(context) ;
        prefs = context.getSharedPreferences("personal", Context.MODE_PRIVATE);
        String userId = prefs.getString("id", null);
        return userId;
    }

    public static boolean getAppUserLoginStatus(Context context) {
        SharedPreferences prefs;// = PreferenceManager.getDefaultSharedPreferences(context) ;
        prefs = context.getSharedPreferences("personal", Context.MODE_PRIVATE);
        String userId = prefs.getString("id", null);
        return userId!=null;
    }
}

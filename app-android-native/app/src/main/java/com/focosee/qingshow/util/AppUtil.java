package com.focosee.qingshow.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class AppUtil {

    public static boolean getAppUserLoginStatus(Context context) {
        SharedPreferences prefs;// = PreferenceManager.getDefaultSharedPreferences(context) ;
        prefs = context.getSharedPreferences("personal", Context.MODE_PRIVATE);
        String userId = prefs.getString("id", null);
        return userId!=null;
    }

    public static String getAppUserId(Context context) {
        SharedPreferences preferences;
        preferences = context.getSharedPreferences("personal", Context.MODE_PRIVATE);
        return preferences.getString("_id", null);
    }

    public static String getAppVersionName(Context context)//获取版本号
    {
        try {
            PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "1.0.0";
        }
    }
}

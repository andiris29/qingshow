package com.focosee.qingshow.util.push;

import android.os.Bundle;
import android.util.Log;

import com.focosee.qingshow.constants.config.QSPushAPI;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2015/7/23.
 */
public class PushUtil {

    private static final String TAG = "JPush_QS";

    public static String getCommand(Bundle bundle){
        return getExtra(bundle,QSPushAPI.COMMAND);
    }

    public static String getId(Bundle bundle){
        return getExtra(bundle, QSPushAPI.ID);
    }

    public static String getExtra(Bundle bundle, String key) {

        String value = "";
        try {
            JSONObject extras = getExtras(bundle);
            value = extras.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static JSONObject getExtras(Bundle bundle) {
        JSONObject value = null;
        try {
            value = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }
}

package com.focosee.qingshow.command;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.activity.LaunchActivity;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.ValueUtil;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/9/17.
 */
public class SystemCommand {

    public static void systemLog(final JSONObject jsonObject){
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getSystemLogApi()
                , jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(MetadataParser.hasError(response)){
                    SharedPreferences.Editor editor = QSApplication.instance().getPreferences().edit();
                    editor.putString(ValueUtil.CRASH_LOG, jsonObject.toString());
                    editor.commit();
                }
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    public static void systemGet(final Handler handler, final Callback callback){

        String url = "http://chinshow.com/services/system/get?client=android&version=" + AppUtil.getVersion();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(LaunchActivity.class.getSimpleName(), "response:" + response);
                if(MetadataParser.hasError(response)){
                    ErrorHandler.handle(QSApplication.instance(), MetadataParser.getError(response));
                    return;
                }
                try {
                    QSAppWebAPI.HOST_ADDRESS_PAYMENT = response.getJSONObject("data").getJSONObject("deployment").getString("paymentServiceRoot");
                    QSAppWebAPI.HOST_ADDRESS_APPWEB = response.getJSONObject("data").getJSONObject("deployment").getString("appWebRoot");
                    SharedPreferences.Editor editor = QSApplication.instance().getPreferences().edit();
                    editor.putString(QSAppWebAPI.host_name, response.getJSONObject("data").getJSONObject("deployment").getString("appServiceRoot"));
//                    editor.putString(QSAppWebAPI.host_name, "http://192.168.1.110:30001/services");
//                    editor.putString(QSAppWebAPI.host_name, "http://chingshow.com/services");
                    editor.putString(QSAppWebAPI.host_address_payment, QSAppWebAPI.HOST_ADDRESS_PAYMENT);
                    editor.putString(QSAppWebAPI.host_address_appweb, QSAppWebAPI.HOST_ADDRESS_APPWEB);
                    editor.putBoolean(ValueUtil.UPDATE_APP_FORCE, false);
                    editor.commit();
                    callback.onComplete(response);
                    if(null == handler)return;
                    Message msg = new Message();
                    msg.what = LaunchActivity.SYSTEM_GET_FINISH;
                    handler.sendMessage(msg);
                } catch (JSONException e) {

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }
}

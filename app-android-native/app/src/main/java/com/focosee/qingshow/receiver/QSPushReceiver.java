package com.focosee.qingshow.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.activity.BaseActivity;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.constants.config.QSPushAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.model.PushModel;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.push.PushHepler;
import com.focosee.qingshow.util.push.PushUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/7/22.
 */
public class QSPushReceiver extends BroadcastReceiver {

    private static final String TAG = "JPush_QS";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
        String registrationId = JPushInterface.getRegistrationID(context);
        if (!TextUtils.isEmpty(registrationId)) {
            PushModel.INSTANCE.setRegId(registrationId);
            if (QSModel.INSTANCE.loggedin()) {
                Map<String, String> params = new HashMap<>();
                params.put("registrationId", registrationId);
                QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getUserUpdateregistrationidApi()
                        , new JSONObject(params), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                });
                RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
            }
        }
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            if (!regId.isEmpty()) {
                PushModel.INSTANCE.setRegId(registrationId);
            }
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
            //推送消息指引
            final String command = PushUtil.getCommand(bundle);
            if (command.equals(QSPushAPI.TRADE_INITIALIZED) || command.equals(QSPushAPI.TRADE_SHIPPED)
                    || command.equals(QSPushAPI.ITEM_EXPECTABLE_PRICEUPDATED) || command.equals(QSPushAPI.NEW_RECOMMANDATIONS)
                    || command.equals(QSPushAPI.NEW_BONUSES) || command.equals(QSPushAPI.NEW_PARTICIPANT_BONUS) || command.equals(QSPushAPI.BONUS_WITHDRAW_COMPLETE)) {
                UserCommand.refresh(new Callback() {
                    @Override
                    public void onComplete() {
                        EventBus.getDefault().post(new PushGuideEvent(true, command));
                    }
                });
            }

            if (AppUtil.isRunningForeground(context)) {
                Intent pushIntent = new Intent(BaseActivity.PUSHNOTIFY);
                pushIntent.putExtras(bundle);
                QSApplication.instance().sendBroadcast(pushIntent);
            }
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
            //打开自定义的Activity
            Intent i = PushHepler._jumpTo(context, bundle, JPushInterface.ACTION_NOTIFICATION_OPENED);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }


    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                try {
                    JSONObject json = new JSONObject(bundle.getString(key));
                    Log.d(TAG, json.get("command").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }


}

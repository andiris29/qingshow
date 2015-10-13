package com.focosee.qingshow.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import com.android.volley.Response;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.CategoriesCommand;
import com.focosee.qingshow.command.SystemCommand;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.ValueUtil;
import com.focosee.qingshow.util.exception.CrashHandler;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import cn.jpush.android.api.InstrumentedActivity;

public class LaunchActivity extends InstrumentedActivity {

    public static final int JUMP = 1;
    public static final int SYSTEM_GET_FINISH = 2;
    private Class _class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
        //友盟接口
        MobclickAgent.updateOnlineConfig(this);

        setContentView(R.layout.activity_launch);
        init();
        if(!AppUtil.checkNetWork(LaunchActivity.this)){//not net
            jump();
            return;
        }
        SystemCommand.systemGet(handler, new Callback());
    }

    private void init() {
        QSAppWebAPI.HOST_ADDRESS_PAYMENT = QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_address_payment, "");
        QSAppWebAPI.HOST_ADDRESS_APPWEB = QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_address_appweb, "");
        if (QSApplication.instance().getPreferences().getBoolean("isFirstLaunch", true)) {
            SharedPreferences.Editor editor = QSApplication.instance().getPreferences().edit();
            editor.putBoolean("isFirstLaunch", false);
            editor.commit();
            _class = G02WelcomeActivity.class;
        } else {
            _class = S01MatchShowsActivity.class;
        }
    }

    private void systemLog() {
        if (!TextUtils.isEmpty(QSApplication.instance().getPreferences().getString(ValueUtil.CRASH_LOG, ""))) {
            Gson gson = QSGsonFactory.create();
            CrashHandler.CrashModel crashModel = gson.fromJson(QSApplication.instance().getPreferences().getString(ValueUtil.CRASH_LOG, ""), CrashHandler.CrashModel.class);
            try {
                SystemCommand.systemLog(new JSONObject(gson.toJson(crashModel)));
            } catch (JSONException e) {
                Log.e(LaunchActivity.class.getSimpleName(), "systemLog:" + e.getMessage());
            }
        }
    }



    public void jump() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    Message msg = new Message();
                    msg.arg1 = JUMP;
                    handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.arg1 == JUMP) {
                Intent mainIntent = new Intent(LaunchActivity.this, _class);
                LaunchActivity.this.startActivity(mainIntent);
                LaunchActivity.this.finish();
            }
            if(msg.arg1 == SYSTEM_GET_FINISH){
                getUser();
                CategoriesCommand.getCategories();
                systemLog();
                jump();
            }
            return true;
        }
    });

    private void getUser() {
        if(TextUtils.isEmpty(QSApplication.instance().getPreferences().getString("id", "")))return;
        Log.d(LaunchActivity.class.getSimpleName(), "getUser");
        UserCommand.refresh(new Callback() {
            @Override
            public void onComplete() {
                super.onComplete();
            }

            @Override
            public void onError() {
                super.onError();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}

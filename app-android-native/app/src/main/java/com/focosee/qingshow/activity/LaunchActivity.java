package com.focosee.qingshow.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.WindowManager;

import com.android.volley.Response;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.util.AppUtil;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import cn.jpush.android.api.InstrumentedActivity;

public class LaunchActivity extends InstrumentedActivity {

    private Class _class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
        //友盟接口
        MobclickAgent.updateOnlineConfig(this);

        String  deviceUid = QSApplication.instance().getPreferences().getString("deviceUid", "");
        if ("".equals(deviceUid) || !deviceUid.equals(((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId())){
            userFollow();
            _class = G02WelcomeActivity.class;
        } else {
            _class = S01MatchShowsActivity.class;
        }

        String id = QSApplication.instance().getPreferences().getString("id", "");
        if(!"".equals(id)){
            MongoPeople _user = new MongoPeople();
            _user._id = id;
            QSModel.INSTANCE.setUser(_user);
        }

        setContentView(R.layout.activity_launch);

        UserCommand.refresh(new Callback() {
            @Override
            public void onComplete() {
                super.onComplete();
                // Bootstrap
                jump();
            }

            @Override
            public void onError() {
                super.onError();
                jump();
            }
        });
    }

    public void jump(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    handler.sendEmptyMessage(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Intent mainIntent = new Intent(LaunchActivity.this, _class);
            LaunchActivity.this.startActivity(mainIntent);
            LaunchActivity.this.finish();
            return true;
        }
    });

    private void userFollow(){
        Map params = new HashMap();
        params.put("version", AppUtil.getVersion());
        params.put("deviceUid", ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
        params.put("osType", 1);
        params.put("osVersion", android.os.Build.VERSION.RELEASE);
        JSONObject jsonObject = new JSONObject(params);
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getSpreadFirstlanuchApi(), jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(!MetadataParser.hasError(response)){
                    SharedPreferences.Editor editor = QSApplication.instance().getPreferences().edit();
                    editor.putString("deviceUid",
                            ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
                    editor.commit();
                }

            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
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

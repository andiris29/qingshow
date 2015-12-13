package com.focosee.qingshow.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import com.android.volley.Request;
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
import com.focosee.qingshow.httpapi.response.dataparser.UserParser;
import com.focosee.qingshow.httpapi.response.error.ErrorCode;
import com.focosee.qingshow.model.PushModel;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.FileUtil;
import com.focosee.qingshow.util.ValueUtil;
import com.focosee.qingshow.util.exception.CrashHandler;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import cn.jpush.android.api.InstrumentedActivity;
import de.greenrobot.event.EventBus;

public class LaunchActivity extends InstrumentedActivity {

    public static final int JUMP = 1;
    public static final int SYSTEM_GET_FINISH = 2;
    private Class _class = S01MatchShowsActivity.class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
        //友盟接口
        MobclickAgent.updateOnlineConfig(this);

        setContentView(R.layout.activity_launch);
        EventBus.getDefault().register(this);
        //init();
        if (!AppUtil.checkNetWork(LaunchActivity.this)) {//not net
            jump();
            return;
        }
        SystemCommand.systemGet(handler, new Callback());
    }

    private void init() {
        if (!QSModel.INSTANCE.isFinished(MongoPeople.FIRST_OPEN_APP)) {
            QSModel.INSTANCE.setUserStatus(MongoPeople.FIRST_OPEN_APP);
            _class = G02WelcomeActivity.class;
        } else if(!QSModel.INSTANCE.isFinished(MongoPeople.MATCH_FINISHED)){
            _class = S20MatcherActivity.class;
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
                    msg.what = JUMP;
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
            if(msg.what == ErrorCode.UnSupportVersion){
                SharedPreferences.Editor editor = QSApplication.instance().getPreferences().edit();
                editor.putBoolean(ValueUtil.UPDATE_APP_FORCE, true);
                editor.commit();
                jump();
                return true;
            }
            if (msg.what == JUMP) {
                Intent mainIntent = new Intent(LaunchActivity.this, _class);
                LaunchActivity.this.startActivity(mainIntent);
                LaunchActivity.this.finish();
                return true;
            }
            if (msg.what == SYSTEM_GET_FINISH) {
                if (!QSModel.INSTANCE.isFinished(MongoPeople.GET_GUEST_USER))
                    userLoginAsGuest();
                getUser();
                CategoriesCommand.getCategories(new Callback());
                systemLog();
                jump();
                return true;
            }
            return true;
        }
    });

    private void userLoginAsGuest() {

        Map<String, String> params = new HashMap<>();
        params.put("registrationId", PushModel.INSTANCE.getRegId());

        Log.d("userLoginAsGuest:", QSAppWebAPI.getUserLoginasguestApi());

        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getUserLoginasguestApi()
                , new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(LaunchActivity.class.getSimpleName(), "response-userLoginAsGuest:" + response);
                if(!MetadataParser.hasError(response)){
                    QSModel.INSTANCE.setUser(UserParser._parsePeople(response));
                    QSModel.INSTANCE.setUserStatus(MongoPeople.GET_GUEST_USER);

                    FileUtil.uploadDefaultPortrait(LaunchActivity.this);
                }
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

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

            }
        });
    }

    public void onEventMainThread(String event){
        if(ValueUtil.UPDATE_APP_EVENT.equals(event)){
            jump();
        }
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

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}

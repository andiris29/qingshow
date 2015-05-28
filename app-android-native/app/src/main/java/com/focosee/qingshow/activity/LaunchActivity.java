package com.focosee.qingshow.activity;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.WindowManager;

import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.umeng.analytics.MobclickAgent;

public class LaunchActivity extends BaseActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
        //友盟接口
        MobclickAgent.updateOnlineConfig(this);
        MobclickAgent.openActivityDurationTrack(false);

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
        Intent mainIntent = new Intent(LaunchActivity.this, G02WelcomeActivity.class);
        LaunchActivity.this.startActivity(mainIntent);
        LaunchActivity.this.finish();
    }

    @Override
    public void reconn() {

    }
}

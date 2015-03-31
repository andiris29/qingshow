package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.WindowManager;

import com.focosee.qingshow.R;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.UserCommand;
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


        setContentView(R.layout.activity_launch);

        UserCommand.refresh(new Callback() {
            @Override
            public void onComplete() {
                super.onComplete();
                // Bootstrap
                Intent mainIntent = new Intent(LaunchActivity.this, S12TopicListActivity.class);
                LaunchActivity.this.startActivity(mainIntent);
                LaunchActivity.this.finish();
            }

            @Override
            public void onError() {
                super.onError();
                Intent mainIntent = new Intent(LaunchActivity.this, S12TopicListActivity.class);
                LaunchActivity.this.startActivity(mainIntent);
                LaunchActivity.this.finish();
            }
        });

    }

    @Override
    public void reconn() {

    }
}

package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.focosee.qingshow.R;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.util.AppUtil;
import com.umeng.analytics.MobclickAgent;

public class LaunchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);

        MobclickAgent.updateOnlineConfig(this);
        MobclickAgent.openActivityDurationTrack(false);

        setContentView(R.layout.activity_launch);

        if(AppUtil.getAppUserLoginStatus(this)){
            QSApplication.get().refreshPeople(this);
        }

        new Handler().postDelayed(new Runnable() {
            public void run() {
                /* Create an Intent that will start the Main WordPress Activity. */
                Intent mainIntent = new Intent(LaunchActivity.this, S01HomeActivity.class);
                LaunchActivity.this.startActivity(mainIntent);
                LaunchActivity.this.finish();
            }
        }, 2900); //2900 for release
    }

}

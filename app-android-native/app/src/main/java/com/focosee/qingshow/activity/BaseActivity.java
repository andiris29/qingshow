package com.focosee.qingshow.activity;

import android.app.Activity;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 2015/2/5.
 */
public class BaseActivity extends Activity {

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SplashScreen"); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SplashScreen"); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }
}

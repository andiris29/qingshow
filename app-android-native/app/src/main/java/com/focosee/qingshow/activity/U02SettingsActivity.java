package com.focosee.qingshow.activity;

import android.content.Context;
import android.os.Bundle;

import com.focosee.qingshow.R;
import com.umeng.analytics.MobclickAgent;


public class U02SettingsActivity extends BaseActivity {
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        context = getApplicationContext();

        U02SettingsFragment settingsFragment = U02SettingsFragment.newIntance();
        getFragmentManager().beginTransaction().replace(R.id.settingsScrollView, settingsFragment, "settingsFragment").commit();
    }

    @Override
    public void reconn() {

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("U02UserSetting"); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("U02UserSetting"); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }
}

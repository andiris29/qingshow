package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.fragment.U02SettingsFragment;
import com.umeng.analytics.MobclickAgent;


public class U02SettingsActivity extends MenuActivity {
    private Context context;
    private U02SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        context = getApplicationContext();

        settingsFragment = U02SettingsFragment.newIntance();
        getFragmentManager().beginTransaction().replace(R.id.settingsScrollView, settingsFragment, "settingsFragment").commit();
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

    @Override
    public void onBackPressed() {
        if(null == settingsFragment)return;
        if(settingsFragment.onBackPressed())return;
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (settingsFragment.isMenuOpened()) settingsFragment.closeMenu();
            else settingsFragment.openMenu();
        }
        return true;
    }
}

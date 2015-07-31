package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.fragment.U02SelectExceptionFragment;
import com.focosee.qingshow.activity.fragment.U02SettingsFragment;
import com.focosee.qingshow.model.U02Model;
import com.umeng.analytics.MobclickAgent;

public class U02SettingsActivity extends BaseActivity {

    private U02SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingsFragment = U02SettingsFragment.newIntance();
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.push_right_in, 0, 0, 0).replace(R.id.settingsScrollView, settingsFragment, U02SettingsFragment.class.getSimpleName()).commit();
    }

    @Override
    public void reconn() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(U02Model.INSTANCE.get_class() == U02SettingsFragment.class) {
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                settingsFragment.menuSwitch();
            }
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                settingsFragment.menuSwitch();
            }
        }else{
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                U02Model.INSTANCE.set_class(U02SettingsFragment.class);
                settingsFragment = new U02SettingsFragment();
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.push_left_in, 0,0, 0).
                        replace(R.id.settingsScrollView, settingsFragment).commit();
            }
        }
        return true;
    }
}

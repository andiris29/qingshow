package com.focosee.qingshow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import com.focosee.qingshow.R;
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
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(U02Model.INSTANCE.get_class() == U02SettingsFragment.class || U02Model.INSTANCE.get_class() == null) {
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                settingsFragment.menuSwitch();
            }
            if(keyCode == KeyEvent.KEYCODE_BACK){
                if(settingsFragment.isMenuOpened()){
                    settingsFragment.closeMenu();
                }else{
                    Intent home = new Intent(Intent.ACTION_MAIN);
                    home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    home.addCategory(Intent.CATEGORY_HOME);
                    startActivity(home);
                }
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

    @Override
    protected void onDestroy() {
        U02Model.INSTANCE.set_class(null);
        super.onDestroy();
    }
}

package com.focosee.qingshow.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.focosee.qingshow.R;
import com.focosee.qingshow.model.GoToWhereAfterLoginModel;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.U01Model;
import com.focosee.qingshow.util.BitMapUtil;
import butterknife.InjectView;

public class MenuActivity extends BaseActivity implements View.OnClickListener{

    @InjectView(R.id.drawer)
    DrawerLayout drawer;
    @InjectView(R.id.navigation)
    LinearLayout navigation;
    @InjectView(R.id.blur)
    ImageView blur;
    @InjectView(R.id.context)
    ViewGroup right;
    @InjectView(R.id.s17_settting)
    ImageView settingBtn;

    private boolean isFirstFocus = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initDrawer() {
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawer,
                R.string.menu_open, R.string.menu_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                openMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                closeMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (slideOffset == 0.0) blur.setVisibility(View.INVISIBLE);
            }
        };
        drawer.setDrawerListener(drawerToggle);
    }

    public void menuSwitch(){
        if(isMenuOpened()){
            closeMenu();
        }else{
            openMenu();
        }
    }

    public void closeMenu() {
//        blur.setVisibility(View.INVISIBLE);
        drawer.closeDrawer(navigation);
    }

    public boolean isMenuOpened() {
        return drawer.isDrawerOpen(navigation);
    }

    public void openMenu() {

//        if (isFirstFocus) {
//            applyBlur();
//            if (Build.VERSION.SDK_INT > 16)
//                isFirstFocus = true;
//        }
//        blur.setVisibility(View.VISIBLE);

        navigation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        drawer.openDrawer(navigation);
    }

    @Override
    public void reconn() {

    }

    private void blur(Bitmap bkg) {
        Handler mHandler = new Handler() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void handleMessage(Message msg) {
                blur.setBackground(new BitmapDrawable(MenuActivity.this.getResources(),
                        (Bitmap) msg.obj));
                right.destroyDrawingCache();
            }
        };
        String str;
        Bitmap overlay = BitMapUtil.convertToBlur(bkg, this);
        Message msg = mHandler.obtainMessage(1, 1, 1, overlay);
        mHandler.sendMessage(msg);
    }

    private void applyBlur() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                right.setDrawingCacheEnabled(true);
                right.buildDrawingCache();
                Bitmap bitmap = right.getDrawingCache();

                blur(bitmap);
            }
        };
        thread.run();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isScrollbarFadingEnabled() && isMenuOpened())
            closeMenu();
        else
            super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (isMenuOpened()) closeMenu();
            else openMenu();
        }
        return true;
    }

    @Override
    public void onClick(View v) {

        closeMenu();
        if(v.getId() == R.id.navigation_btn_match){
            startActivity(new Intent(MenuActivity.this, S01MatchShowsActivity.class));
            return;
        }

        Class _class = null;
        switch (v.getId()) {
            case R.id.navigation_btn_good_match:
                _class = S21CategoryActivity.class;
                break;
            case R.id.u01_people:
                U01Model.INSTANCE.setUser(QSModel.INSTANCE.getUser());
                _class = U01UserActivity.class;
                break;
            case R.id.s17_settting:
                _class = U02SettingsActivity.class;
                break;
        }

        if(!QSModel.INSTANCE.loggedin()){
            Toast.makeText(this, R.string.need_login, Toast.LENGTH_SHORT).show();
            GoToWhereAfterLoginModel.INSTANCE.set_class(_class);
            startActivity(new Intent(MenuActivity.this, U07RegisterActivity.class));
            return;
        }

        startActivity(new Intent(MenuActivity.this, _class));
        finish();
    }
}

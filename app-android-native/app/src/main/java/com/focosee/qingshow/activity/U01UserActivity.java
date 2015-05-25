package com.focosee.qingshow.activity;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.U01PushAdapter;
import com.focosee.qingshow.model.vo.mongo.Bean;
import com.focosee.qingshow.util.BitMapUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class U01UserActivity extends BaseActivity {

    @InjectView(R.id.recycler)
    RecyclerView recyclerView;
    @InjectView(R.id.user_bg)
    ImageView userBg;

    @InjectView(R.id.drawer)
    DrawerLayout drawer;
    @InjectView(R.id.navigation)
    LinearLayout navigation;
    @InjectView(R.id.blur)
    ImageView blur;
    @InjectView(R.id.context)
    RelativeLayout right;

    private final float DAMP = 3.0f;

    private List<Bean> list;
    private U01PushAdapter adapter;

    private boolean isFirstFocus = true;

    public void reconn() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u01_base);
        ButterKnife.inject(this);
        initData();
        initDrawer();
        initRectcler();
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

    private void closeMenu() {
        blur.setVisibility(View.INVISIBLE);
        drawer.closeDrawer(navigation);
    }

    private boolean isMenuOpened() {
        return drawer.isDrawerOpen(navigation);
    }

    private void openMenu() {

        if (isFirstFocus) {
            applyBlur();
            if (Build.VERSION.SDK_INT > 16)
                isFirstFocus = true;
        }
        blur.setVisibility(View.VISIBLE);

        navigation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        drawer.openDrawer(navigation);
    }

    private void blur(Bitmap bkg) {
        Handler mHandler = new Handler() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void handleMessage(Message msg) {
                blur.setBackground(new BitmapDrawable(U01UserActivity.this.getResources(),
                        (Bitmap) msg.obj));
                right.destroyDrawingCache();
            }
        };
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


    private void initRectcler() {
        adapter = new U01PushAdapter(list, this,
                R.layout.item_u01_push, R.layout.item_u01_header);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (0 == position) {
                    return 2;
                }
                return 1;
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                float offset = recyclerView.computeVerticalScrollOffset();
                userBg.setY(-offset / DAMP);
            }
        });
    }

    private void initData() {
        list = new ArrayList<Bean>();

        for (int i = 0; i < 20; i++) {

            if (i % 2 != 0) {
                Bean b = new Bean();
                b.url = "http://trial01.focosee.com/demo6/1107a50100.jpg";
                b.text = "123";
                list.add(b);
            } else {
                Bean b = new Bean();
                b.url = "http://trial01.focosee.com/demo6/1211b60100.jpg";
                b.text = "456";
                list.add(b);
            }
        }
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
    protected void onDestroy() {
        super.onDestroy();
    }

}

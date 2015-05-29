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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S17TopAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.ShowParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.BitMapUtil;
import com.focosee.qingshow.util.ShowUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by DylanJiang on 15/4/30.
 */

public class S17TopShowsActivity extends BaseActivity{

    @InjectView(R.id.s17_recycler)
    RecyclerView recyclerView;

    @InjectView(R.id.title)
    TextView title;

    @InjectView(R.id.drawer)
    DrawerLayout drawer;
    @InjectView(R.id.navigation)
    LinearLayout navigation;
    @InjectView(R.id.blur)
    ImageView blur;
    @InjectView(R.id.context)
    LinearLayout right;
    private boolean isFirstFocus = true;

    private LinkedList<MongoShow> data;
    private S17TopAdapter adapter;

    @Override
    public void reconn() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s17_topshows);
        ButterKnife.inject(this);

        title.setText(R.string.s17_title_name);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new S17TopAdapter(new ArrayList<>(),this,R.layout.item_s17);
        recyclerView.setAdapter(adapter);
        getDataFormNet();
    }

    private void getDataFormNet(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(QSAppWebAPI.getTopApi(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(S17TopShowsActivity.this, MetadataParser.getError(response));
                    return;
                }
                data = ShowParser.parseQuery(response);
                adapter.addDataAtTop(ShowUtil.formentShow(data));
                adapter.notifyDataSetChanged();

            }
        },null);
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
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
            public boolean onTouch(View v, MotionEvent event) {
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
                blur.setBackground(new BitmapDrawable(S17TopShowsActivity.this.getResources(),
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
}

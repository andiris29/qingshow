package com.focosee.qingshow.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.fragment.U13PersonalizeFragment;
import com.focosee.qingshow.adapter.S15Adapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.ChosenParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.IMongoChosen;
import com.focosee.qingshow.util.BitMapUtil;
import com.focosee.qingshow.widget.MPullRefreshListView;
import com.focosee.qingshow.widget.PullToRefreshBase;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by Administrator on 2015/3/27.
 */
public class S15ChosenActivity extends BaseActivity implements View.OnClickListener {

    private ImageView _blurImage;
    private DrawerLayout spl;
    private RelativeLayout _mFrmLeft;
    private LinearLayout right;
    private ActionBarDrawerToggle drawerbar;
    private MPullRefreshListView pullRefreshView;
    private ListView refreshView;
    private S15Adapter adapter;

    private Timer timer = new Timer(true);
    private TimerTask timerTask;
    private long time = 2000;
    private int count = 0;

    private boolean isFirstFocus_activity = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s12_topiclist);
        init();
    }

    private void init() {
        _blurImage = (ImageView) findViewById(R.id.s12_switch_right_background);
        spl = (DrawerLayout) findViewById(R.id.s12_sliding_layout);
        _mFrmLeft = (RelativeLayout) findViewById(R.id.s12_FrameLa_left);
        right = (LinearLayout) findViewById(R.id.s12_show_relative);
        pullRefreshView = (MPullRefreshListView) findViewById(R.id.s12_waterfall_content);
        refreshView = pullRefreshView.getRefreshableView();
        refreshView.setDividerHeight(0);

        adapter = new S15Adapter(this);
        refreshView.setAdapter(adapter);
        initEvent();

        getDataFromNet(true);

        pullRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getDataFromNet(true);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getDataFromNet(false);
            }
        });
    }

    @Override
    public void reconn() {

    }

    private void getDataFromNet(final boolean refresh) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(QSAppWebAPI.getChosenApi("0"), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    if (refresh)
                        pullRefreshView.onPullDownRefreshComplete();
                    else
                        pullRefreshView.onPullUpRefreshComplete();

                    pullRefreshView.setHasMoreData(false);
                    ErrorHandler.handle(S15ChosenActivity.this, MetadataParser.getError(response));
                }

                Log.d("tag",response + "");
                LinkedList<IMongoChosen> data = ChosenParser.parse(response);
                if (refresh){
                    adapter.addItemTop(data);
                    adapter.notifyDataSetChanged();
                    pullRefreshView.onPullDownRefreshComplete();
                }else {
                    adapter.addItemLast(data);
                    adapter.notifyDataSetChanged();
                    pullRefreshView.onPullUpRefreshComplete();
                }

                pullRefreshView.setHasMoreData(true);
            }
        }, null);
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void closeMenu() {
        _blurImage.setVisibility(View.INVISIBLE);
        spl.closeDrawer(_mFrmLeft);
    }

    private boolean isMenuOpened() {
        return spl.isDrawerOpen(_mFrmLeft);
    }

    private void openMenu() {

        if (isFirstFocus_activity) {
            applyBlur();
            if (Build.VERSION.SDK_INT > 16)
                isFirstFocus_activity = true;
        }
        _blurImage.setVisibility(View.VISIBLE);

        _mFrmLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        spl.openDrawer(_mFrmLeft);
    }

    private void blur(Bitmap bkg) {
        Handler mHandler = new Handler() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void handleMessage(Message msg) {
                _blurImage.setBackground(new BitmapDrawable(S15ChosenActivity.this.getResources(), (Bitmap) msg.obj));
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

    private void initEvent() {
        drawerbar = new ActionBarDrawerToggle(this, spl, R.drawable.root_cell_placehold_image1, R.string.menu_open, R.string.menu_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                pullRefreshView.setClickable(false);
                refreshView.setClickable(false);
                openMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                pullRefreshView.setClickable(true);
                refreshView.setClickable(true);
                closeMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (slideOffset == 0.0) _blurImage.setVisibility(View.INVISIBLE);
            }
        };
        spl.setDrawerListener(drawerbar);
    }

    private void showVerison() {

        ++count;
        if (null != timerTask) {
            timerTask.cancel();
        }
        timerTask = new TimerTask() {
            @Override
            public void run() {
                count = 0;
            }
        };
        timer.schedule(timerTask, time / 5);
        String version = "";
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (count == 5)
            showDialag(version);
    }


    private void showDialag(String msg) {
        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.version_dialag, null);
        TextView textView = (TextView) linearLayout.findViewById(R.id.version);
        textView.setText(msg);
        final MaterialDialog materialDialog = new MaterialDialog(this);

        materialDialog.setContentView(linearLayout)
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void onBackPressed() {
        if (spl.isScrollbarFadingEnabled() && isMenuOpened())
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

    public void showPersonalize(){
        Log.i("tag","show");
        getSupportFragmentManager().beginTransaction()
                .add(R.id.u13,new U13PersonalizeFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.s12_nav_icon_flash:
                intent = new Intent(S15ChosenActivity.this, S02ShowClassify.class);
                intent.putExtra(S02ShowClassify.INPUT_CATEGORY, 0);
                startActivity(intent);
                closeMenu();
                break;
            case R.id.s12_nav_icon_match:
                intent = new Intent(S15ChosenActivity.this, S17TopShowsActivity.class);
                intent.putExtra(S02ShowClassify.INPUT_CATEGORY, 1);
                startActivity(intent);
                closeMenu();
                break;
//            case R.id.s12_nav_icon_people:
//                intent = new Intent(S15ChosenActivity.this, P01ModelListActivity.class);
//                intent.putExtra(S02ShowClassify.INPUT_CATEGORY, 2);
//                startActivity(intent);
//                closeMenu();
//                break;
            case R.id.s12_nav_icon_design:
                intent = new Intent(S15ChosenActivity.this, S08TrendActivity.class);
                intent.putExtra(S02ShowClassify.INPUT_CATEGORY, 5);
                startActivity(intent);
                closeMenu();
                break;
//            case R.id.s12_nav_icon_brand:
//                intent = new Intent(S15ChosenActivity.this, P03BrandListActivity.class);
//                intent.putExtra(S02ShowClassify.INPUT_CATEGORY, 4);
//                startActivity(intent);
//                closeMenu();
//                break;
            case R.id.s12_logo:
                showPersonalize();
//                showVerison();
                break;
            case R.id.s12_menu_button:
                if (isMenuOpened()) {
                    closeMenu();
                } else {
                    openMenu();
                }
                break;
            case R.id.s12_user:
                intent = new Intent(S15ChosenActivity.this,
                        (QSModel.INSTANCE.loggedin())
                                ? U01UserActivity.class : U07RegisterActivity.class);

                startActivity(intent);
                break;
        }
    }

}

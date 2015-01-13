package com.focosee.qingshow.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.HomeWaterfallAdapter;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.entity.People;
import com.focosee.qingshow.entity.ShowListEntity;
import com.focosee.qingshow.request.MJsonObjectRequest;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.BitMapUtil;
import com.focosee.qingshow.widget.MPullRefreshMultiColumnListView;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.huewu.pla.lib.MultiColumnListView;
import com.huewu.pla.lib.internal.PLA_AbsListView;
import com.huewu.pla.lib.internal.PLA_AdapterView;
import com.nostra13.universalimageloader.core.ImageLoader;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class S01HomeActivity extends Activity {
    //    private MNavigationView _navigationView;
    private boolean isFirstFocus_activity = true;
    private final static String S01_TAG = "S01HomeActivity";
    private LinearLayout _popView;
    //抽屉对象
    private RelativeLayout _mFrmRight;
    private RelativeLayout _mFrmLeft;
    private ActionBarDrawerToggle drawerbar;
    private DrawerLayout spl;

    private MPullRefreshMultiColumnListView _wfPullRefreshView;
    private MultiColumnListView _wfListView;
    private HomeWaterfallAdapter _adapter;
    private int _currentPageIndex = 1;
    private RelativeLayout relativeLayout_right_fragment;

    private ImageView _blurImage;
    private ImageView _accountImage;

    private SimpleDateFormat _mDateFormat = new SimpleDateFormat("MM-dd HH:mm");

    private Handler mHandler = new Handler() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void handleMessage(Message msg) {
            _blurImage.setBackground(new BitmapDrawable(S01HomeActivity.this.getResources(), (Bitmap) msg.obj));
            relativeLayout_right_fragment.destroyDrawingCache();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s01_home);

        spl = (DrawerLayout) S01HomeActivity.this
                .findViewById(R.id.s01_sliding_layout);

        initMenu();


        relativeLayout_right_fragment = (RelativeLayout) findViewById(R.id.s01_show_relative);

        _blurImage = (ImageView) findViewById(R.id.s01_switch_right_background);
        _mFrmRight = (RelativeLayout) findViewById(R.id.s01_FrameLa_right);
        _mFrmLeft = (RelativeLayout) findViewById(R.id.s01_FrameLa_left);
        _wfPullRefreshView = (MPullRefreshMultiColumnListView) findViewById(R.id.S01_waterfall_content);
        _wfListView = _wfPullRefreshView.getRefreshableView();
        _adapter = new HomeWaterfallAdapter(this, R.layout.item_showlist, ImageLoader.getInstance());

        _wfListView.setAdapter(_adapter);

        _wfListView.setOnScrollListener(new PLA_AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(PLA_AbsListView view, int scrollState) {
                if(Build.VERSION.SDK_INT > 16) {
                    if (scrollState == PLA_AbsListView.OnScrollListener.SCROLL_STATE_IDLE)
                        applyBlur();
                }
            }

            @Override
            public void onScroll(PLA_AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        _wfPullRefreshView.setPullLoadEnabled(true);
        _wfPullRefreshView.setScrollLoadEnabled(true);

        _wfPullRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<MultiColumnListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<MultiColumnListView> refreshView) {
                doRefreshTask();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<MultiColumnListView> refreshView) {
                doGetMoreTask();
            }


        });

        _wfListView.setOnItemClickListener(new PLA_AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(PLA_AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) return;
                Intent intent = new Intent(S01HomeActivity.this, S03SHowActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(S03SHowActivity.INPUT_SHOW_ENTITY_ID, _adapter.getItemDataAtIndex(position)._id);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        setLastUpdateTime();

        _wfPullRefreshView.doPullRefreshing(true, 500);

        initEvent();

    }

    private void closeMenu() {
        _blurImage.setVisibility(View.INVISIBLE);
        spl.closeDrawer(_mFrmLeft);
    }

    private boolean isMenuOpened() {
        return spl.isDrawerOpen(_mFrmLeft);
    }

    private void openMenu() {
        if(isFirstFocus_activity){
            applyBlur();
            if(Build.VERSION.SDK_INT > 16)
                isFirstFocus_activity = false;
        }
        _blurImage.setVisibility(View.VISIBLE);

        spl.openDrawer(_mFrmLeft);
    }

    private void initEvent() {
        drawerbar = new ActionBarDrawerToggle(this, spl, R.drawable.ic_launcher, R.string.menu_open, R.string.menu_close) {
            private int _tag = 0;

            //菜单打开
            @Override
            public void onDrawerOpened(View drawerView) {

                openMenu();
            }

            // 菜单关闭
            @Override
            public void onDrawerClosed(View drawerView) {

                closeMenu();

                _tag = 0;
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if(slideOffset == 0.0) _blurImage.setVisibility(View.INVISIBLE);
            }
        };
        spl.setDrawerListener(drawerbar);
    }


    @Override
    public void onBackPressed() {
        if (spl.isScrollbarFadingEnabled() && isMenuOpened())
            closeMenu();
        else
            super.onBackPressed();
    }

    private void setLastUpdateTime() {
        String text = formatDateTime(System.currentTimeMillis());
        _wfPullRefreshView.setLastUpdatedLabel(text);
        _adapter.resetUpdateString();
    }

    private String formatDateTime(long time) {
        if (0 == time) {
            return "";
        }
        return _mDateFormat.format(new Date(time));
    }

    private void doRefreshTask() {
        _getDataFromNet(true, "1", "10");
    }

    private void doGetMoreTask() {
        _getDataFromNet(false, String.valueOf(_currentPageIndex + 1), "10");
    }

    private void _getDataFromNet(boolean refreshSign, String pageNo, String pageSize) {
        final boolean _tRefreshSign = refreshSign;
        MJsonObjectRequest jor = new MJsonObjectRequest(QSAppWebAPI.getShowListApi(Integer.valueOf(pageNo), Integer.valueOf(pageSize)), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    LinkedList<ShowListEntity> results = ShowListEntity.getShowListFromResponse(response);
                    if (_tRefreshSign) {
                        _adapter.addItemTop(results);
                        _currentPageIndex = 1;
                    } else {
                        _adapter.addItemLast(results);
                        _currentPageIndex++;
                    }
                    _adapter.notifyDataSetChanged();
                    _wfPullRefreshView.onPullDownRefreshComplete();
                    _wfPullRefreshView.onPullUpRefreshComplete();
                    _wfPullRefreshView.setHasMoreData(true);
                    setLastUpdateTime();

                } catch (Exception error) {
                    Log.i("test", "error" + error.toString());
                    Toast.makeText(S01HomeActivity.this, "Error:" + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    _wfPullRefreshView.onPullDownRefreshComplete();
                    _wfPullRefreshView.onPullUpRefreshComplete();
                    _wfPullRefreshView.setHasMoreData(true);
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(S01HomeActivity.this, "Error:" + error.toString(), Toast.LENGTH_SHORT).show();
                _wfPullRefreshView.onPullDownRefreshComplete();
                _wfPullRefreshView.onPullUpRefreshComplete();
                _wfPullRefreshView.setHasMoreData(true);
            }
        });
        QSApplication.get().QSRequestQueue().add(jor);
    }


    //模糊处理
    private void blur(Bitmap bkg) {

        Bitmap overlay = BitMapUtil.convertToBlur(bkg, this);
        //向主线程发消息
        Message msg = mHandler.obtainMessage(1, 1, 1, overlay);
        mHandler.sendMessage(msg);
    }

    Thread thread = new Thread() {

        @Override
        public void run() {
            //截屏
            relativeLayout_right_fragment.setDrawingCacheEnabled(true);
            relativeLayout_right_fragment.buildDrawingCache();
            Bitmap bitmap = relativeLayout_right_fragment.getDrawingCache();

            blur(bitmap);
        }
    };

    //进行模糊处理
    private void applyBlur() { thread.run(); }

    // init menu
    private void initMenu() {
        // TODO: Improve code to add menu here
        ((ImageView) findViewById(R.id.S01_nav_icon_flash)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(S01HomeActivity.this, S02ShowClassify.class);
                intent.putExtra(S02ShowClassify.INPUT_CATEGORY, 0);
                startActivity(intent);
                closeMenu();
            }
        });
        ((ImageView) findViewById(R.id.S01_nav_icon_match)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(S01HomeActivity.this, S02ShowClassify.class);
                intent.putExtra(S02ShowClassify.INPUT_CATEGORY, 1);
                startActivity(intent);
                closeMenu();
            }
        });
        ((ImageView) findViewById(R.id.S01_nav_icon_people)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(S01HomeActivity.this, P01ModelListActivity.class);
                intent.putExtra(S02ShowClassify.INPUT_CATEGORY, 2);
                startActivity(intent);
                closeMenu();
            }
        });
        ((ImageView) findViewById(R.id.S01_nav_icon_design)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(S01HomeActivity.this, S08TrendActivity.class);
                intent.putExtra(S02ShowClassify.INPUT_CATEGORY, 5);
                startActivity(intent);
                closeMenu();
            }
        });
        ((ImageView) findViewById(R.id.S01_nav_icon_brand)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(S01HomeActivity.this, P03BrandListActivity.class);
                intent.putExtra(S02ShowClassify.INPUT_CATEGORY, 4);
                startActivity(intent);
                closeMenu();

            }
        });

        _popView = (LinearLayout) findViewById(R.id.S01_pop_menu);

        ((ImageView) findViewById(R.id.S01_title_menu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMenuOpened()) {
                    closeMenu();
                } else {
                    openMenu();
                }
            }
        });

        _accountImage = (ImageView) findViewById(R.id.S01_title_account);

        _accountImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(S01HomeActivity.this,
                        (AppUtil.getAppUserLoginStatus(S01HomeActivity.this))
                                ? U01PersonalActivity.class : U06LoginActivity.class);

                startActivity(intent);
            }
        });
    }
    //获得用户图标ID（根据用户性格进行选择）
    private int getAccountImgId(){
        People people = QSApplication.get().getPeople();
        Log.d(S01_TAG, people+"sex");
        if(null != people && 1 == people.gender)
            return R.drawable.nav_account_btn_woman;
        return R.drawable.nav_btn_account_man;
    }




}

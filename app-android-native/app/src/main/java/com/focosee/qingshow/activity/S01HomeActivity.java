package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
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
import com.focosee.qingshow.entity.ShowListEntity;
import com.focosee.qingshow.request.MJsonObjectRequest;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.BlurBehindUtil;
import com.focosee.qingshow.widget.MPullRefreshMultiColumnListView;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.huewu.pla.lib.MultiColumnListView;
import com.huewu.pla.lib.internal.PLA_AdapterView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class S01HomeActivity extends Activity {

//    private MNavigationView _navigationView;
    private LinearLayout _popView;
    private SlidingPaneLayout spl;
    private MPullRefreshMultiColumnListView _wfPullRefreshView;
    private MultiColumnListView _wfListView;
    private HomeWaterfallAdapter _adapter;
    private int _currentPageIndex = 1;

    private SimpleDateFormat _mDateFormat = new SimpleDateFormat("MM-dd HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s01_home);

        spl = (SlidingPaneLayout) S01HomeActivity.this
                .findViewById(R.id.s01_sliding_layout);
        spl.setPanelSlideListener(new PanelSlideListener());

//        _navigationView = (MNavigationView) findViewById(R.id.S01_navigation);
//        _navigationView.setLeft_drawable(R.drawable.nav_btn_menu);
//        _navigationView.setRight_drawable(R.drawable.nav_btn_account);
//        _navigationView.setLogo_drawable(R.drawable.nav_btn_image_logo);

        // TODO: Improve code to add menu here
        ((ImageView)findViewById(R.id.S01_nav_icon_flash)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(S01HomeActivity.this, S02ShowClassify.class);
                intent.putExtra(S02ShowClassify.INPUT_CATEGORY, 0);
                startActivity(intent);
                spl.closePane();
                //_popView.setVisibility(View.GONE);
            }
        });
        ((ImageView)findViewById(R.id.S01_nav_icon_match)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(S01HomeActivity.this, S02ShowClassify.class);
                intent.putExtra(S02ShowClassify.INPUT_CATEGORY, 1);
                startActivity(intent);
                spl.closePane();
                //_popView.setVisibility(View.GONE);
            }
        });
        ((ImageView)findViewById(R.id.S01_nav_icon_people)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(S01HomeActivity.this, P01ModelListActivity.class);
                intent.putExtra(S02ShowClassify.INPUT_CATEGORY, 2);
                startActivity(intent);
                spl.closePane();
                //_popView.setVisibility(View.GONE);
            }
        });
        ((ImageView)findViewById(R.id.S01_nav_icon_design)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(S01HomeActivity.this, S02ShowClassify.class);
                intent.putExtra(S02ShowClassify.INPUT_CATEGORY, 3);
                startActivity(intent);
                _popView.setVisibility(View.GONE);*/
                //author:Chenhr
                Intent intent = new Intent(S01HomeActivity.this, S08TrendActivity.class);
                intent.putExtra(S02ShowClassify.INPUT_CATEGORY, 5);
                startActivity(intent);
                spl.closePane();
                //_popView.setVisibility(View.GONE);
            }
        });
        ((ImageView)findViewById(R.id.S01_nav_icon_brand)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(S01HomeActivity.this, P03BrandListActivity.class);
                intent.putExtra(S02ShowClassify.INPUT_CATEGORY, 4);
                startActivity(intent);
                spl.closePane();
                //_popView.setVisibility(View.GONE);

            }
        });

        _popView = (LinearLayout) findViewById(R.id.S01_pop_menu);


        ((ImageView)findViewById(R.id.S01_title_menu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spl.isOpen()) {
                    spl.closePane();
                } else {
                    spl.openPane();
                    //BlurBehindUtil.getInstance().withAlpha(80)
                            //.withFilterColor(Color.parseColor("#0075c0"))
                            //.setBackground(S01HomeActivity.this);
                }
            }
        });
        ((ImageView)findViewById(R.id.S01_title_account)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(S01HomeActivity.this,
                        (AppUtil.getAppUserLoginStatus(S01HomeActivity.this))
                                ? U01PersonalActivity.class : U06LoginActivity.class);

//                Intent intent = new Intent(S01HomeActivity.this, U06LoginActivity.class);
                startActivity(intent);
            }
        });

        _wfPullRefreshView = (MPullRefreshMultiColumnListView) findViewById(R.id.S01_waterfall_content);
        _wfListView = _wfPullRefreshView.getRefreshableView();
        _adapter = new HomeWaterfallAdapter(this, R.layout.item_showlist, ImageLoader.getInstance());

        _wfListView.setAdapter(_adapter);

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
                Intent intent = new Intent(S01HomeActivity.this, S03SHowActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(S03SHowActivity.INPUT_SHOW_ENTITY_ID, _adapter.getItemDataAtIndex(position)._id);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        setLastUpdateTime();

        _wfPullRefreshView.doPullRefreshing(true, 500);

    }

    @Override
    public void onBackPressed() {
        if(spl.isSlideable() && spl.isOpen())
            spl.closePane();
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
        _getDataFromNet(false, String.valueOf(_currentPageIndex+1), "10");
    }

    private void _getDataFromNet(boolean refreshSign, String pageNo, String pageSize) {
        final boolean _tRefreshSign = refreshSign;
        MJsonObjectRequest jor = new MJsonObjectRequest(QSAppWebAPI.getShowListApi(Integer.valueOf(pageNo), Integer.valueOf(pageSize)), null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try{
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

                }catch (Exception error){
                    Log.i("test", "error" + error.toString());
                    Toast.makeText(S01HomeActivity.this, "Error:"+error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    _wfPullRefreshView.onPullDownRefreshComplete();
                    _wfPullRefreshView.onPullUpRefreshComplete();
                    _wfPullRefreshView.setHasMoreData(true);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(S01HomeActivity.this, "Error:"+error.toString(), Toast.LENGTH_SHORT).show();
                _wfPullRefreshView.onPullDownRefreshComplete();
                _wfPullRefreshView.onPullUpRefreshComplete();
                _wfPullRefreshView.setHasMoreData(true);
            }
        });
        QSApplication.get().QSRequestQueue().add(jor);
    }

    class PanelSlideListener implements android.support.v4.widget.SlidingPaneLayout.PanelSlideListener {
        @Override
        public void onPanelClosed(View view) {
            S01HomeActivity.this.closeOptionsMenu();
        }

        @Override
        public void onPanelOpened(View viw) {
            S01HomeActivity.this.openOptionsMenu();
            //getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
            //RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.s01_show_relative);

        }

        @Override
        public void onPanelSlide(View arg0, float arg1) {

        }
    }

}

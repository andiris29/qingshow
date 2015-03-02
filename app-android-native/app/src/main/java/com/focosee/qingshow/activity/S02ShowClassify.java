package com.focosee.qingshow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.AbsWaterfallAdapter;
import com.focosee.qingshow.adapter.HotWaterfallAdapter;
import com.focosee.qingshow.adapter.S02ItemRandomAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.response.dataparser.FeedingParser;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.dataparser.ItemRandomParser;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.widget.MNavigationView;
import com.focosee.qingshow.widget.MPullRefreshMultiColumnListView;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.huewu.pla.lib.MultiColumnListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class S02ShowClassify extends BaseActivity {

    static class ShowClassifyConfig {
        private static final String[] titleList = {"闪点推荐", "美搭榜单", "人气用户", "潮流时尚", "品牌专区"};

        public static String getTitle(int mod) {
            return (titleList.length > mod) ? titleList[mod] : "参数错误";
        }
    }

    public static final String INPUT_CATEGORY = "INPUT_CATEGORY";

    private MNavigationView _navigationView;
    private MPullRefreshMultiColumnListView _pullRefreshListView;
    private MultiColumnListView _waterfallListView;
    private AbsWaterfallAdapter _adapter;

    private SimpleDateFormat _mDateFormat = new SimpleDateFormat("MM-dd HH:mm");
    private int _currentPageIndex = 1;
    private int classifyMod = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s02_show_classify);

        Intent intent = getIntent();
        classifyMod = intent.getIntExtra(S02ShowClassify.INPUT_CATEGORY, 0);

        _navigationView = (MNavigationView) findViewById(R.id.S02_nav_bar);
        _pullRefreshListView = (MPullRefreshMultiColumnListView) findViewById(R.id.S02_waterfall_content);

        _navigationView.getTv_title().setText(ShowClassifyConfig.getTitle(classifyMod));
        _navigationView.getBtn_left().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                S02ShowClassify.this.finish();
            }
        });
        _navigationView.getBtn_right().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(S02ShowClassify.this, U06LoginActivity.class);
                startActivity(intent);
            }
        });


        _navigationView.getBtn_right().setVisibility(View.INVISIBLE);
        _waterfallListView = _pullRefreshListView.getRefreshableView();

        switch (classifyMod){
            case 0:
                _adapter = new S02ItemRandomAdapter(this, R.layout.item_randomlist, ImageLoader.getInstance());
                break;
            case 1:
                _adapter = new HotWaterfallAdapter(this, R.layout.item_showlist, ImageLoader.getInstance());
                break;
        }

        _waterfallListView.setAdapter(_adapter);

        _pullRefreshListView.setPullLoadEnabled(true);
        _pullRefreshListView.setScrollLoadEnabled(true);

        _pullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<MultiColumnListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<MultiColumnListView> refreshView) {
                doRefreshTask();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<MultiColumnListView> refreshView) {
                doGetMoreTask();
            }
        });

        _pullRefreshListView.doPullRefreshing(true, 500);
    }

    @Override
    public void reconn() {
        doRefreshTask();
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
        String url = "";
        switch (classifyMod){
            case 0:
                url = QSAppWebAPI.getitemRandomApi(Integer.valueOf(pageNo), Integer.valueOf(20));
                break;
            case 1:
                url = QSAppWebAPI.getShowHotApi(Integer.valueOf(pageNo), Integer.valueOf(pageSize));
                break;
        }
        QSJsonObjectRequest jor = new QSJsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                    LinkedList results = null;
                    switch (classifyMod){
                        case 0:
                             results = ItemRandomParser.parse(response);
                            break;
                        case 1:
                             results = FeedingParser.parse(response);
                            break;
                    }
                    if (_tRefreshSign) {
                        _adapter.addItemTop(results);
                        _currentPageIndex = 1;
                    } else {
                        _adapter.addItemLast(results);
                        _currentPageIndex++;
                    }
                    _adapter.notifyDataSetChanged();
                    _pullRefreshListView.onPullDownRefreshComplete();
                    _pullRefreshListView.onPullUpRefreshComplete();
                    _pullRefreshListView.setHasMoreData(true);
                    setLastUpdateTime(response);
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jor);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_s02_show_classify, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("S02"); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("S02"); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }

    private void setLastUpdateTime(JSONObject response) {
        String text = formatDateTime(System.currentTimeMillis());
        _pullRefreshListView.setLastUpdatedLabel(text);
        _adapter.refreshDate(response);
    }

}

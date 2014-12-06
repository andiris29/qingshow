package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.ClassifyWaterfallAdapter;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.entity.ShowEntity;
import com.focosee.qingshow.widget.MNavigationView;
import com.focosee.qingshow.widget.MPullRefreshMultiColumnListView;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.huewu.pla.lib.MultiColumnListView;
import com.huewu.pla.lib.internal.PLA_AdapterView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;


class ShowClassifyConfig {
    private static final String[] titleList = {"闪点推荐","美搭榜单", "人气用户", "设计风尚", "品牌专区"};

    public static String getTitle(int mod) {
        return (titleList.length > mod) ? titleList[mod] : "参数错误";
    }

    public static String getApi(int mod, int pageNo, int pageSize) {
        return QSAppWebAPI.getShowCategoryListApi(mod, pageNo, pageSize);
    }
}

public class S02ShowClassify extends Activity {

    public static final String INPUT_CATEGORY = "sfdsjflkasd";

    private MNavigationView _navigationView;
    private MPullRefreshMultiColumnListView _pullRefreshListView;
    private MultiColumnListView _waterfallListView;
    private ClassifyWaterfallAdapter _adapter;

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

        _waterfallListView = _pullRefreshListView.getRefreshableView();

        _adapter = new ClassifyWaterfallAdapter(this, R.layout.item_showlist, ImageLoader.getInstance());

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

        _waterfallListView.setOnItemClickListener(new PLA_AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(PLA_AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(S02ShowClassify.this, S03SHowActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(S03SHowActivity.INPUT_SHOW_ENTITY, _adapter.getItemDataAtIndex(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        setLastUpdateTime();

        _pullRefreshListView.doPullRefreshing(true, 500);
    }
    private void setLastUpdateTime() {
        String text = formatDateTime(System.currentTimeMillis());
        _pullRefreshListView.setLastUpdatedLabel(text);
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
        JsonObjectRequest jor = new JsonObjectRequest(ShowClassifyConfig.getApi(classifyMod, Integer.valueOf(pageNo), Integer.valueOf(pageSize)), null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String resultStr = ((JSONObject) response.get("data")).get("shows").toString();
                    LinkedList<ShowEntity> results = ShowEntity.getLinkedListFromString(resultStr);
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
                    setLastUpdateTime();

                }catch (Exception error){
                    Toast.makeText(S02ShowClassify.this, "Error:" + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    _pullRefreshListView.onPullDownRefreshComplete();
                    _pullRefreshListView.onPullUpRefreshComplete();
                    _pullRefreshListView.setHasMoreData(true);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(S02ShowClassify.this, "Error:"+error.toString(), Toast.LENGTH_SHORT).show();
                _pullRefreshListView.onPullDownRefreshComplete();
                _pullRefreshListView.onPullUpRefreshComplete();
                _pullRefreshListView.setHasMoreData(true);
            }
        });
        QSApplication.QSRequestQueue().add(jor);
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
}
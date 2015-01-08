package com.focosee.qingshow.activity;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S08TrendListAdapter;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.entity.TrendEntity;
import com.focosee.qingshow.request.MJsonObjectRequest;
import com.focosee.qingshow.widget.MPullRefreshListView;
import com.focosee.qingshow.widget.PullToRefreshBase;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class S08TrendActivity extends Activity {


    private MPullRefreshListView mPullRefreshListView;
    private ListView listView;

    private S08TrendListAdapter adapter;
    private int _currentPageIndex = 1;

    private SimpleDateFormat _mDateFormat = new SimpleDateFormat("MM-dd HH:mm");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s08_trend);

        mPullRefreshListView = (MPullRefreshListView) findViewById(R.id.S08_content_list_view);
        listView = mPullRefreshListView.getRefreshableView();

        //test

        adapter = new S08TrendListAdapter(this, new LinkedList<TrendEntity>(), getScreenHeight());
        listView.setAdapter(adapter);

        mPullRefreshListView.setPullRefreshEnabled(true);
        mPullRefreshListView.setPullLoadEnabled(true);
        mPullRefreshListView.setScrollLoadEnabled(true);
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                doRefreshTask();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                doGetMoreTask();
            }
        });
        mPullRefreshListView.doPullRefreshing(true,500);
    }

    private void setLastUpdateTime() {
        String text = formatDateTime(System.currentTimeMillis());
        mPullRefreshListView.setLastUpdatedLabel(text);
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
        MJsonObjectRequest jor = new MJsonObjectRequest(QSAppWebAPI.getPreviewTrendListApi(Integer.valueOf(pageNo), Integer.valueOf(pageSize)), null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try{
                    LinkedList<TrendEntity> results = TrendEntity.getTrendListFromResponse(response);
                    if (_tRefreshSign) {
                       adapter.addItemTop(results);
                        _currentPageIndex = 1;
                    } else {
                        adapter.addItemLast(results);
                        _currentPageIndex++;
                    }
                    adapter.notifyDataSetChanged();
                    mPullRefreshListView.onPullDownRefreshComplete();
                    mPullRefreshListView.onPullUpRefreshComplete();
                    mPullRefreshListView.setHasMoreData(true);
                    setLastUpdateTime();

                }catch (Exception error){
                    Log.i("test", "error" + error.toString());
                    Toast.makeText(S08TrendActivity.this, "Error:" + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    mPullRefreshListView.onPullDownRefreshComplete();
                    mPullRefreshListView.onPullUpRefreshComplete();
                    mPullRefreshListView.setHasMoreData(true);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(S08TrendActivity.this, "Error:"+error.toString(), Toast.LENGTH_LONG).show();
                mPullRefreshListView.onPullDownRefreshComplete();
                mPullRefreshListView.onPullUpRefreshComplete();
                mPullRefreshListView.setHasMoreData(true);
            }
        });
        //Toast.makeText(this,jor.get,Toast.LENGTH_LONG).show();
        QSApplication.get().QSRequestQueue().add(jor);
    }

    private Point getScreenSize(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    private int getScreenHeight(){
        return getScreenSize().y;
    }

}

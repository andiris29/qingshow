package com.focosee.qingshow.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.HomeWaterfallAdapter;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.entity.ShowEntity;
import com.focosee.qingshow.widget.MPullRefreshListView;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.huewu.pla.lib.MultiColumnListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class S01HomeActivity extends Activity {

    private MPullRefreshListView _wfPullRefreshView;
    private MultiColumnListView _wfListView;
    private HomeWaterfallAdapter _adapter;
    private int _currentPageIndex = 1;

    private SimpleDateFormat _mDateFormat = new SimpleDateFormat("MM-dd HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s01_home);

        _wfPullRefreshView = (MPullRefreshListView) findViewById(R.id.S01_waterfall_content);
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
        setLastUpdateTime();

        _wfPullRefreshView.doPullRefreshing(true, 500);

    }

    private void setLastUpdateTime() {
        String text = formatDateTime(System.currentTimeMillis());
        _wfPullRefreshView.setLastUpdatedLabel(text);
    }

    private String formatDateTime(long time) {
        if (0 == time) {
            return "";
        }

        return _mDateFormat.format(new Date(time));
    }

    private void doRefreshTask() {
        Log.i("okyu", "refresh: start");
        _getDataFromNet(true, "1", "10");
    }

    private void doGetMoreTask() {
        Log.i("okyu", "loadmore: start");
        _getDataFromNet(false, String.valueOf(_currentPageIndex+1), "10");
    }

    private void _getDataFromNet(boolean refreshSign, String pageNo, String pageSize) {
        final boolean _tRefreshSign = refreshSign;
        String url = "http://121.41.162.102:30001/services/feeding/chosen?pageNo="+pageNo+"&pageSize="+pageSize;
        JsonObjectRequest jor = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String resultStr = ((JSONObject) response.get("data")).get("shows").toString();
                    Log.i("okyu", "receive:" + resultStr);
                    LinkedList<ShowEntity> results = ShowEntity.getLinkedListFromString(resultStr);
                    if (_tRefreshSign) {
                        _adapter.addItemTop(results);
                        _adapter.notifyDataSetChanged();
                        _currentPageIndex = 1;
                        Log.i("okyu", "refresh: ok");
                    } else {
                        _adapter.addItemLast(results);
                        _adapter.notifyDataSetChanged();
                        _currentPageIndex++;
                        Log.i("okyu", "load more: ok");
                    }
                    _wfPullRefreshView.onPullDownRefreshComplete();
                    _wfPullRefreshView.onPullUpRefreshComplete();
                    _wfPullRefreshView.setHasMoreData(true);
                    setLastUpdateTime();

                }catch (Exception error){
                    Toast.makeText(S01HomeActivity.this, "Error:"+error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    Log.i("okyu", "error" + error.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(S01HomeActivity.this, "Error:"+error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                Log.i("okyu", "error" + error.getMessage());
            }
        });
        QSApplication.QSRequestQueue().add(jor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_s01_home, menu);
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

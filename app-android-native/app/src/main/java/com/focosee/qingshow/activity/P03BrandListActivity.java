package com.focosee.qingshow.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.P03BrandListAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.model.vo.mongo.MongoBrand;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.BrandParser;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.widget.MNavigationView;
import com.focosee.qingshow.widget.MPullRefreshListView;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.ArrayList;

public class P03BrandListActivity extends BaseActivity {

    public static String ACTION_REFRESH = "refresh_P03BrandListActivity";

    private MNavigationView navigationView;
    public MPullRefreshListView pullRefreshListView;
    private ListView listView;
    public int brandType = 0;
    private int pageIndex = 1;

    private P03BrandListAdapter adapter;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p03_brand_list);

        navigationView = (MNavigationView) findViewById(R.id.P03_brand_list_navigation);
        navigationView.getBtn_left().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                P03BrandListActivity.this.finish();
            }
        });
        navigationView.getBtn_right().setVisibility(View.INVISIBLE);

        pullRefreshListView = (MPullRefreshListView) findViewById(R.id.P03_brand_list_list_view);
        listView = pullRefreshListView.getRefreshableView();

        pullRefreshListView.setPullRefreshEnabled(true);
        pullRefreshListView.setPullLoadEnabled(true);
        pullRefreshListView.setScrollLoadEnabled(true);

        adapter = new P03BrandListAdapter(this, new ArrayList<MongoBrand>(), ImageLoader.getInstance(), (LinearLayout)findViewById(R.id.P03_brand_list_tab_control));

        listView.setAdapter(adapter);
        pullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadMoreData();
            }
        });
        pullRefreshListView.doPullRefreshing(true, 0);
        registerReceiver(receiver, new IntentFilter(P03BrandListActivity.ACTION_REFRESH));
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public void reconn() {
        refreshData();
    }

    private void loadMoreData() {
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getBrandListApi(String.valueOf(brandType),String.valueOf(pageIndex + 1)),null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(MetadataParser.hasError(response)){
                    pullRefreshListView.onPullUpRefreshComplete();
                    Toast.makeText(P03BrandListActivity.this, "没有更多了！", Toast.LENGTH_SHORT).show();
                    return;
                }
                pageIndex++;
                ArrayList<MongoBrand> moreData = BrandParser.parseQueryBrands(response);
                adapter.addData(moreData);
                pullRefreshListView.setHasMoreData(false);
                adapter.notifyDataSetChanged();
                pullRefreshListView.onPullUpRefreshComplete();
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    public void refreshData() {
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getBrandListApi(String.valueOf(brandType),"1"),null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(MetadataParser.hasError(response)){
                    Toast.makeText(P03BrandListActivity.this, "没有数据！", Toast.LENGTH_SHORT).show();
                }
                pageIndex = 1;
                ArrayList<MongoBrand> newData = BrandParser.parseQueryBrands(response);
                adapter.resetData(newData);
                adapter.notifyDataSetChanged();

                pullRefreshListView.onPullDownRefreshComplete();
                pullRefreshListView.setHasMoreData(true);
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void handleErrorMsg(VolleyError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        Log.i("P03BrandListActivity", error.toString());
    }

    private ArrayList<MongoBrand> __createFakeData() {
        ArrayList<MongoBrand> tempData = new ArrayList<MongoBrand>();
        for (int i = 0; i < 3; i++) {
            MongoBrand brandEntity = new MongoBrand();
            brandEntity.name = "品牌" + String.valueOf(i);
            brandEntity.logo = "http://img2.imgtn.bdimg.com/it/u=2439868726,3891592022&fm=21&gp=0.jpg";
            brandEntity.cover = "http://img1.imgtn.bdimg.com/it/u=3411049717,3668206888&fm=21&gp=0.jpg";
            tempData.add(brandEntity);
        }
        return tempData;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("P03BrandList"); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("P03BrandList"); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }
}

package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.P02ModelItemListAdapter;
import com.focosee.qingshow.adapter.P02ModelViewPagerAdapter;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.entity.ModelShowEntity;
import com.focosee.qingshow.widget.MPullRefreshListView;
import com.focosee.qingshow.widget.PullToRefreshBase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class P02ModelActivity extends Activity {

    private ViewPager viewPager;
    private MPullRefreshListView latestPullRefreshListView;
    private ListView latestListView;

    private P02ModelViewPagerAdapter viewPagerAdapter;
    private P02ModelItemListAdapter itemListAdapter;

    private String modelId;
    private int pageIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p02_model);

        viewPager = (ViewPager) findViewById(R.id.P02_personalViewPager);

        modelId = getIntent().getStringExtra("_id");

        ArrayList<View> pagerViewList = new ArrayList<View>();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        pagerViewList.add(inflater.inflate(R.layout.pager_p02_model_item, null));
        viewPagerAdapter = new P02ModelViewPagerAdapter(pagerViewList);

        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        latestPullRefreshListView = (MPullRefreshListView) pagerViewList.get(0).findViewById(R.id.pager_P02_item_list);
        latestListView = latestPullRefreshListView.getRefreshableView();

        ArrayList<ModelShowEntity> itemEntities = new ArrayList<ModelShowEntity>();
        itemListAdapter = new P02ModelItemListAdapter(this, itemEntities);

        latestListView.setAdapter(itemListAdapter);
        latestPullRefreshListView.setScrollLoadEnabled(true);
        latestPullRefreshListView.setPullRefreshEnabled(true);
        latestPullRefreshListView.setPullLoadEnabled(true);
        latestPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                doRefreshDataTask();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                doLoadMoreTask();
            }
        });

        latestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(P02ModelActivity.this, S03SHowActivity.class);
                intent.putExtra(S03SHowActivity.INPUT_SHOW_ENTITY_ID, ((ModelShowEntity)itemListAdapter.getItem(position)).get_id());
                startActivity(intent);
            }
        });

        latestPullRefreshListView.doPullRefreshing(true, 0);
    }

    private void doRefreshDataTask() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, QSAppWebAPI.getModelShowsApi(String.valueOf(modelId), "1"), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (checkErrorExist(response)) {
                    try {
                        Toast.makeText(P02ModelActivity.this, ((JSONObject)response.get("metadata")).get("devInfo").toString(), Toast.LENGTH_SHORT).show();
                    }catch (JSONException e) {
                        Toast.makeText(P02ModelActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    latestPullRefreshListView.onPullDownRefreshComplete();
                    latestPullRefreshListView.setHasMoreData(false);
                    return;
                }

                pageIndex = 1;

                ArrayList<ModelShowEntity> modelShowEntities = ModelShowEntity.getModelShowEntities(response);

                itemListAdapter.resetData(modelShowEntities);
                itemListAdapter.notifyDataSetChanged();
                latestPullRefreshListView.onPullDownRefreshComplete();
                latestPullRefreshListView.setHasMoreData(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                latestPullRefreshListView.onPullDownRefreshComplete();
                handleErrorMsg(error);
            }
        });
        QSApplication.get().QSRequestQueue().add(jsonObjectRequest);
    }

    private void doLoadMoreTask() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, QSAppWebAPI.getModelShowsApi(String.valueOf(modelId), String.valueOf(pageIndex + 1)), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (checkErrorExist(response)) {
                    try {
                        Toast.makeText(P02ModelActivity.this, ((JSONObject)response.get("metadata")).get("devInfo").toString(), Toast.LENGTH_SHORT).show();
                    }catch (JSONException e) {
                        Toast.makeText(P02ModelActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    latestPullRefreshListView.onPullUpRefreshComplete();
                    latestPullRefreshListView.setHasMoreData(false);
                    return;
                }

                pageIndex++;

                ArrayList<ModelShowEntity> modelShowEntities = ModelShowEntity.getModelShowEntities(response);

                itemListAdapter.addData(modelShowEntities);
                itemListAdapter.notifyDataSetChanged();
                latestPullRefreshListView.onPullUpRefreshComplete();
                latestPullRefreshListView.setHasMoreData(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                latestPullRefreshListView.onPullUpRefreshComplete();
                handleErrorMsg(error);
            }
        });
        QSApplication.get().QSRequestQueue().add(jsonObjectRequest);
    }

    private boolean checkErrorExist(JSONObject response) {
        try {
            return ((JSONObject)response.get("metadata")).has("error");
        }catch (Exception e) {
            return true;
        }
    }

    private void handleErrorMsg(VolleyError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        Log.i("P02ModelActivity", error.toString());
    }

}

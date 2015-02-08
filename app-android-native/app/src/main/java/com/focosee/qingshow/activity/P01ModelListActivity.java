package com.focosee.qingshow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.P01ModelListAdapter;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.entity.mongo.MongoPeople;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.PeopleParser;
import com.focosee.qingshow.request.QSJsonObjectRequest;
import com.focosee.qingshow.widget.MNavigationView;
import com.focosee.qingshow.widget.MPullRefreshListView;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;

public class P01ModelListActivity extends BaseActivity {

    private MNavigationView navigationView;
    private MPullRefreshListView pullRefreshListView;
    private ListView listView;
    private P01ModelListAdapter adapter;
    private int _currentIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p01_model_list);

        navigationView = (MNavigationView) findViewById(R.id.P01_model_list_navigation);
        navigationView.getBtn_left().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                P01ModelListActivity.this.finish();
            }
        });

        pullRefreshListView = (MPullRefreshListView) findViewById(R.id.P01_model_list_view);

        pullRefreshListView.setPullLoadEnabled(true);
        pullRefreshListView.setPullRefreshEnabled(true);
        pullRefreshListView.setScrollLoadEnabled(true);

        listView = pullRefreshListView.getRefreshableView();

        adapter = new P01ModelListAdapter(this, new ArrayList<MongoPeople>(), ImageLoader.getInstance(), P01ModelListAdapter.TYPE_P01MODELLIST);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(P01ModelListActivity.this, "test click", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(P01ModelListActivity.this, P02ModelActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(P02ModelActivity.INPUT_MODEL, ((MongoPeople) adapter.getItem(position)));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        pullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                doRefreshData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                doLoadMoreData();
            }
        });

        pullRefreshListView.doPullRefreshing(true, 0);

    }

    private void doRefreshData() {
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getModelListApi("1", "10"), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ArrayList<MongoPeople> moreData = PeopleParser.parseQueryModels(response);
                adapter.resetData(moreData);
                adapter.notifyDataSetChanged();

                _currentIndex = 1;

                pullRefreshListView.onPullDownRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleErrorMsg(error);
            }
        });

        QSApplication.get().QSRequestQueue().add(jsonObjectRequest);
    }

    private void doLoadMoreData() {
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getModelListApi(String.valueOf(_currentIndex + 1), "10"), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if (MetadataParser.hasError(response)) {
                    pullRefreshListView.onPullUpRefreshComplete();
                    Toast.makeText(P01ModelListActivity.this, "没有更多了！", Toast.LENGTH_SHORT).show();
                    return;
                }
                ArrayList<MongoPeople> moreData = PeopleParser.parseQueryModels(response);
                adapter.addData(moreData);
                adapter.notifyDataSetChanged();
                pullRefreshListView.onPullUpRefreshComplete();
                _currentIndex++;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleErrorMsg(error);
            }
        });

        QSApplication.get().QSRequestQueue().add(jsonObjectRequest);
    }

    private void handleErrorMsg(VolleyError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        Log.i("P01ModelListActivity", error.toString());
    }
}

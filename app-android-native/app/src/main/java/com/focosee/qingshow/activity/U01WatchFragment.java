package com.focosee.qingshow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.HomeWaterfallAdapter;
import com.focosee.qingshow.adapter.P01ModelListAdapter;
import com.focosee.qingshow.adapter.P03BrandListAdapter;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.entity.BrandEntity;
import com.focosee.qingshow.entity.ModelEntity;
import com.focosee.qingshow.entity.ShowListEntity;
import com.focosee.qingshow.request.MJsonObjectRequest;
import com.focosee.qingshow.widget.MNavigationView;
import com.focosee.qingshow.widget.MPullRefreshListView;
import com.focosee.qingshow.widget.MPullRefreshMultiColumnListView;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.huewu.pla.lib.MultiColumnListView;
import com.huewu.pla.lib.internal.PLA_AdapterView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by zenan on 12/27/14.
 */
public class U01WatchFragment extends Fragment {

    private MPullRefreshListView pullRefreshListView;
    private ListView listView;
    private P01ModelListAdapter adapter;

    public static U01WatchFragment newInstance() {
        U01WatchFragment fragment = new U01WatchFragment();

        return fragment;
    }

    public U01WatchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_personal_pager_watch, container, false);

        pullRefreshListView = (MPullRefreshListView) view.findViewById(R.id.modelMPullRefreshListView);

        pullRefreshListView.setPullLoadEnabled(true);
        pullRefreshListView.setPullRefreshEnabled(true);
        pullRefreshListView.setScrollLoadEnabled(true);

        listView = pullRefreshListView.getRefreshableView();

        adapter = new P01ModelListAdapter(getActivity(), new ArrayList<ModelEntity>(), ImageLoader.getInstance());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), P02ModelActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(P02ModelActivity.INPUT_MODEL, ((ModelEntity) adapter.getItem(position)));
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

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void doRefreshData() {
        MJsonObjectRequest jsonObjectRequest = new MJsonObjectRequest(QSAppWebAPI.getModelListApi("1", "10"), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ArrayList<ModelEntity> moreData = ModelEntity.getModelEntityListFromResponse(response);
                adapter.resetData(moreData);
                adapter.notifyDataSetChanged();

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
        MJsonObjectRequest jsonObjectRequest = new MJsonObjectRequest(QSAppWebAPI.getModelListApi("1", "10"), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ArrayList<ModelEntity> moreData = ModelEntity.getModelEntityListFromResponse(response);
                adapter.addData(moreData);
                adapter.notifyDataSetChanged();

                pullRefreshListView.onPullUpRefreshComplete();
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
        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
        Log.i("U01WatchFragment", error.toString());
    }

}

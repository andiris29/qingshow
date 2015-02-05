package com.focosee.qingshow.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.HomeWaterfallAdapter;
import com.focosee.qingshow.adapter.P01ModelListAdapter;
import com.focosee.qingshow.adapter.P02ModelFollowPeopleListAdapter;
import com.focosee.qingshow.adapter.P03BrandListAdapter;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.entity.BrandEntity;
import com.focosee.qingshow.entity.FollowPeopleEntity;
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
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by zenan on 12/27/14.
 */
public class U01WatchFragment extends Fragment {
    private ListView followerListView;
    private MPullRefreshListView followerPullRefreshListView;
    private P01ModelListAdapter followerPeopleListAdapter;
    private U01PersonalActivity u01PersonalActivity;

    private int pageIndex = 1;
    private String _id;

    public static U01WatchFragment newInstance() {
        U01WatchFragment fragment = new U01WatchFragment();
        return fragment;
    }

    public U01WatchFragment() {
        // Required empty public constructor
    }

    public void setU01PersonalActivity(U01PersonalActivity u01PersonalActivity){
        this.u01PersonalActivity = u01PersonalActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _id = QSApplication.get().getPeople()._id;
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_personal_pager_watch, container, false);

        followerPullRefreshListView = (MPullRefreshListView) view.findViewById(R.id.pager_P02_item_list);
        followerListView = followerPullRefreshListView.getRefreshableView();
        ArrayList<ModelEntity> followerPeopleList = new ArrayList<ModelEntity>();
        followerPeopleListAdapter = new P01ModelListAdapter(getActivity(), followerPeopleList, ImageLoader.getInstance(), P01ModelListAdapter.TYPE_U01WATCHFRAGMENT);
        followerPeopleListAdapter.setU01PersonActivity(u01PersonalActivity);
        followerListView.setAdapter(followerPeopleListAdapter);
        followerPullRefreshListView.setScrollLoadEnabled(true);
        followerPullRefreshListView.setPullRefreshEnabled(false);
        followerPullRefreshListView.setPullLoadEnabled(true);
        followerPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                doFollowersLoadMoreTask();
            }
        });

        followerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), P02ModelActivity.class);

                ModelEntity itemEntity = followerPeopleListAdapter.getItemData(position);

                Bundle bundle = new Bundle();
                bundle.putSerializable(P02ModelActivity.INPUT_MODEL, itemEntity);

                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
        doFollowersRefreshDataTask();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void doFollowersRefreshDataTask() {
        MJsonObjectRequest jsonObjectRequest = new MJsonObjectRequest(Request.Method.GET,
                QSAppWebAPI.getPeopleQueryFollowedApi(_id,1, 10), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ((TextView) getActivity().findViewById(R.id.followedCountTextView)).setText(getTotalDataFromResponse(response));
                if (checkErrorExist(response)) {
                    followerPullRefreshListView.onPullUpRefreshComplete();
                    followerPullRefreshListView.setHasMoreData(false);
                    return;
                }

                pageIndex = 1;

                ArrayList<ModelEntity> modelShowEntities = ModelEntity.getModelEntityListFromResponse(response);
                followerPeopleListAdapter.resetData(modelShowEntities);
                followerPeopleListAdapter.notifyDataSetChanged();
                followerPullRefreshListView.onPullUpRefreshComplete();
                followerPullRefreshListView.setHasMoreData(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                followerPullRefreshListView.onPullUpRefreshComplete();
                handleErrorMsg(error);
            }
        });
        QSApplication.get().QSRequestQueue().add(jsonObjectRequest);
    }

    private void doFollowersLoadMoreTask() {
        MJsonObjectRequest jsonObjectRequest = new MJsonObjectRequest(Request.Method.GET,
                QSAppWebAPI.getPeopleQueryFollowedApi(_id, pageIndex + 1, 10), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (checkErrorExist(response)) {
                    Toast.makeText(getActivity(), "没有更多数据了！", Toast.LENGTH_SHORT).show();
                    followerPullRefreshListView.onPullUpRefreshComplete();
                    followerPullRefreshListView.setHasMoreData(false);
                    return;
                }

                pageIndex++;


                ArrayList<ModelEntity> modelShowEntities = ModelEntity.getModelEntityListFromResponse(response);

                followerPeopleListAdapter.addData(modelShowEntities);
                followerPeopleListAdapter.notifyDataSetChanged();
                followerPullRefreshListView.onPullUpRefreshComplete();
                followerPullRefreshListView.setHasMoreData(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                followerPullRefreshListView.onPullUpRefreshComplete();
                handleErrorMsg(error);
            }
        });
        QSApplication.get().QSRequestQueue().add(jsonObjectRequest);
    }

    private boolean checkErrorExist(JSONObject response) {
        try {
            return ((JSONObject) response.get("metadata")).has("error");
        } catch (Exception e) {
            return true;
        }
    }

    private void handleErrorMsg(VolleyError error) {
        Log.i("P02ModelActivity", error.toString());
    }

    private String getTotalDataFromResponse(JSONObject response) {
        try {
            return ((JSONObject) response.get("metadata")).get("numTotal").toString();
        } catch (Exception e) {
            return "0";
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MainScreen"); //统计页面
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainScreen");
    }
}

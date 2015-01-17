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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.HomeWaterfallAdapter;
import com.focosee.qingshow.adapter.P02ModelItemListAdapter;
import com.focosee.qingshow.adapter.P03BrandListAdapter;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.entity.BrandEntity;
import com.focosee.qingshow.entity.ModelShowEntity;
import com.focosee.qingshow.entity.ShowListEntity;
import com.focosee.qingshow.request.MJsonObjectRequest;
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
public class U01RecommendFragment extends Fragment {
    private int currentPageIndex = 1;

    private MPullRefreshListView latestPullRefreshListView;
    private ListView latestListView;
    private P02ModelItemListAdapter itemListAdapter;

    public static U01RecommendFragment newInstance() {
        U01RecommendFragment fragment = new U01RecommendFragment();

        return fragment;
    }

    public U01RecommendFragment() {
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
        View view = inflater.inflate(R.layout.activity_personal_pager_recommend, container, false);

        latestPullRefreshListView = (MPullRefreshListView) view.findViewById(R.id.pager_P02_item_list);
        latestListView = latestPullRefreshListView.getRefreshableView();

        ArrayList<ModelShowEntity> itemEntities = new ArrayList<ModelShowEntity>();
        itemListAdapter = new P02ModelItemListAdapter(getActivity(), itemEntities);
        latestListView.setAdapter(itemListAdapter);
        latestPullRefreshListView.setScrollLoadEnabled(true);
        latestPullRefreshListView.setPullRefreshEnabled(true);
        latestPullRefreshListView.setPullLoadEnabled(true);
        latestPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                doShowsRefreshDataTask();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                doShowsLoadMoreTask(currentPageIndex+"", 10+"");
            }
        });

        latestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), S03SHowActivity.class);
                intent.putExtra(S03SHowActivity.INPUT_SHOW_ENTITY_ID, ((ModelShowEntity)itemListAdapter.getItem(position)).get_id());
                startActivity(intent);
            }
        });

        latestPullRefreshListView.doPullRefreshing(true, 0);


        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    private void doShowsRefreshDataTask() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                QSAppWebAPI.getFeedingRecommendationApi(1, 10), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ((TextView)getActivity().findViewById(R.id.recommendCountTextView)).setText(getTotalDataFromResponse(response));
                if (checkErrorExist(response)) {
//                    try {
//                        Toast.makeText(P02ModelActivity.this, ((JSONObject)response.get("metadata")).get("devInfo").toString(), Toast.LENGTH_SHORT).show();
//                    }catch (Exception e) {
//                        Toast.makeText(P02ModelActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
                    latestPullRefreshListView.onPullDownRefreshComplete();
                    latestPullRefreshListView.setHasMoreData(false);
                    return;
                }

                currentPageIndex = 1;

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

    private void doShowsLoadMoreTask(String pageNo, String pageSize) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                QSAppWebAPI.getFeedingRecommendationApi(Integer.valueOf(pageNo), Integer.valueOf(pageSize)), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (checkErrorExist(response)) {
//                    try {
//                        Toast.makeText(P02ModelActivity.this, ((JSONObject)response.get("metadata")).get("devInfo").toString(), Toast.LENGTH_SHORT).show();
//                    }catch (JSONException e) {
//                        Toast.makeText(P02ModelActivity.this,  e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
                    latestPullRefreshListView.onPullUpRefreshComplete();
                    latestPullRefreshListView.setHasMoreData(false);
                    return;
                }

                currentPageIndex++;

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
        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
        Log.i("P02ModelActivity", error.toString());
    }

    private String getTotalDataFromResponse(JSONObject response) {
        try {
            return ((JSONObject)response.get("metadata")).get("numTotal").toString();
        } catch (Exception e) {
            return "0";
        }
    }

}

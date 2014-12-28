package com.focosee.qingshow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.HomeWaterfallAdapter;
import com.focosee.qingshow.adapter.P03BrandListAdapter;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.entity.BrandEntity;
import com.focosee.qingshow.entity.ShowListEntity;
import com.focosee.qingshow.request.MJsonObjectRequest;
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
    private MPullRefreshMultiColumnListView mPullRefreshMultiColumnListView;
    private MultiColumnListView multiColumnListView;
    private HomeWaterfallAdapter homeWaterfallAdapter;
    private int currentPageIndex = 1;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");

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

        mPullRefreshMultiColumnListView = (MPullRefreshMultiColumnListView) view.findViewById(R.id.P03_brand_list_list_view);
        multiColumnListView = mPullRefreshMultiColumnListView.getRefreshableView();

        homeWaterfallAdapter = new HomeWaterfallAdapter(getActivity(), R.layout.item_showlist, ImageLoader.getInstance());
        multiColumnListView.setAdapter(homeWaterfallAdapter);

        mPullRefreshMultiColumnListView.setPullLoadEnabled(true);
        mPullRefreshMultiColumnListView.setScrollLoadEnabled(true);

        mPullRefreshMultiColumnListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<MultiColumnListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<MultiColumnListView> refreshView) {
                doRefreshTask();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<MultiColumnListView> refreshView) {
                doGetMoreTask();
            }
        });

        multiColumnListView.setOnItemClickListener(new PLA_AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(PLA_AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), S03SHowActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(S03SHowActivity.INPUT_SHOW_ENTITY_ID, homeWaterfallAdapter.getItemDataAtIndex(position)._id);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        setLastUpdateTime();
        mPullRefreshMultiColumnListView.doPullRefreshing(true, 500);


        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    private void doRefreshTask() {
        getDataFromNet(true, "1", "10");
    }

    private void doGetMoreTask() {
        getDataFromNet(false, String.valueOf(currentPageIndex + 1), "10");
    }

    //TODO 可以抽出来，四个fragment合成一个，然后在onCreateView判断
    private void getDataFromNet(boolean refreshSign, String pageNo, String pageSize) {
        final boolean tRefreshSign = refreshSign;
        MJsonObjectRequest jor = new MJsonObjectRequest(QSAppWebAPI.getShowListApi(Integer.valueOf(pageNo), Integer.valueOf(pageSize)), null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try{
                    LinkedList<ShowListEntity> results = ShowListEntity.getShowListFromResponse(response);
                    if (tRefreshSign) {
                        homeWaterfallAdapter.addItemTop(results);
                        currentPageIndex = 1;
                    } else {
                        homeWaterfallAdapter.addItemLast(results);
                        currentPageIndex++;
                    }
                    homeWaterfallAdapter.notifyDataSetChanged();
                    mPullRefreshMultiColumnListView.onPullDownRefreshComplete();
                    mPullRefreshMultiColumnListView.onPullUpRefreshComplete();
                    mPullRefreshMultiColumnListView.setHasMoreData(true);
                    setLastUpdateTime();

                }catch (Exception error){
                    Log.i("test", "error" + error.toString());
                    Toast.makeText(getActivity(), "Error:"+error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    mPullRefreshMultiColumnListView.onPullDownRefreshComplete();
                    mPullRefreshMultiColumnListView.onPullUpRefreshComplete();
                    mPullRefreshMultiColumnListView.setHasMoreData(true);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Error:"+error.toString(), Toast.LENGTH_SHORT).show();
                mPullRefreshMultiColumnListView.onPullDownRefreshComplete();
                mPullRefreshMultiColumnListView.onPullUpRefreshComplete();
                mPullRefreshMultiColumnListView.setHasMoreData(true);
            }
        });
        QSApplication.get().QSRequestQueue().add(jor);
    }

    private void setLastUpdateTime() {
        String text = formatDateTime(System.currentTimeMillis());
        mPullRefreshMultiColumnListView.setLastUpdatedLabel(text);
    }

    private String formatDateTime(long time) {
        if (0 == time) {
            return "";
        }

        return simpleDateFormat.format(new Date(time));
    }

}

package com.focosee.qingshow.activity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.Response;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.U01UserActivity;
import com.focosee.qingshow.adapter.U01FansFragAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.PeopleParser;
import com.focosee.qingshow.httpapi.response.error.ErrorCode;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.EventModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.LinkedList;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
public class U01FansFragment extends U01BaseFragment {


    private U01FansFragAdapter adapter;

    public static U01FansFragment newInstance(){
        return new U01FansFragment();
    }
    public U01FansFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        adapter = new U01FansFragAdapter(new LinkedList<MongoPeople>(), getActivity(), R.layout.item_u01_push, R.layout.item_u01_fan_and_followers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.setTag(U01UserActivity.POS_FANS);
                EventModel eventModel = new EventModel(U01UserActivity.class.getSimpleName(), recyclerView);
                EventBus.getDefault().post(eventModel);
            }
        });
        refresh();
        return view;
    }


    @Override
    public void refresh() {
        getDatasFromNet(1, 10);
    }

    @Override
    public void loadMore() {
        getDatasFromNet(currentPageN0, 10);
    }

    public void getDatasFromNet(final int pageNo, int pageSize){

        if(user == null) return;

        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getQueryPeopleFollowerApi(user._id, pageNo, pageSize), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(MetadataParser.hasError(response)){
                    if(MetadataParser.getError(response) != ErrorCode.PagingNotExist) {
                        ErrorHandler.handle(getActivity(), MetadataParser.getError(response));
                    }
                    if(pageNo == 1){
                        adapter.clearData();
                        adapter.notifyDataSetChanged();
                    }
                    mRefreshLayout.endLoadingMore();
                    mRefreshLayout.endRefreshing();
                    return;
                }

                ArrayList<MongoPeople> peoples = PeopleParser.parseQueryFollowers(response);
                if(pageNo == 1) {
                    adapter.addDataAtTop(peoples);
                    mRefreshLayout.endRefreshing();
                    currentPageN0 = pageNo;
                }else{
                    adapter.addData(peoples);
                    mRefreshLayout.endLoadingMore();
                }
                adapter.notifyDataSetChanged();
                currentPageN0++;
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("U01FansFragment");
        MobclickAgent.onResume(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("U01FansFragment");
        MobclickAgent.onPause(getActivity());
    }


}

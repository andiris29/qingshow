package com.focosee.qingshow.activity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.Response;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.U01UserActivity;
import com.focosee.qingshow.adapter.U01FollowerFragAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.PeopleParser;
import com.focosee.qingshow.httpapi.response.error.ErrorCode;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.EventModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
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
public class U01FollowerFragment extends U01BaseFragment {

    private static final String TAG = "U01FollowerFragment";

    private U01FollowerFragAdapter adapter;

    public static U01FollowerFragment newInstance(){
        return new U01FollowerFragment();
    }
    public U01FollowerFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        adapter = new U01FollowerFragAdapter(new LinkedList<MongoPeople>(), getActivity(), R.layout.item_u01_push, R.layout.item_u01_fan_and_followers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.setTag(U01UserActivity.POS_FOLLOW);
                EventModel eventModel = new EventModel(U01UserActivity.class, recyclerView);
                EventBus.getDefault().post(eventModel);
            }
        });
        refresh();
        return view;
    }

    @Override
    public void onResume() {
        refresh();
        super.onResume();
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

        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getPeopleQueryFollowPeoplesApi(user._id, pageNo, pageSize), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "response:" + response);
                if(MetadataParser.hasError(response)){
                    if(MetadataParser.getError(response) == ErrorCode.PagingNotExist)
                        recyclerPullToRefreshView.setHasMoreData(false);
                    else {
                        ErrorHandler.handle(getActivity(), MetadataParser.getError(response));
                        recyclerPullToRefreshView.onPullUpRefreshComplete();
                    }
                    recyclerPullToRefreshView.onPullDownRefreshComplete();
                    return;
                }

                ArrayList<MongoPeople> peoples = PeopleParser.parseQueryFollowers(response);
                if(pageNo == 1) {
                    adapter.addDataAtTop(peoples);
                    recyclerPullToRefreshView.onPullDownRefreshComplete();
                    currentPageN0 = pageNo;
                }else{
                    adapter.addData(peoples);
                    recyclerPullToRefreshView.onPullUpRefreshComplete();
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

}

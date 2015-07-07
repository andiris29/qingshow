package com.focosee.qingshow.activity.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.Response;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.U01UserActivity;
import com.focosee.qingshow.adapter.U01MatchFragAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.ShowParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.U01Model;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.widget.RecyclerPullToRefreshView;

import org.json.JSONObject;
import java.util.LinkedList;
import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class U01MatchFragment extends U01BaseFragment {

    private static final String TAG = "U01MatchFragment";

    private U01MatchFragAdapter adapter;
    private static Context context;

    public static U01MatchFragment newInstance(Context context1){
        context = context1;
        return new U01MatchFragment();
    }

    public U01MatchFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        adapter = new U01MatchFragAdapter(new LinkedList<MongoShow>(), context, R.layout.item_u01_push, R.layout.item_u01_match_frag);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 0 == position ? 2 : 1;
            }
        });

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.setTag(U01UserActivity.POS_MATCH);
                EventBus.getDefault().post(recyclerView);
            }
        });

        recyclerPullToRefreshView.doPullRefreshing(true, 200);
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

        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getMatchCreatedbyApi(U01Model.INSTANCE.getUser()._id, pageNo, pageSize), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "response:" + response);
                if(MetadataParser.hasError(response)){
                    ErrorHandler.handle(getActivity(), MetadataParser.getError(response));
                    recyclerPullToRefreshView.onPullDownRefreshComplete();
                    recyclerPullToRefreshView.onPullUpRefreshComplete();
                    return;
                }

                if (pageNo == 1) {
                    adapter.addDataAtTop(ShowParser.parseQuery_itemString(response));
                    recyclerPullToRefreshView.onPullDownRefreshComplete();
                    currentPageN0 = pageNo;
                }else{
                    adapter.addData(ShowParser.parseQuery_itemString(response));
                    recyclerPullToRefreshView.onPullUpRefreshComplete();
                }
                currentPageN0++;
                adapter.notifyDataSetChanged();
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

}

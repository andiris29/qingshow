package com.focosee.qingshow.activity.fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
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
import com.focosee.qingshow.httpapi.response.error.ErrorCode;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.EventModel;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;
import java.util.LinkedList;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class U01MatchFragment extends U01BaseFragment {

    private static final String TAG = "U01MatchFragment";

    private U01MatchFragAdapter adapter;

    public static U01MatchFragment newInstance(){
        return new U01MatchFragment();
    }

    public U01MatchFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        adapter = new U01MatchFragAdapter(new LinkedList<MongoShow>(), getActivity(), R.layout.item_u01_push, R.layout.item_match);
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
                EventModel eventModel = new EventModel(U01UserActivity.class.getSimpleName(), recyclerView);
                EventBus.getDefault().post(eventModel);
            }
        });

        refresh();
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void refresh() {
        getDatasFromNet(1, 10);
    }

    @Override
    public void loadMore() {
        getDatasFromNet(currentPageN0, 10);
    }

    public void getDatasFromNet(final int pageNo, final int pageSize){

        if(user == null) return;

        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getMatchCreatedbyApi(user._id, pageNo, pageSize), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Message message = new Message();
                if(MetadataParser.hasError(response)){
                    if(MetadataParser.getError(response) != ErrorCode.PagingNotExist) {
                        ErrorHandler.handle(getActivity(), MetadataParser.getError(response));
                    }
                    if(pageNo == 1){
                        adapter.clearData();
                        adapter.notifyDataSetChanged();
                    }

                    message.arg1 = ERROR;
                    handler.sendMessage(message);
                    return;
                }

                if (pageNo == 1) {
                    adapter.addDataAtTop(ShowParser.parseQuery_itemString(response));
                    message.arg1 = REFRESH_FINISH;
                    currentPageN0 = pageNo;
                }else{
                    adapter.addData(ShowParser.parseQuery_itemString(response));
                    message.arg1 = LOAD_FINISH;
                }
                handler.sendMessage(message);
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

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("U01MatchFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("U01MatchFragment");
    }

}

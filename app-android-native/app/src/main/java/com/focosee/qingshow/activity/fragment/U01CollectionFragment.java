package com.focosee.qingshow.activity.fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.Response;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.U01UserActivity;
import com.focosee.qingshow.adapter.U01CollectionFragAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.ShowParser;
import com.focosee.qingshow.httpapi.response.error.ErrorCode;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.EventModel;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.ShowUtil;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
public class U01CollectionFragment extends U01BaseFragment {

    private static final String TAG = "U01CollectionFragment";

    private U01CollectionFragAdapter adapter;

    public static U01CollectionFragment newInstance(){
        return new U01CollectionFragment();
    }
    public U01CollectionFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        adapter = new U01CollectionFragAdapter(new LinkedList<MongoShow>(), getActivity(), R.layout.item_u01_push, R.layout.item_match, R.layout.item_u01_collection_qingshow);
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
                recyclerView.setTag(U01UserActivity.POS_COLL);
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

        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getFeedingLikeApi(user._id, pageNo, pageSize), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(U01CollectionFragment.class.getSimpleName(), "response:" + response);
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

                List<MongoShow> datas = ShowUtil.cleanHidedShow(ShowParser.parseQuery(response));

                if(pageNo == 1) {
                     adapter.addDataAtTop(datas);
                    message.arg1 = REFRESH_FINISH;
                    currentPageN0 = pageNo;
                }else{
                    adapter.addData(datas);
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
        MobclickAgent.onPageStart("U01CollectionFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("U01CollectionFragment");
    }

}

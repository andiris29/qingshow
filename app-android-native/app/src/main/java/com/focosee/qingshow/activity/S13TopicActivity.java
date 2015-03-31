package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.Response;
import com.focosee.qingshow.Listener.EndlessRecyclerOnScrollListener;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S13TopicAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.ShowParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.vo.mongo.MongoShow;

import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by Administrator on 2015/3/31.
 */
public class S13TopicActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private S13TopicAdapter mAdapter;
    private int _currentPageNo = 1;
    private String _id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_s13_topic);
        recyclerView = (RecyclerView) findViewById(R.id.s13_recyclerview);

        layoutManager = new GridLayoutManager(this, 2);

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {

                if(i == 0){
                    return 1;
                }
                return 2;
            }
        });

        mAdapter = new S13TopicAdapter(this);

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                doLoadMore();
            }
        });

        doRefresh();
    }

    public void onEventMainThread(String event) {
        if(S13TopicAdapter.ASK_FINISH.equals(event))finish();
    }

    public void doRefresh(){

        QSJsonObjectRequest jor = new QSJsonObjectRequest(QSAppWebAPI.getFeedingTopicApi(_id, 1, 10), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(MetadataParser.hasError(response)){
                    ErrorHandler.handle(S13TopicActivity.this, MetadataParser.getError(response));
                    return;
                }

                LinkedList<MongoShow> datas = ShowParser.parseQuery(response);

                mAdapter.resetDatas(datas);
                mAdapter.notifyDataSetChanged();
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jor);
    }

    public void doLoadMore(){

        QSJsonObjectRequest jor = new QSJsonObjectRequest(QSAppWebAPI.getFeedingTopicApi(_id, _currentPageNo + 1, 10), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(MetadataParser.hasError(response)){
                    ErrorHandler.handle(S13TopicActivity.this, MetadataParser.getError(response));
                    return;
                }

                _currentPageNo++;

                LinkedList<MongoShow> datas = ShowParser.parseQuery(response);

                mAdapter.addDatas(datas);
                mAdapter.notifyDataSetChanged();
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jor);
    }



    public void reconn() {

    }
}

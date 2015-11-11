package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.volley.Response;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S01ItemAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.ShowParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

public class S23SearchActivity extends BaseActivity implements BGARefreshLayout.BGARefreshLayoutDelegate, View.OnClickListener{

    private final int PAGESIZE = 20;
    @InjectView(R.id.s23_recyclerview)
    RecyclerView s23Recyclerview;
    @InjectView(R.id.s23_refresh)
    BGARefreshLayout mRefreshLayout;

    private S01ItemAdapter adapter;

    private int currentPageNo = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s23_search);
        ButterKnife.inject(this);
    }

    @Override
    public void reconn() {
        doRefresh();
    }

    public void doRefresh() {
        getDatasFromNet(1);
    }

    public void doLoadMore() {
        getDatasFromNet(currentPageNo);
    }

    public void getDatasFromNet(final int pageNo) {

        String url = "";

        Log.d("url:", url);
        if(pageNo == 1){
            adapter.clearData();
        }
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(S01MatchShowsActivity.class.getSimpleName(), "response:" + response);
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(S23SearchActivity.this, MetadataParser.getError(response));
                    mRefreshLayout.endLoadingMore();
                    mRefreshLayout.endRefreshing();
                    adapter.notifyDataSetChanged();
                    return;
                }

                List<MongoShow> datas = ShowParser.parseQuery_itemString(response);
                if (pageNo == 1) {
                    mRefreshLayout.endRefreshing();
                    adapter.addDataAtTop(datas);
                    currentPageNo = pageNo;
                } else {
                    mRefreshLayout.endLoadingMore();
                    adapter.addData(datas);
                }

                adapter.notifyDataSetChanged();
                currentPageNo++;
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.s23_back:
                finish();
                break;

        }
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout bgaRefreshLayout) {
        doRefresh();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout bgaRefreshLayout) {
        doLoadMore();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("S23SearchActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("S23SearchActivity");
        MobclickAgent.onPause(this);
    }
}

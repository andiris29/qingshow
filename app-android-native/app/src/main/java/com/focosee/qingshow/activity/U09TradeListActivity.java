package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.android.volley.Response;
import com.focosee.qingshow.Listener.EndlessRecyclerOnScrollListener;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.U09TradeListAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.TradeParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import org.json.JSONObject;
import java.util.LinkedList;

/**
 * Created by Administrator on 2015/3/13.
 */
public class U09TradeListActivity extends BaseActivity{

    private RecyclerView tradelist;
    private U09TradeListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private MongoPeople people;
    private int currentPageNo = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_person_tradelist);

        people = QSModel.INSTANCE.getUser();
        if(null == people){
            finish();
        }

        findViewById(R.id.person_activity_back_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tradelist = (RecyclerView) findViewById(R.id.person_activity_tradelist_recycleview);

        mAdapter = new U09TradeListAdapter(this);

//创建默认的线性LayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        tradelist.setLayoutManager(mLayoutManager);
//如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        tradelist.setHasFixedSize(true);
//创建并设置Adapter
        mAdapter.setOnViewHolderListener(new U09TradeListAdapter.OnViewHolderListener() {
            @Override
            public void onRequestedLastItem() {
                doLoadMore();

            }
        });
        tradelist.setAdapter(mAdapter);
        tradelist.addItemDecoration(mAdapter.getItemDecoration(20));

        tradelist.setOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                doLoadMore();
            }
        });

        doRefresh();

    }

    private void doRefresh(){
        getTradeFromNet(1, 10);
    }

    private void doLoadMore(){
        getTradeFromNet(currentPageNo + 1, 10);
    }

    private void getTradeFromNet(int pageNo, int pageSize){

        System.out.println(QSAppWebAPI.getTradeQueryApi(people._id, pageNo, pageSize));
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getTradeQueryApi(people._id, pageNo, pageSize), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if(MetadataParser.hasError(response)){
                    ErrorHandler.handle(U09TradeListActivity.this, MetadataParser.getError(response));
                    return;
                }
                LinkedList<MongoTrade> tradeList = TradeParser.parseQuery(response);
                if(currentPageNo == 1){
                    mAdapter.resetDatas(tradeList);
                } else {
                    currentPageNo++;
                    mAdapter.addDatas(tradeList);
                }
                mAdapter.notifyDataSetChanged();
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);

    }

    @Override
    public void reconn() {

    }
}

package com.focosee.qingshow.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.focosee.qingshow.widget.RecyclerView.SpacesItemDecoration;

import org.json.JSONObject;
import java.util.LinkedList;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/3/13.
 */
public class U09TradeListActivity extends BaseActivity{

    private final String TAG = "U09TradeListActivity";
    private RecyclerView tradelist;
    private U09TradeListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private MongoPeople people;
    private int currentPageNo = 1;
    private EventBus eventBus;

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

        mAdapter = new U09TradeListAdapter(new LinkedList<MongoTrade>(), U09TradeListActivity.this, R.layout.head_trade_list, R.layout.item_trade_list);

        mLayoutManager = new LinearLayoutManager(this);
        tradelist.setLayoutManager(mLayoutManager);

//如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        tradelist.setHasFixedSize(true);
        mAdapter.setOnViewHolderListener(new U09TradeListAdapter.OnViewHolderListener() {
            @Override
            public void onRequestedLastItem() {
                doLoadMore();

            }
        });
        tradelist.setAdapter(mAdapter);
        tradelist.addItemDecoration(new SpacesItemDecoration(10));

        tradelist.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                doLoadMore();
            }
        });


        doRefresh();

        eventBus = new EventBus();
        eventBus.register(this);

    }

    public void onEventMainThread(LinkedList<MongoTrade> event) {
        mAdapter.resetDatas(event);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
    }

    public void doRefresh(){
        getTradeFromNet(1, 10);
    }

    public void doLoadMore(){
        getTradeFromNet(currentPageNo, 10);
    }

    private void getTradeFromNet(int pageNo, int pageSize){

        final ProgressDialog progressDialog = new ProgressDialog(U09TradeListActivity.this);
        progressDialog.show();
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getTradeQueryApi(people._id, pageNo, pageSize), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "response:" + response);
                if(MetadataParser.hasError(response)){
                    ErrorHandler.handle(U09TradeListActivity.this, MetadataParser.getError(response));
                    progressDialog.dismiss();
                    return;
                }
                LinkedList<MongoTrade> tradeList = TradeParser.parseQuery(response);
                if(currentPageNo == 1){
                    mAdapter.resetDatas(tradeList);
                } else {
                    mAdapter.addDatas(tradeList);
                }
                currentPageNo++;
                mAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);

    }

    @Override
    public void reconn() {

    }
}

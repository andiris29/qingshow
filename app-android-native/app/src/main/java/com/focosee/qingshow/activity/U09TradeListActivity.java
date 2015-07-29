package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Response;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.U09TradeListAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.TradeParser;
import com.focosee.qingshow.httpapi.response.error.ErrorCode;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.focosee.qingshow.widget.RecyclerPullToRefreshView;
import com.focosee.qingshow.widget.RecyclerView.SpacesItemDecoration;

import org.json.JSONObject;

import java.util.LinkedList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/3/13.
 */
public class U09TradeListActivity extends BaseActivity implements View.OnClickListener{

    private final int TYPE_ALL = 0;
    private final int TYPE_RUNNING = 1;

    @InjectView(R.id.person_activity_back_image_button)
    ImageView backBtn;
    @InjectView(R.id.person_activity_tradelist_recyclerPullToRefreshView)
    RecyclerPullToRefreshView recyclerPullToRefreshView;
    @InjectView(R.id.U09_head_layout)
    LinearLayout u09HeadLayout;
    @InjectView(R.id.u09_tab_all)
    Button u09TabAll;
    @InjectView(R.id.u09_tab_running)
    Button u09TabRunning;
    private RecyclerView tradelist;
    private U09TradeListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private MongoPeople people;
    private int currentPageNo = 1;
    private EventBus eventBus;

    private View firstItem;
    private int currentType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_person_tradelist);
        ButterKnife.inject(this);

        people = QSModel.INSTANCE.getUser();
        if (null == people) {
            finish();
        }

        u09TabAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentType = 0;
            }
        });



        tradelist = recyclerPullToRefreshView.getRefreshableView();

        mAdapter = new U09TradeListAdapter(new LinkedList<MongoTrade>(), U09TradeListActivity.this, R.layout.head_trade_list, R.layout.item_trade_list);

        mLayoutManager = new LinearLayoutManager(this);
        tradelist.setLayoutManager(mLayoutManager);

//如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        tradelist.setHasFixedSize(true);
        mAdapter.setOnViewHolderListener(new U09TradeListAdapter.OnViewHolderListener() {
            @Override
            public void onRequestedLastItem() {
                doLoadMore(currentType);

            }
        });
        tradelist.setAdapter(mAdapter);
        tradelist.addItemDecoration(new SpacesItemDecoration(10));

        recyclerPullToRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                doRefresh(currentType);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                doLoadMore(currentType);
            }
        });

        tradelist.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(firstItem == null){
                    firstItem = recyclerView.getChildAt(0);
                }
                if (firstItem == recyclerView.getChildAt(0)) {
                    u09HeadLayout.setY(firstItem.getBottom() - firstItem.getHeight());
                } else
                    u09HeadLayout.setY(-u09HeadLayout.getHeight());
            }
        });

        doRefresh(currentType);

        eventBus = new EventBus();
        eventBus.register(this);

    }

    public void onEventMainThread(LinkedList<MongoTrade> event) {
        mAdapter.addDataAtTop(event);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
    }

    public void doRefresh(int type) {
        getTradeFromNet(type, 1, 10);
    }

    public void doLoadMore(int type) {
        getTradeFromNet(type, currentPageNo, 10);
    }

    private void getTradeFromNet(int type, int pageNo, int pageSize) {

        boolean inProgress  = false;

        if(type == 1){
            inProgress = true;
        }
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getTradeQueryApi(people._id, pageNo, pageSize, inProgress), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    if (MetadataParser.getError(response) == ErrorCode.PagingNotExist)
                        recyclerPullToRefreshView.setHasMoreData(false);
                    else {
                        ErrorHandler.handle(U09TradeListActivity.this, MetadataParser.getError(response));
                        recyclerPullToRefreshView.onPullUpRefreshComplete();
                    }
                    return;
                }
                LinkedList<MongoTrade> tradeList = TradeParser.parseQuery(response);
                if (currentPageNo == 1) {
                    mAdapter.addDataAtTop(tradeList);
                } else {
                    mAdapter.addData(tradeList);
                }
                currentPageNo++;
                mAdapter.notifyDataSetChanged();
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);

    }

    @Override
    public void reconn() {
        doRefresh(currentType);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.person_activity_back_image_button:
                finish();
                break;
            case R.id.u09_tab_all:
                doRefresh(0);
                currentType = 0;
                break;
            case R.id.u09_tab_running:
                doRefresh(1);
                currentType = 1;
                break;
        }
    }
}

package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.focosee.qingshow.util.TradeUtil;
import com.focosee.qingshow.widget.LoadingDialog;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.focosee.qingshow.widget.RecyclerPullToRefreshView;
import com.focosee.qingshow.widget.RecyclerView.SpacesItemDecoration;
import org.json.JSONObject;
import java.util.LinkedList;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/3/13.
 */
public class U09TradeListActivity extends MenuActivity {

    private final int TYPE_ALL = 0;
    private final int TYPE_RUNNING = 1;

    @InjectView(R.id.person_activity_tradelist_recyclerPullToRefreshView)
    RecyclerPullToRefreshView recyclerPullToRefreshView;
    @InjectView(R.id.U09_head_layout)
    LinearLayout u09HeadLayout;
    @InjectView(R.id.u09_tab_all)
    Button u09TabAll;
    @InjectView(R.id.u09_tab_running)
    Button u09TabRunning;
    @InjectView(R.id.backTop_btn)
    ImageView backTopBtn;
    @InjectView(R.id.navigation_btn_discount)
    ImageButton navigationBtnDiscount;
    @InjectView(R.id.navigation_btn_discount_tv)
    TextView navigationBtnDiscountTv;
    private RecyclerView tradelist;
    private U09TradeListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private String peopleId;
    private int currentPageNo = 1;
    private EventBus eventBus;

    private View firstItem;
    private int currentType = 1;
    private boolean isRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_person_tradelist);
        ButterKnife.inject(this);
        initDrawer();
        navigationBtnDiscount.setImageResource(R.drawable.root_menu_discount_gray);
        navigationBtnDiscountTv.setTextColor(getResources().getColor(R.color.darker_gray));
        peopleId = QSModel.INSTANCE.getUserId();
        if (null == peopleId) {
            finish();
        }

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
                if (dy < 0 && firstItem != recyclerView.getChildAt(0)) {
                    backTopBtn.setVisibility(View.VISIBLE);
                } else {
                    backTopBtn.setVisibility(View.GONE);
                }
                if (firstItem == null) {
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

        getTradeFromNet(type, 1, 100);
    }

    public void doLoadMore(int type) {
        getTradeFromNet(type, currentPageNo, 10);
    }

    private void getTradeFromNet(int type, final int pageNo, int pageSize) {

        boolean inProgress = false;

        if (type == TYPE_RUNNING) {
            inProgress = true;
        }

        final LoadingDialog pDialog = new LoadingDialog(getSupportFragmentManager());
        if(1 == pageNo)
            pDialog.show(U09TradeListActivity.class.getSimpleName());
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getTradeQueryApi(peopleId, pageNo, pageSize, inProgress), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(1 == pageNo)
                    pDialog.dismiss();
                if (MetadataParser.hasError(response)) {
                    recyclerPullToRefreshView.onPullDownRefreshComplete();
                    if (MetadataParser.getError(response) == ErrorCode.PagingNotExist) {
                        recyclerPullToRefreshView.setHasMoreData(false);
                        if (isRunning) {
                            doRefresh(TYPE_ALL);
                            currentType = TYPE_ALL;
                            isRunning = false;
                        }
                    } else {
                        ErrorHandler.handle(U09TradeListActivity.this, MetadataParser.getError(response));
                        recyclerPullToRefreshView.onPullUpRefreshComplete();
                    }
                    return;
                }
                if (isRunning) {
                    clickTabRunning();
                    currentType = TYPE_ALL;
                    isRunning = false;
                }
                List<MongoTrade> tradeList = TradeParser.parseQuery(response);
                tradeList = TradeUtil.tradelistSort(tradeList);
                if (pageNo == 1) {
                    mAdapter.addDataAtTop(tradeList);
                    recyclerPullToRefreshView.onPullDownRefreshComplete();
                    currentPageNo = 1;
                } else {
                    mAdapter.addData(tradeList);
                    recyclerPullToRefreshView.onPullUpRefreshComplete();
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
        super.onClick(v);
        switch (v.getId()) {
            case R.id.person_activity_back_image_button:
                menuSwitch();
                break;
            case R.id.u09_tab_all:
                currentType = TYPE_ALL;
                clickTabAll();
                doRefresh(TYPE_ALL);
                break;
            case R.id.u09_tab_running:
                currentType = TYPE_RUNNING;
                clickTabRunning();
                doRefresh(TYPE_RUNNING);
                break;
            case R.id.backTop_btn:
                tradelist.smoothScrollToPosition(0);
                break;
        }
    }

    private void clickTabAll() {
        u09TabAll.setBackgroundResource(R.drawable.s01_tab_btn1);
        u09TabAll.setTextColor(getResources().getColor(R.color.white));
        u09TabRunning.setBackgroundResource(R.drawable.s01_tab_border1);
        u09TabRunning.setTextColor(getResources().getColor(R.color.master_pink));
    }

    private void clickTabRunning() {
        u09TabAll.setBackgroundResource(R.drawable.s01_tab_border2);
        u09TabAll.setTextColor(getResources().getColor(R.color.master_pink));
        u09TabRunning.setBackgroundResource(R.drawable.s01_tab_btn2);
        u09TabRunning.setTextColor(getResources().getColor(R.color.white));
    }
}

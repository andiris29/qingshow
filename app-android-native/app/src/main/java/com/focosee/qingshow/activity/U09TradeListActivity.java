package com.focosee.qingshow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
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
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.util.RecyclerViewUtil;
import com.focosee.qingshow.util.ValueUtil;
import com.focosee.qingshow.widget.LoadingDialogs;
import com.focosee.qingshow.widget.RecyclerView.SpacesItemDecoration;
import com.focosee.qingshow.wxapi.ShareTradeEvent;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/3/13.
 */
public class U09TradeListActivity extends MenuActivity implements BGARefreshLayout.BGARefreshLayoutDelegate {

    public static final String responseToStatusToSuccessed = "responseToStatusToSuccessed";
    public static final String FROM_WHERE = "FROM_WHEN";
    public static final String PUSH_NOTIFICATION = "PUSH_NOTIFICATION";
    private final String CURRENT_POSITION = "CURRENT_POSITION";
    private final int TYPE_APPLY = 0;
    private final int TYPE_SUCCESSED = 1;

    @InjectView(R.id.U09_head_layout)
    LinearLayout u09HeadLayout;
    @InjectView(R.id.u09_tab_all)
    Button u09TabAll;
    @InjectView(R.id.u09_tab_running)
    Button u09TabRunning;
    @InjectView(R.id.backTop_btn)
    ImageButton backTopBtn;
    @InjectView(R.id.navigation_btn_discount)
    ImageButton navigationBtnDiscount;
    @InjectView(R.id.navigation_btn_discount_tv)
    TextView navigationBtnDiscountTv;
    @InjectView(R.id.u09_recyclerview)
    RecyclerView recyclerView;
    @InjectView(R.id.u09_refresh)
    BGARefreshLayout mRefreshLayout;
    private U09TradeListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private String peopleId;
    private int currentPageNo = 1;

    private View firstItem;
    private int currentType = TYPE_APPLY;
    private boolean isSuccessed = true;

    private String fromWhere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_person_tradelist);
        ButterKnife.inject(this);
        if (savedInstanceState != null) {
            position = savedInstanceState.getInt("position", Integer.MAX_VALUE);
        }
        fromWhere = getIntent().getStringExtra(FROM_WHERE);
        initDrawer();
        initCurrentType();
        navigationBtnDiscount.setImageResource(R.drawable.root_menu_discount_gray);
        navigationBtnDiscountTv.setTextColor(getResources().getColor(R.color.darker_gray));
        peopleId = QSModel.INSTANCE.getUserId();
        if (null == peopleId) {
            finish();
        }

        mAdapter = new U09TradeListAdapter(new LinkedList<MongoTrade>(), U09TradeListActivity.this, R.layout.head_trade_list, R.layout.item_trade_list);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration(10));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (firstItem == null) {
                    firstItem = recyclerView.getChildAt(0);
                }
                if (firstItem == recyclerView.getChildAt(0)) {
                    u09HeadLayout.setY(firstItem.getBottom() - firstItem.getHeight());
                } else
                    u09HeadLayout.setY(-u09HeadLayout.getHeight());
            }
        });

        RecyclerViewUtil.setBackTop(recyclerView, backTopBtn, mLayoutManager);
        initRefreshLayout();
        doRefresh(currentType);
        EventBus.getDefault().register(this);
        new EventBus().register(this);
    }

    private void initCurrentType() {
        if (S10ItemDetailActivity.class.getSimpleName().equals(fromWhere)) {
            currentType = TYPE_APPLY;
            clickTabApply();
        }
        if (S17PayActivity.class.getSimpleName().equals(fromWhere)) {
            currentType = TYPE_SUCCESSED;
            clickTabSuccessed();
        }
        if (PUSH_NOTIFICATION.equals(fromWhere)) {
            currentType = TYPE_SUCCESSED;
            clickTabSuccessed();
        }
    }

    private void initRefreshLayout() {
        mRefreshLayout.setDelegate(this);
        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(this, true);
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);
    }

    public void onEventMainThread(LinkedList<MongoTrade> event) {
        mAdapter.addDataAtTop(event);
        mAdapter.notifyDataSetChanged();
    }

    public void onEventMainThread(String event) {
        if (responseToStatusToSuccessed.equals(event)) doRefresh(currentType);
    }

    public void onEventMainThread(ShareTradeEvent event) {
        if (!event.shareByCreateUser) {
            return;
        }
        if (position == Integer.MAX_VALUE) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("_id", mAdapter.getItemData(position)._id);
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getTradeShareApi(), new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    return;
                }
                mAdapter.getItemData(position).__context.sharedByCurrentUser = true;
                mAdapter.notifyDataSetChanged();
                Intent intent = new Intent(U09TradeListActivity.this, S17PayActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(S17PayActivity.INPUT_ITEM_ENTITY, mAdapter.getItemData(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private int position = Integer.MAX_VALUE;//当前分享并支付的trader的position


    public void onEventMainThread(MongoTrade trade) {
        if (mAdapter.indexOf(trade) > -1)
            this.position = mAdapter.indexOf(trade) + 1;
    }

    public void onEventMainThread(U12ReturnEvent event) {
        doRefresh(currentType);
        if(recyclerView.getAdapter().getItemCount() - 1 < position)return;
        recyclerView.scrollToPosition(event.position);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("position", position);
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void doRefresh(int type) {
        getTradeFromNet(type, 1, 10);
    }

    public void doLoadMore(int type) {
        getTradeFromNet(type, currentPageNo, 10);
    }

    private void getTradeFromNet(int type, final int pageNo, int pageSize) {

        String phases;

        if (type == TYPE_SUCCESSED) {
            phases = ValueUtil.phases_finish;
        } else {
            phases = ValueUtil.phases_apply;
        }

        final LoadingDialogs pDialog = new LoadingDialogs(this, R.style.dialog);
        if (pageNo == 1) {
            pDialog.show();
        }
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getTradeQuerybyPhaseApi(phases, pageNo, pageSize), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (pageNo == 1) {
                    pDialog.dismiss();
                }
                if (MetadataParser.hasError(response)) {
                    if (MetadataParser.getError(response) == ErrorCode.PagingNotExist) {
                        if(pageNo == 1){
                            mAdapter.clearData();
                            mAdapter.notifyDataSetChanged();
                        }
                        if (isSuccessed) {//进入u09时的第一次请求
                            clickTabApply();
                            doRefresh(TYPE_APPLY);
                            currentType = TYPE_APPLY;
                            isSuccessed = false;
                        }
                    } else {
                        ErrorHandler.handle(U09TradeListActivity.this, MetadataParser.getError(response));
                    }
                    mRefreshLayout.endRefreshing();
                    mRefreshLayout.endLoadingMore();
                    return;
                }

                List<MongoTrade> tradeList = TradeParser.parseQuery(response);
                if (pageNo == 1) {
                    mAdapter.addDataAtTop(tradeList);
                    mRefreshLayout.endRefreshing();
                    currentPageNo = 1;
                } else {
                    mAdapter.addData(tradeList);
                    mRefreshLayout.endLoadingMore();
                }
                currentPageNo++;
                mAdapter.notifyDataSetChanged();
                isSuccessed = false;
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
                currentPageNo = 1;
                currentType = TYPE_APPLY;
                clickTabApply();
                doRefresh(TYPE_APPLY);
                break;
            case R.id.u09_tab_running:
                currentPageNo = 1;
                currentType = TYPE_SUCCESSED;
                clickTabSuccessed();
                doRefresh(TYPE_SUCCESSED);
                break;
            case R.id.backTop_btn:
                recyclerView.scrollToPosition(0);
                break;
        }
    }

    private void clickTabApply() {
        u09TabAll.setBackgroundResource(R.drawable.s01_tab_btn1);
        u09TabAll.setTextColor(getResources().getColor(R.color.white));
        u09TabRunning.setBackgroundResource(R.drawable.s01_tab_border1);
        u09TabRunning.setTextColor(getResources().getColor(R.color.master_pink));
    }

    private void clickTabSuccessed() {
        u09TabAll.setBackgroundResource(R.drawable.s01_tab_border2);
        u09TabAll.setTextColor(getResources().getColor(R.color.master_pink));
        u09TabRunning.setBackgroundResource(R.drawable.s01_tab_btn2);
        u09TabRunning.setTextColor(getResources().getColor(R.color.white));
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout bgaRefreshLayout) {
        doRefresh(currentType);
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout bgaRefreshLayout) {
        doLoadMore(currentType);
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("U09TradeListActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("U09TradeListActivity");
        MobclickAgent.onPause(this);
    }
}

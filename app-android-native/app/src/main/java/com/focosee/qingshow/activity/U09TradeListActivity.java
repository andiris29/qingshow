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
import com.focosee.qingshow.model.EventModel;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.TradeModel;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.util.RecyclerViewUtil;
import com.focosee.qingshow.util.TradeUtil;
import com.focosee.qingshow.widget.LoadingDialog;
import com.focosee.qingshow.widget.RecyclerView.SpacesItemDecoration;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import org.json.JSONObject;
import java.util.LinkedList;
import java.util.List;
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
    private final int TYPE_ALL = 0;
    private final int TYPE_RUNNING = 1;

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

        mAdapter = new U09TradeListAdapter(new LinkedList<MongoTrade>(), U09TradeListActivity.this, R.layout.head_trade_list, R.layout.item_trade_list);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

//如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
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
        mRefreshLayout.beginRefreshing();

        EventBus.getDefault().register(this);
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

    public void onEventMainThread(EventModel<Integer> event) {
        if (event.tag == U09TradeListActivity.class) {
//            mAdapter.getItemData(sharePosition).__context.sharedByCurrentUser = true;
            if (event.msg != SendMessageToWX.Resp.ErrCode.ERR_OK) {
                Toast.makeText(U09TradeListActivity.this, "分享失败，请重试。", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(U09TradeListActivity.this, S17PayActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(S17PayActivity.INPUT_ITEM_ENTITY, TradeModel.INSTANCE.getTrade());
            intent.putExtras(bundle);
            startActivity(intent);
        }
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

        boolean inProgress = false;

        if (type == TYPE_RUNNING) {
            inProgress = true;
        }

        final LoadingDialog pDialog = new LoadingDialog(getSupportFragmentManager());
        if (currentPageNo == 1)
            pDialog.show(U09TradeListActivity.class.getSimpleName());
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getTradeQueryApi(peopleId, pageNo, pageSize, inProgress), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("response:" + response);
                if (currentPageNo == 1)
                    pDialog.dismiss();
                if (MetadataParser.hasError(response)) {
                    if (MetadataParser.getError(response) == ErrorCode.PagingNotExist) {
                        if (isRunning) {//进入u09时的第一次请求
                            clickTabRunning();
                            doRefresh(TYPE_ALL);
                            currentType = TYPE_ALL;
                            isRunning = false;
                        }
                    } else {
                        ErrorHandler.handle(U09TradeListActivity.this, MetadataParser.getError(response));
                    }
                    mRefreshLayout.endRefreshing();
                    mRefreshLayout.endLoadingMore();
                    return;
                }
                if (isRunning) {//进入u09时的第一次请求
                    clickTabRunning();
                    currentType = TYPE_RUNNING;
                }

                List<MongoTrade> tradeList = TradeParser.parseQuery(response);
                tradeList = TradeUtil.tradelistSort(tradeList);
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
                isRunning = false;
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
                currentType = TYPE_ALL;
                clickTabAll();
                doRefresh(TYPE_ALL);
                break;
            case R.id.u09_tab_running:
                currentPageNo = 1;
                currentType = TYPE_RUNNING;
                clickTabRunning();
                doRefresh(TYPE_RUNNING);
                break;
            case R.id.backTop_btn:
                recyclerView.scrollToPosition(0);
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

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout bgaRefreshLayout) {
        doRefresh(currentType);
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout bgaRefreshLayout) {
        doLoadMore(currentType);
        return false;
    }
}

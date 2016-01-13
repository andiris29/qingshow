package com.focosee.qingshow.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.U09TradeListAdapter;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.constants.config.QSPushAPI;
import com.focosee.qingshow.httpapi.QSRxApi;
import com.focosee.qingshow.httpapi.request.QSSubscriber;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.receiver.PushGuideEvent;
import com.focosee.qingshow.util.RecyclerViewUtil;
import com.focosee.qingshow.util.ValueUtil;
import com.focosee.qingshow.util.user.UnreadHelper;
import com.focosee.qingshow.widget.LoadingDialogs;
import com.focosee.qingshow.widget.MenuView;
import com.focosee.qingshow.widget.RecyclerView.SpacesItemDecoration;
import com.umeng.analytics.MobclickAgent;

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
public class U09TradeListActivity extends BaseActivity implements BGARefreshLayout.BGARefreshLayoutDelegate, View.OnClickListener {

    public static final String responseToStatusToSuccessed = "responseToStatusToSuccessed";
    public static final String FROM_WHERE = "FROM_WHEN";
    public static final String PUSH_NOTIFICATION = "PUSH_NOTIFICATION";

    @InjectView(R.id.person_activity_back_image_button)
    ImageButton menu;
    @InjectView(R.id.U09_head_layout)
    LinearLayout u09HeadLayout;
    @InjectView(R.id.backTop_btn)
    ImageButton backTopBtn;
    @InjectView(R.id.u09_recyclerview)
    RecyclerView recyclerView;
    @InjectView(R.id.u09_refresh)
    BGARefreshLayout mRefreshLayout;
    @InjectView(R.id.container)
    FrameLayout container;
    @InjectView(R.id.tel_relative)
    RelativeLayout telRelative;
    private U09TradeListAdapter mAdapter;
    private int currentPageNo = 1;

    private View firstItem;
    private boolean isSuccessed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_person_tradelist);
        ButterKnife.inject(this);
        if (savedInstanceState != null) {
            position = savedInstanceState.getInt("position", Integer.MAX_VALUE);
        }
        mAdapter = new U09TradeListAdapter(new LinkedList<MongoTrade>(), U09TradeListActivity.this, R.layout.head_trade_list, R.layout.item_trade_list);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
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
        doRefresh();
        UserCommand.refresh();
        EventBus.getDefault().register(this);
        new EventBus().register(this);

        telRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:4007501010")));
            }
        });
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
        if (responseToStatusToSuccessed.equals(event) || ValueUtil.SUBMIT_TRADE_SUCCESSED.equals(event))
            doRefresh();

        if (position == Integer.MAX_VALUE || position >= mAdapter.getItemCount()) {
            return;
        }
        if (mAdapter.getItemData(position)._id.equals(event)) {
            mAdapter.notifyDataSetChanged();
            return;
        }
        if (ValueUtil.PAY_FINISHED.equals(event)) {
            reconn();
        }
    }

    private int position = Integer.MAX_VALUE;//当前分享并支付的trader的position

    public void onEventMainThread(MongoTrade trade) {
        if (mAdapter.indexOf(trade) > -1)
            this.position = mAdapter.indexOf(trade) + 1;
    }

    public void onEventMainThread(U12ReturnEvent event) {
        doRefresh();
        if (recyclerView.getAdapter().getItemCount() - 1 < position) return;
        recyclerView.scrollToPosition(event.position);
    }

    public void onEventMainThread(PushGuideEvent event) {
        if (event.unread) {
            if (event.command.equals(QSPushAPI.TRADE_SHIPPED)) {
                reconn();
            }
        } else {
            if (!UnreadHelper.hasUnread()) {
                menu.setImageResource(R.drawable.nav_btn_menu_n);
            }
        }
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

    public void doRefresh() {
        getTradeFromNet(1, 10);
    }

    public void doLoadMore() {
        getTradeFromNet(currentPageNo, 10);
    }

    private void getTradeFromNet(final int pageNo, int pageSize) {

        final LoadingDialogs pDialog = new LoadingDialogs(this, R.style.dialog);
        if (pageNo == 1) {
            pDialog.show();
        }
        QSRxApi.tradeOwn(pageNo, pageSize)
                .subscribe(new QSSubscriber<List<MongoTrade>>() {
                    @Override
                    public void onNetError(int message) {
                        if (pageNo == 1) {
                            mAdapter.clearData();
                            mAdapter.notifyDataSetChanged();
                        }
                        if (isSuccessed) {//进入u09时的第一次请求
                            isSuccessed = false;
                        }
                        mRefreshLayout.endRefreshing();
                        mRefreshLayout.endLoadingMore();
                        pDialog.dismiss();
                        ErrorHandler.handle(U09TradeListActivity.this, message);
                    }

                    @Override
                    public void onNext(List<MongoTrade> tradeList) {
                        pDialog.dismiss();
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
    }

    @Override
    public void reconn() {
        doRefresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.person_activity_back_image_button:
                menu.setImageResource(R.drawable.nav_btn_menu_n);
                MenuView menuView = new MenuView();
                menuView.show(getSupportFragmentManager(), U09TradeListActivity.class.getSimpleName(), container);
                break;
            case R.id.backTop_btn:
                recyclerView.scrollToPosition(0);
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
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("U09TradeListActivity");
        MobclickAgent.onResume(this);
        if (UnreadHelper.hasUnread()) {
            menu.setImageResource(R.drawable.nav_btn_menu_n_dot);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("U09TradeListActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
        }
        return true;
    }
}

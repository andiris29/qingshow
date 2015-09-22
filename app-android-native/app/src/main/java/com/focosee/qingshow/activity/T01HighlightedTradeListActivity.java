package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.receiver.PushGuideEvent;
import com.focosee.qingshow.util.ValueUtil;
import com.focosee.qingshow.widget.MenuView;
import com.focosee.qingshow.widget.RecyclerView.*;
import com.android.volley.Response;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.T01HihghtedTradeListAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.TradeParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.widget.QSTextView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

public class T01HighlightedTradeListActivity extends BaseActivity implements BGARefreshLayout.BGARefreshLayoutDelegate {

    private static int PAGE_SIZE = 10;

    @InjectView(R.id.left_btn)
    ImageButton leftBtn;
    @InjectView(R.id.title)
    QSTextView title;
    @InjectView(R.id.t01_recycler)
    RecyclerView t01Recycler;
    @InjectView(R.id.t01_refresh)
    BGARefreshLayout mRefreshLayout;
    @InjectView(R.id.container)
    FrameLayout contrainer;

    private T01HihghtedTradeListAdapter adapter;
    private int currentPageNo = 1;
    private MenuView menuView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_t01_highlighted_trade_list);
        ButterKnife.inject(this);
        initRefreshLayout();
        mRefreshLayout.beginRefreshing();
        title.setText(getText(R.string.title_t01));
        leftBtn.setImageResource(R.drawable.nav_btn_menu_n);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftBtn.setImageResource(R.drawable.nav_btn_menu_n);
                menuView = new MenuView();
                menuView.show(getSupportFragmentManager(), T01HighlightedTradeListActivity.class.getSimpleName(), contrainer);
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(T01HighlightedTradeListActivity.this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        t01Recycler.setLayoutManager(manager);
        adapter = new T01HihghtedTradeListAdapter(new ArrayList<MongoTrade>()
                , T01HighlightedTradeListActivity.this, R.layout.item_t01_higthlighted_tradelist);

        t01Recycler.setAdapter(adapter);

        t01Recycler.addItemDecoration(new SpacesItemDecoration(10));
    }

    private void initRefreshLayout() {
        mRefreshLayout.setDelegate(this);
        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(this, true);
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);
    }

    private void doRefresh() {
        getHighlightedTrades(1);
    }

    private void doLoadMore() {
        getHighlightedTrades(currentPageNo);
    }

    private void getHighlightedTrades(final int pageNo) {

        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getTradeQueryHighlightedApi(pageNo, PAGE_SIZE)
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(T01HighlightedTradeListActivity.class.getSimpleName(), "response:" + response);
                if (MetadataParser.hasError(response)) {
                    mRefreshLayout.endRefreshing();
                    mRefreshLayout.endLoadingMore();
                    ErrorHandler.handle(T01HighlightedTradeListActivity.this, MetadataParser.getError(response));
                    return;
                }

                List<MongoTrade> trades = TradeParser.parseQuery(response);
                if (pageNo == 1) {
                    mRefreshLayout.endRefreshing();
                    adapter.addDataAtTop(trades);
                    currentPageNo = 1;
                } else {
                    mRefreshLayout.endLoadingMore();
                    adapter.addData(trades);
                }
                currentPageNo++;
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    @Override
    public void reconn() {

    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        doRefresh();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        doLoadMore();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("S20MatcherActivity");
        MobclickAgent.onResume(this);
        if (!TextUtils.isEmpty(QSApplication.instance().getPreferences().getString(ValueUtil.NEED_GUIDE, ""))) {
            leftBtn.setImageResource(R.drawable.nav_btn_menu_n_dot);
        }
    }

    public void onEventMainThread(PushGuideEvent event) {
        if (event.unread) {
            leftBtn.setImageResource(R.drawable.nav_btn_menu_n_dot);
        }else{
            leftBtn.setImageResource(R.drawable.nav_btn_menu_n);
        }
    }
}

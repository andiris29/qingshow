package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
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
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

public class T01HighlightedTradeListActivity extends MenuActivity implements BGARefreshLayout.BGARefreshLayoutDelegate {

    private static int PAGE_SIZE = 10;

    @InjectView(R.id.left_btn)
    ImageButton leftBtn;
    @InjectView(R.id.title)
    QSTextView title;
    @InjectView(R.id.t01_recycler)
    RecyclerView t01Recycler;
    @InjectView(R.id.t01_refresh)
    BGARefreshLayout mRefreshLayout;
    @InjectView(R.id.u01_bonusList)
    ImageButton nav_bonusList;
    @InjectView(R.id.u01_bonusList_tv)
    TextView nav_bonusListTV;

    private T01HihghtedTradeListAdapter adapter;
    private int currentPageNo = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_t01_highlighted_trade_list);
        ButterKnife.inject(this);
        nav_bonusList.setImageResource(R.drawable.root_menu_highted_gray);
        nav_bonusListTV.setTextColor(getResources().getColor(R.color.darker_gray));
        initDrawer();
        initRefreshLayout();
        mRefreshLayout.beginRefreshing();
        title.setText(getText(R.string.title_t01));
        leftBtn.setImageResource(R.drawable.nav_btn_menu_n);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuSwitch();
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
                System.out.println("response:" + response);
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
}

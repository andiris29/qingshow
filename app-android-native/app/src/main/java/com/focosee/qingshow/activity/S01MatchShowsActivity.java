package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.volley.Response;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S01ItemAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.ShowParser;
import com.focosee.qingshow.httpapi.response.error.ErrorCode;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.TimeUtil;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.focosee.qingshow.widget.RecyclerPullToRefreshView;

import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class S01MatchShowsActivity extends MenuActivity {

    private static final String TAG = "S01MatchShowsActivity";
    @InjectView(R.id.s01_backTop_btn)
    ImageView s01BackTopBtn;
    @InjectView(R.id.navigation_btn_match)
    ImageButton navigationBtnMatch;

    private int TYPE_HOT = 0;
    private int TYPE_NEW = 1;
    private final int PAGESIZE = 20;

    @InjectView(R.id.s01_menu_btn)
    ImageView s01MenuBtn;
    @InjectView(R.id.s01_tab_hot)
    Button s01TabHot;
    @InjectView(R.id.s01_recyclerView)
    RecyclerPullToRefreshView recyclerPullToRefreshView;
    @InjectView(R.id.s01_tab_new)
    Button s01TabNew;

    private S01ItemAdapter adapter;
    private RecyclerView recyclerView;
    private int currentPageNo = 1;
    private int currentType = TYPE_HOT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s01_match_shows);
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);
        initDrawer();
        navigationBtnMatch.setImageResource(R.drawable.root_menu_icon_meida_gray);
        s01MenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuSwitch();
            }
        });
        recyclerView = recyclerPullToRefreshView.getRefreshableView();
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
        LinearLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new S01ItemAdapter(new LinkedList<MongoShow>(), this, R.layout.item_s01_matchlist);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy < 0) {
                    s01BackTopBtn.setVisibility(View.VISIBLE);
                } else {
                    s01BackTopBtn.setVisibility(View.GONE);
                }
            }
        });
        s01BackTopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
            }
        });
        recyclerPullToRefreshView.doPullRefreshing(true, 0);
    }

    @Override
    public void reconn() {
        doRefresh(currentType);
    }

    public void doRefresh(int type) {
        getDatasFromNet(type, 1);
    }

    public void doLoadMore(int type) {
        getDatasFromNet(type, currentPageNo);
    }

    public void getDatasFromNet(int type, final int pageNo) {

        String url = type == TYPE_HOT ? QSAppWebAPI.getMatchHotApi(pageNo, PAGESIZE) : QSAppWebAPI.getMatchNewApi(pageNo, PAGESIZE);

        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    if (MetadataParser.getError(response) == ErrorCode.PagingNotExist)
                        recyclerPullToRefreshView.setHasMoreData(false);
                    else {
                        ErrorHandler.handle(S01MatchShowsActivity.this, MetadataParser.getError(response));
                        recyclerPullToRefreshView.onPullUpRefreshComplete();
                    }
                    recyclerPullToRefreshView.onPullDownRefreshComplete();
                    return;
                }

                List<MongoShow> datas = ShowParser.parseQuery_categoryString(response);
                if (pageNo == 1) {
                    adapter.addDataAtTop(datas);
                    currentPageNo = pageNo;
                } else {
                    adapter.addData(datas);
                }

                setLastUpdateTime();
                adapter.notifyDataSetChanged();
                recyclerPullToRefreshView.onPullDownRefreshComplete();
                recyclerPullToRefreshView.onPullUpRefreshComplete();
                currentPageNo++;
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void setLastUpdateTime() {
        String text = TimeUtil.formatDateTime(System.currentTimeMillis());
        recyclerPullToRefreshView.setLastUpdatedLabel(text);
    }

    public void onEventMainThread(String event) {
        if (event.equals("refresh")) {
            recyclerPullToRefreshView.doPullRefreshing(true, 0);
        }
    }

    @Override
    public void onClick(View v) {
        recyclerView.scrollToPosition(0);
        if (v.getId() == R.id.s01_tab_hot) {
            currentType = TYPE_HOT;
            recyclerPullToRefreshView.doPullRefreshing(true, 500);
            s01TabHot.setBackgroundResource(R.drawable.s01_tab_btn1);
            s01TabHot.setTextColor(getResources().getColor(R.color.white));
            s01TabNew.setBackgroundResource(R.drawable.s01_tab_border1);
            s01TabNew.setTextColor(getResources().getColor(R.color.master_pink));
            return;
        }
        if (v.getId() == R.id.s01_tab_new) {
            currentType = TYPE_NEW;
            recyclerPullToRefreshView.doPullRefreshing(true, 500);
            s01TabHot.setBackgroundResource(R.drawable.s01_tab_border2);
            s01TabHot.setTextColor(getResources().getColor(R.color.master_pink));
            s01TabNew.setBackgroundResource(R.drawable.s01_tab_btn2);
            s01TabNew.setTextColor(getResources().getColor(R.color.white));
            return;
        }
        super.onClick(v);
    }

    @Override
    protected void onResume() {
        doRefresh(currentType);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}

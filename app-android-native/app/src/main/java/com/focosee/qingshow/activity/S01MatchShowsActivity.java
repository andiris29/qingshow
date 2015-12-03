package com.focosee.qingshow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.Response;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S01ItemAdapter;
import com.focosee.qingshow.adapter.S01MatchNewAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.QSRxApi;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.QSSubscriber;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.ShowParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.vo.aggregation.FeedingAggregation;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.receiver.PushGuideEvent;
import com.focosee.qingshow.util.RecyclerViewUtil;
import com.focosee.qingshow.util.TimeUtil;
import com.focosee.qingshow.util.user.UnreadHelper;
import com.focosee.qingshow.widget.MenuView;
import com.focosee.qingshow.widget.QSButton;
import com.squareup.timessquare.CalendarPickerView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import de.greenrobot.event.EventBus;

public class S01MatchShowsActivity extends BaseActivity implements BGARefreshLayout.BGARefreshLayoutDelegate, View.OnClickListener {

    public static final String S1_INPUT_SHOWABLE = "INPUT_SHOWABLE";
    public static final String S1_INPUT_TRADEID_NOTIFICATION = "S1_INPUT_TRADEID_NOTIFICATION";
    public static final String INTENT_CURRENT_TYPE = "intent_current_type";

    @InjectView(R.id.s01_backTop_btn)
    ImageButton s01BackTopBtn;
    @InjectView(R.id.s01_refresh)
    BGARefreshLayout mRefreshLayout;
    @InjectView(R.id.s01_recyclerview)
    RecyclerView recyclerView;
    @InjectView(R.id.container)
    FrameLayout container;
    @InjectView(R.id.s01_tab_feature)
    QSButton s01TabFeature;
    @InjectView(R.id.calendar)
    CalendarPickerView calendarPicker;

    private int TYPE_HOT = 0;
    private int TYPE_NEW = 1;
    private int TYPE_FEATURED = 2;
    private final int PAGESIZE = 30;

    @InjectView(R.id.s01_menu_btn)
    ImageView s01MenuBtn;
    @InjectView(R.id.s01_tab_hot)
    Button s01TabHot;
    @InjectView(R.id.s01_tab_new)
    Button s01TabNew;
    @InjectView(R.id.head)
    RelativeLayout head;
    @InjectView(R.id.home_time)
    ImageView timeBtn;

    private S01ItemAdapter adapter;
    private S01MatchNewAdapter matchNewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private int currentPageNo = 1;
    private int currentType = TYPE_FEATURED;

    private GregorianCalendar calendar;


    private MenuView menuView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s01_match_shows);
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);
        if (null != getIntent()) {
            currentType = getIntent().getIntExtra(INTENT_CURRENT_TYPE, TYPE_FEATURED);
            if (currentType == TYPE_NEW) clickTabNew();
        }
        initRefreshLayout();
        calendar = new GregorianCalendar();
        calendarPicker.init(new GregorianCalendar(2015, 1, 1).getTime(), new Date());
        initCalendar();
        calendarPicker.setVisibility(View.INVISIBLE);
        timeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarPicker.scrollToDate(calendar.getTime());
                if (calendarPicker.isShown()) calendarPicker.setVisibility(View.INVISIBLE);
                else calendarPicker.setVisibility(View.VISIBLE);
            }
        });
        s01MenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s01MenuBtn.setImageResource(R.drawable.nav_btn_menu_n);
                menuView = new MenuView();
                menuView.show(getSupportFragmentManager(), S01MatchShowsActivity.class.getSimpleName(), container);
            }
        });
        final GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new S01ItemAdapter(new LinkedList<MongoShow>(), this, R.layout.item_match);
        recyclerView.setAdapter(adapter);
        matchNewAdapter = new S01MatchNewAdapter(new LinkedList<FeedingAggregation>(), this, R.layout.item_matchnew);
        RecyclerViewUtil.setBackTop(recyclerView, s01BackTopBtn, layoutManager);
        mRefreshLayout.beginRefreshing();
    }

    private void initCalendar() {
        calendarPicker.scrollToDate(calendar.getTime());
        calendarPicker.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                calendar.setTime(date);
                mRefreshLayout.beginRefreshing();
            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });
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

    public void getDatasFromNet(final int type, final int pageNo) {

        String url = "";
        switch (type) {
            case 0:
                url = QSAppWebAPI.getMatchHotApi(pageNo, PAGESIZE);
                break;
            case 1:
                return;
            case 2:
                url = QSAppWebAPI.getFeedingFeatured(pageNo, PAGESIZE);
                break;
        }
        if (pageNo == 1) {
            adapter.clearData();
        }
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(S01MatchShowsActivity.this, MetadataParser.getError(response));
                    mRefreshLayout.endLoadingMore();
                    mRefreshLayout.endRefreshing();
                    adapter.notifyDataSetChanged();
                    return;
                }

                List<MongoShow> datas = ShowParser.parseQuery(response);
                if (pageNo == 1) {
                    mRefreshLayout.endRefreshing();
                    adapter.addDataAtTop(datas);
                    currentPageNo = pageNo;
                } else {
                    mRefreshLayout.endLoadingMore();
                    adapter.addData(datas);
                }

                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
                currentPageNo++;
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    public void onEventMainThread(String event) {
        if (event.equals("refresh")) {
            mRefreshLayout.beginRefreshing();
        }
    }

    public void onEventMainThread(PushGuideEvent event) {
        if (event.unread) {
            s01MenuBtn.setImageResource(R.drawable.nav_btn_menu_n_dot);
        } else {
            if (!UnreadHelper.hasUnread()) {
                s01MenuBtn.setImageResource(R.drawable.nav_btn_menu_n);
            }
        }
    }

    public void onEventMainThread(ShowCollectionEvent event) {
        if (event.position != Integer.MAX_VALUE) {
            adapter.setItem(event.position, event.show);
            adapter.notifyDataSetChanged();
        }
    }

    private void initRefreshLayout() {
        mRefreshLayout.setDelegate(this);
        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(this, true);
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);
    }

    @Override
    public void onClick(View v) {
        recyclerView.scrollToPosition(0);
        switch (v.getId()) {
            case R.id.s01_tab_hot:
                currentType = TYPE_HOT;
                mRefreshLayout.beginRefreshing();
                s01TabHot.setBackgroundResource(R.drawable.square_pink_btn);
                s01TabHot.setTextColor(getResources().getColor(R.color.white));
                s01TabNew.setBackgroundResource(R.drawable.s01_tab_new_btn_border);
                s01TabNew.setTextColor(getResources().getColor(R.color.master_pink));
                s01TabFeature.setBackgroundResource(R.drawable.s01_tab_border2);
                s01TabFeature.setTextColor(getResources().getColor(R.color.master_pink));
                return;
            case R.id.s01_tab_new:
                clickTabNew();
                return;
            case R.id.s01_tab_feature:
                currentType = TYPE_FEATURED;
                mRefreshLayout.beginRefreshing();
                s01TabHot.setBackgroundResource(R.drawable.square_btn_border);
                s01TabHot.setTextColor(getResources().getColor(R.color.master_pink));
                s01TabNew.setBackgroundResource(R.drawable.s01_tab_new_btn_border);
                s01TabNew.setTextColor(getResources().getColor(R.color.master_pink));
                s01TabFeature.setBackgroundResource(R.drawable.s01_tab_btn1);
                s01TabFeature.setTextColor(getResources().getColor(R.color.white));
                return;
            case R.id.s01_search_btn:
                Intent intent = new Intent(S01MatchShowsActivity.this, S21CategoryActivity.class);
                intent.putExtra(S21CategoryActivity.TYPE, S21CategoryActivity.TYPE_SEARCH);
                intent.putStringArrayListExtra(S20MatcherActivity.S20_SELECT_CATEGORYREFS, new ArrayList<String>());
                startActivity(intent);
                return;
        }
    }

    private void clickTabNew() {
        currentType = TYPE_NEW;
        s01TabHot.setBackgroundResource(R.drawable.square_btn_border);
        s01TabHot.setTextColor(getResources().getColor(R.color.master_pink));
        s01TabNew.setBackgroundResource(R.drawable.s01_tab_btn2);
        s01TabNew.setTextColor(getResources().getColor(R.color.white));
        s01TabFeature.setBackgroundResource(R.drawable.s01_tab_border2);
        s01TabFeature.setTextColor(getResources().getColor(R.color.master_pink));
        head.setVisibility(View.GONE);
    }

    private void getMatchNew(final GregorianCalendar calendar) {
        QSRxApi.queryFeedingaggregationMatchNew(TimeUtil.formatTime(calendar))
                .subscribe(new QSSubscriber<List<FeedingAggregation>>() {
                    @Override
                    public void onNetError(int message) {
                        ErrorHandler.handle(S01MatchShowsActivity.this, message);
                    }

                    @Override
                    public void onNext(List<FeedingAggregation> feedingAggregations) {
                        matchNewAdapter.setCalendar(calendar);
                        matchNewAdapter.addDataAtTop(feedingAggregations);
                        layoutManager = new LinearLayoutManager(S01MatchShowsActivity.this);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(matchNewAdapter);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        mRefreshLayout.endRefreshing();
                        matchNewAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout bgaRefreshLayout) {
        if (currentType == TYPE_NEW)
            getMatchNew(new GregorianCalendar(2015,10,26));
        else doRefresh(currentType);
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout bgaRefreshLayout) {
        doLoadMore(currentType);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if (UnreadHelper.hasUnread()) {
            s01MenuBtn.setImageResource(R.drawable.nav_btn_menu_n_dot);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
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

package com.focosee.qingshow.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S01MatchNewAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.QSRxApi;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.QSSubscriber;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.vo.aggregation.FeedingAggregationLatest;
import com.focosee.qingshow.receiver.PushGuideEvent;
import com.focosee.qingshow.util.RecyclerViewUtil;
import com.focosee.qingshow.util.user.UnreadHelper;
import com.focosee.qingshow.widget.MenuView;
import com.squareup.timessquare.CalendarPickerView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import de.greenrobot.event.EventBus;

public class S01MatchShowsActivity extends BaseActivity implements BGARefreshLayout.BGARefreshLayoutDelegate {

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
    @InjectView(R.id.calendar)
    CalendarPickerView calendarPicker;

    @InjectView(R.id.s01_menu_btn)
    ImageView s01MenuBtn;
    @InjectView(R.id.home_time)
    ImageView timeBtn;

    @InjectView(R.id.guide)
    SimpleDraweeView global;

    private S01MatchNewAdapter matchNewAdapter;

    private MenuView menuView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s01_match_shows);
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);
        initRefreshLayout();
        initCalendar();
        calendarPicker.setVisibility(View.INVISIBLE);
        timeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarPicker.scrollToDate(new Date());
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
        recyclerView.setHasFixedSize(true);
        matchNewAdapter = new S01MatchNewAdapter(new LinkedList<FeedingAggregationLatest>(), this, R.layout.item_matchnew);
        recyclerView.setAdapter(matchNewAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(matchNewAdapter);

        RecyclerViewUtil.setBackTop(recyclerView, s01BackTopBtn, layoutManager);
        mRefreshLayout.beginRefreshing();
        
        getConfig();

        global.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                global.setVisibility(View.GONE);
            }
        });

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calendarPicker.isShown()) calendarPicker.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void initCalendar() {
        calendarPicker.init(new GregorianCalendar(2015, 1, 1).getTime(), new Date());
        calendarPicker.scrollToDate(new Date());
        calendarPicker.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                GregorianCalendar from = new GregorianCalendar();
                from.setTime(date);
                GregorianCalendar to = new GregorianCalendar();
                to.setTimeInMillis(date.getTime() + 24 * 3600 * 1000);
                jump(from, to);
            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });
    }

    private void jump(GregorianCalendar from, GregorianCalendar to){
        Intent intent = new Intent(S01MatchShowsActivity.this, S24ShowsDateActivity.class);
        intent.putExtra("MATCH_NEW_FROM", from);
        intent.putExtra("MATCH_NEW_TO", to);
        this.startActivity(intent);
    }

    @Override
    public void reconn() {
       mRefreshLayout.beginRefreshing();
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

    private void initRefreshLayout() {
        mRefreshLayout.setDelegate(this);
        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(this, true);
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);
        //mRefreshLayout.setIsShowLoadingMoreView(false);
    }

    private void getLatest() {
        QSRxApi.queryFeedingaggregationLatest()
                .subscribe(new QSSubscriber<List<FeedingAggregationLatest>>() {
                    @Override
                    public void onNetError(int message) {
                        ErrorHandler.handle(S01MatchShowsActivity.this, message);
                    }

                    @Override
                    public void onNext(List<FeedingAggregationLatest> feedingAggregations) {
                        matchNewAdapter.addDataAtTop(feedingAggregations);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        mRefreshLayout.endRefreshing();
                        matchNewAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void getConfig(){
        QSJsonObjectRequest request = new QSJsonObjectRequest(Request.Method.GET, QSAppWebAPI.getConfig(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getJSONObject("data").getJSONObject("guide").has("global")){
                                final String url = response.getJSONObject("data").getJSONObject("guide").get("global").toString();
                                final SharedPreferences preferences = QSApplication.instance().getPreferences();

                                if (!preferences.getBoolean(url, false)){
                                    global.setImageURI(Uri.parse(url));
                                    global.setVisibility(View.VISIBLE);
                                    SharedPreferences.Editor edit = preferences.edit();
                                    edit.putBoolean(url, true);
                                    edit.apply();
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        RequestQueueManager.INSTANCE.getQueue().add(request);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout bgaRefreshLayout) {
        getLatest();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout bgaRefreshLayout) {
        return false;
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

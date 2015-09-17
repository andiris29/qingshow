package com.focosee.qingshow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Response;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.fragment.S11NewTradeNotifyFragment;
import com.focosee.qingshow.adapter.S01ItemAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.ShowParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.RecyclerViewUtil;
import com.focosee.qingshow.util.ValueUtil;
import com.focosee.qingshow.widget.MenuView;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONObject;
import java.util.LinkedList;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import de.greenrobot.event.EventBus;

public class S01MatchShowsActivity extends BaseActivity implements BGARefreshLayout.BGARefreshLayoutDelegate, View.OnClickListener{

    public static final String S1_INPUT_SHOWABLE = "INPUT_SHOWABLE";
    public static final String S1_INPUT_TRADEID_NOTIFICATION = "S1_INPUT_TRADEID_NOTIFICATION";

    @InjectView(R.id.s01_backTop_btn)
    ImageButton s01BackTopBtn;
    @InjectView(R.id.s01_refresh)
    BGARefreshLayout mRefreshLayout;
    @InjectView(R.id.s01_recyclerview)
    RecyclerView recyclerView;
    @InjectView(R.id.container)
    FrameLayout container;

    private int TYPE_HOT = 0;
    private int TYPE_NEW = 1;
    private final int PAGESIZE = 30;

    @InjectView(R.id.s01_menu_btn)
    ImageView s01MenuBtn;
    @InjectView(R.id.s01_tab_hot)
    Button s01TabHot;
    @InjectView(R.id.s01_tab_new)
    Button s01TabNew;

    private S01ItemAdapter adapter;
    private int currentPageNo = 1;
    private int currentType = TYPE_HOT;

    private MenuView menuView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s01_match_shows);
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);
        initRefreshLayout();
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
        adapter = new S01ItemAdapter(new LinkedList<MongoShow>(), this, R.layout.item_s01_matchlist);
        recyclerView.setAdapter(adapter);

        RecyclerViewUtil.setBackTop(recyclerView, s01BackTopBtn, layoutManager);
        mRefreshLayout.beginRefreshing();
        showNewTradeNotify(getIntent());
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
        Log.d("url:", url);
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(S01MatchShowsActivity.class.getSimpleName(), "response:" + response);
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(S01MatchShowsActivity.this, MetadataParser.getError(response));
                    mRefreshLayout.endLoadingMore();
                    mRefreshLayout.endRefreshing();
                    return;
                }

                List<MongoShow> datas = ShowParser.parseQuery_itemString(response);
                if (pageNo == 1) {
                    mRefreshLayout.endRefreshing();
                    adapter.addDataAtTop(datas);
                    currentPageNo = pageNo;
                } else {
                    mRefreshLayout.endLoadingMore();
                    adapter.addData(datas);
                }

                adapter.notifyDataSetChanged();
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

    public void onEventMainThread(){

    }

    private void initRefreshLayout() {
        mRefreshLayout.setDelegate(this);
        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(this, true);
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);
    }

    @Override
    public void onClick(View v) {
        recyclerView.scrollToPosition(0);
        if (v.getId() == R.id.s01_tab_hot) {
            currentType = TYPE_HOT;
            mRefreshLayout.beginRefreshing();
            s01TabHot.setBackgroundResource(R.drawable.s01_tab_btn1);
            s01TabHot.setTextColor(getResources().getColor(R.color.white));
            s01TabNew.setBackgroundResource(R.drawable.s01_tab_border1);
            s01TabNew.setTextColor(getResources().getColor(R.color.master_pink));
            return;
        }
        if (v.getId() == R.id.s01_tab_new) {
            currentType = TYPE_NEW;
            mRefreshLayout.beginRefreshing();
            s01TabHot.setBackgroundResource(R.drawable.s01_tab_border2);
            s01TabHot.setTextColor(getResources().getColor(R.color.master_pink));
            s01TabNew.setBackgroundResource(R.drawable.s01_tab_btn2);
            s01TabNew.setTextColor(getResources().getColor(R.color.white));
            return;
        }
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout bgaRefreshLayout) {
        doRefresh(currentType);
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout bgaRefreshLayout) {
        doLoadMore(currentType);
        return true;
    }


    private void showNewTradeNotify(Intent intent) {
        boolean showable = intent.getBooleanExtra(S1_INPUT_SHOWABLE, false);
        if (showable) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, new S11NewTradeNotifyFragment(), "newTradeNotify" + System.currentTimeMillis());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if(!TextUtils.isEmpty(QSApplication.instance().getPreferences().getString(ValueUtil.NEED_GUIDE, ""))){
            s01MenuBtn.setImageResource(R.drawable.nav_btn_menu_n_dot);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}

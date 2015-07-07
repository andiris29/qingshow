package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.Response;
import com.focosee.qingshow.Listener.EndlessRecyclerOnScrollListener;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S01ItemAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.ShowParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.TimeUtil;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.focosee.qingshow.widget.RecyclerPullToRefreshView;

import org.json.JSONObject;

import java.util.LinkedList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class S01MatchShowsActivity extends MenuActivity {

    private int TYPE_HOT = 0;
    private int TYPE_NEW = 1;

    @InjectView(R.id.s01_menu_btn)
    ImageView s01MenuBtn;
    @InjectView(R.id.s01_tab_hot)
    Button s01TabHot;
    @InjectView(R.id.s01_title_layout)
    RelativeLayout s01TitleLayout;
    @InjectView(R.id.navigation_btn_match)
    ImageButton navigationBtnMatch;
    @InjectView(R.id.navigation_btn_good_match)
    ImageButton navigationBtnGoodMatch;
    @InjectView(R.id.u01_people)
    ImageButton u01Collection;
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
        recyclerPullToRefreshView.doPullRefreshing(true, 200);
    }

    public void doRefresh(int type) {
        getDatasFromNet(type, 1, 10);
    }

    public void doLoadMore(int type) {
        getDatasFromNet(type, currentPageNo, 10);
    }

    public void getDatasFromNet(int type, final int pageNo, int pageSize) {

        String url = type == TYPE_HOT ? QSAppWebAPI.getMatchHotApi(pageNo, pageSize) : QSAppWebAPI.getMatchNewApi(pageNo, pageSize);

        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(S01MatchShowsActivity.this, MetadataParser.getError(response));
                    recyclerPullToRefreshView.onPullDownRefreshComplete();
                    recyclerPullToRefreshView.onPullUpRefreshComplete();
                    return;
                }

                if (pageNo == 1) {
                    adapter.addDataAtTop(ShowParser.parseQuery(response));
                    currentPageNo = pageNo;
                } else {
                    adapter.addData(ShowParser.parseQuery(response));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_s01_match_shows, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.s01_tab_hot) {
            currentType = TYPE_HOT;
            recyclerPullToRefreshView.doPullRefreshing(true, 500);
            s01TabHot.setBackground(getResources().getDrawable(R.drawable.s01_tab_btn1));
            s01TabHot.setTextColor(getResources().getColor(R.color.white));
            s01TabNew.setBackground(getResources().getDrawable(R.drawable.s01_tab_border1));
            s01TabNew.setTextColor(getResources().getColor(R.color.s21_pink));
            return;
        }
        if (v.getId() == R.id.s01_tab_new) {
            currentType = TYPE_NEW;
            recyclerPullToRefreshView.doPullRefreshing(true, 500);
            s01TabHot.setBackground(getResources().getDrawable(R.drawable.s01_tab_border2));
            s01TabHot.setTextColor(getResources().getColor(R.color.s21_pink));
            s01TabNew.setBackground(getResources().getDrawable(R.drawable.s01_tab_btn2));
            s01TabNew.setTextColor(getResources().getColor(R.color.white));
            return;
        }
        super.onClick(v);
    }
}

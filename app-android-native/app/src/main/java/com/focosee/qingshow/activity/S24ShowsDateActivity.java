package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S01ItemAdapter;
import com.focosee.qingshow.httpapi.QSRxApi;
import com.focosee.qingshow.httpapi.request.QSSubscriber;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.TimeUtil;
import com.focosee.qingshow.util.adapter.AbsAdapter;

import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

public class S24ShowsDateActivity extends BaseActivity implements BGARefreshLayout.BGARefreshLayoutDelegate ,View.OnClickListener{


    @InjectView(R.id.s24_rv)
    RecyclerView dataRv;
    @InjectView(R.id.s24_time)
    TextView timeTv;
    @InjectView(R.id.s24_refresh)
    BGARefreshLayout refresh;

    @InjectView(R.id.iv_new_or_hot)
    private ImageView ivNewOrHot;

    public static String MATCH_NEW_FROM = "MATCH_NEW_FROM";
    public static String MATCH_NEW_TO = "MATCH_NEW_TO";
    private AbsAdapter<MongoShow> adapter;

    private int mPageNo = 1;
    private int pageSize = 30;
    private GregorianCalendar from;
    private GregorianCalendar to;
    private boolean chouldLoadMore;
    private boolean isHot = true;

    @Override
    public void reconn() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s24_shows_date);
        ButterKnife.inject(this);
        ivNewOrHot.setOnClickListener(this);
        from = (GregorianCalendar) getIntent().getSerializableExtra(MATCH_NEW_FROM);
        to = (GregorianCalendar) getIntent().getSerializableExtra(MATCH_NEW_TO);
        String title = getIntent().getExtras().getString("title");
        String time = TimeUtil.formatDateTime_CN_Pre(from);
        timeTv.setText(title);
        adapter = new S01ItemAdapter(new LinkedList<MongoShow>(), this, R.layout.item_match);
        dataRv.setAdapter(adapter);
        dataRv.setLayoutManager(new GridLayoutManager(this, 2));
        bindDataFromNet(1, pageSize, from, to);
        findViewById(R.id.backImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                S24ShowsDateActivity.this.finish();
            }
        });
        dataRv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        initRefreshLayout();
    }

    private void initRefreshLayout() {
        refresh.setDelegate(this);
        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(this, true);
        refresh.setRefreshViewHolder(refreshViewHolder);
        //mRefreshLayout.setIsShowLoadingMoreView(false);
    }

    /**
     * 时间网络数据
     * @param pageNo
     * @param pageSize
     * @param from
     * @param to
     */
    private void bindDataFromNet(int pageNo, final int pageSize, GregorianCalendar from, GregorianCalendar to) {
        QSRxApi.feedingTime(pageNo, pageSize, TimeUtil.formatTime(from), TimeUtil.formatTime(to))
                .subscribe(new QSSubscriber<List<MongoShow>>() {
                    @Override
                    public void onNetError(int message) {
                        ErrorHandler.handle(S24ShowsDateActivity.this, message);
                    }

                    @Override
                    public void onNext(List<MongoShow> mongoShows) {
                        if (mongoShows.size() < pageSize) {
                            chouldLoadMore = false;
                        } else {
                            chouldLoadMore = true;
                        }
                        mPageNo = 1;
                        adapter.clearData();
                        adapter.addDataAtTop(mongoShows);
                        adapter.notifyDataSetChanged();
                        refresh.endRefreshing();
                    }
                });
    }

    /**
     * 热度网络数据
     * @param pageNo
     * @param pageSize
     * @param from
     * @param to
     */
    private void bindDataFromNetHot(int pageNo, final int pageSize, GregorianCalendar from, GregorianCalendar to) {
        QSRxApi.feedingHot(pageNo, pageSize, TimeUtil.formatTime(from), TimeUtil.formatTime(to))
                .subscribe(new QSSubscriber<List<MongoShow>>() {
                    @Override
                    public void onNetError(int message) {
                        ErrorHandler.handle(S24ShowsDateActivity.this, message);
                    }

                    @Override
                    public void onNext(List<MongoShow> mongoShows) {
                        if (mongoShows.size() < pageSize){
                            chouldLoadMore = false;
                        }else {
                            chouldLoadMore = true;
                        }
                        mPageNo = 1;
                        adapter.clearData();
                        adapter.addDataAtTop(mongoShows);
                        adapter.notifyDataSetChanged();
                        refresh.endRefreshing();
                    }
                });
    }

    private void loadMore(final int pageNo, final int pageSize, GregorianCalendar from, GregorianCalendar to) {
        QSRxApi.feedingTime(pageNo, pageSize, TimeUtil.formatTime(from), TimeUtil.formatTime(to))
                .subscribe(new QSSubscriber<List<MongoShow>>() {
                    @Override
                    public void onNetError(int message) {
                        ErrorHandler.handle(S24ShowsDateActivity.this, message);
                    }

                    @Override
                    public void onNext(List<MongoShow> mongoShows) {
                        if (mongoShows.size() < pageSize){
                            chouldLoadMore = false;
                        }else {
                            chouldLoadMore = true;
                        }
                        mPageNo++;
                        adapter.addDataAtLast(mongoShows);
                        adapter.notifyDataSetChanged();
                        refresh.endLoadingMore();
                    }
                });
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        bindDataFromNet(mPageNo, pageSize, from, to);
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        if (chouldLoadMore){
            loadMore(mPageNo, pageSize, from, to);
        }
        return chouldLoadMore;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_new_or_hot:
                if (isHot){
                    isHot = false;
                    ivNewOrHot.setImageResource(R.drawable.match_pink);
                }else {
                    isHot = true;
                    ivNewOrHot.setImageResource(R.drawable.match);
                }
                break;
        }
    }
}

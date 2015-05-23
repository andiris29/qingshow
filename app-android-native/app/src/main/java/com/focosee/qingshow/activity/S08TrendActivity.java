package com.focosee.qingshow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S08TrendListAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.ChosenParser;
import com.focosee.qingshow.httpapi.response.dataparser.ShowParser;
import com.focosee.qingshow.model.vo.mongo.IMongoChosen;
import com.focosee.qingshow.model.vo.mongo.MongoChosen;
import com.focosee.qingshow.model.vo.mongo.MongoPreview;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.dataparser.PreviewParser;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.widget.MPullRefreshListView;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class S08TrendActivity extends BaseActivity {

    private final String TAG = "S08TrendActivity";
    private final String TYPE = "1";

    @InjectView(R.id.S08_content_list_view)
    MPullRefreshListView mPullRefreshListView;
    private ListView listView;

    @InjectView(R.id.right_btn)
    ImageView rightBtn;

    private S08TrendListAdapter adapter;
    private int _currentPageIndex = 1;
    @InjectView(R.id.left_btn)
    ImageView _backImageBtn;

    private SimpleDateFormat _mDateFormat = new SimpleDateFormat("MM-dd HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s08_trend);
        ButterKnife.inject(this);
        listView = mPullRefreshListView.getRefreshableView();

        ((TextView)findViewById(R.id.title)).setText(R.string.s08_title);

        _backImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(S08TrendActivity.this, U01UserActivity.class));
            }
        });

        adapter = new S08TrendListAdapter(this);
        listView.setAdapter(adapter);
        listView.setSmoothScrollbarEnabled(false);
        listView.setSmoothScrollbarEnabled(false);

        mPullRefreshListView.setPullRefreshEnabled(true);
        mPullRefreshListView.setPullLoadEnabled(true);
        mPullRefreshListView.setScrollLoadEnabled(true);
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                doRefreshTask();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                doGetMoreTask();
            }
        });
        mPullRefreshListView.doPullRefreshing(true, 500);
    }

    @Override
    public void reconn() {
        doRefreshTask();
    }

    private void setLastUpdateTime() {
        String text = formatDateTime(System.currentTimeMillis());
        mPullRefreshListView.setLastUpdatedLabel(text);
    }

    private String formatDateTime(long time) {
        if (0 == time) {
            return "";
        }

        return _mDateFormat.format(new Date(time));
    }

    private void doRefreshTask() {

        _getDataFromNet(true, 1, 10);
    }

    private void doGetMoreTask() {
        _getDataFromNet(false, _currentPageIndex + 1, 10);
    }

    private void _getDataFromNet(boolean refreshSign, int pageNo, int pageSize) {
        final boolean _tRefreshSign = refreshSign;
        QSJsonObjectRequest jor = new QSJsonObjectRequest(QSAppWebAPI.getChosenFeedApi(TYPE, pageNo, pageSize), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    Toast.makeText(getApplication(), "已经是最后一页了", Toast.LENGTH_SHORT).show();
                    mPullRefreshListView.onPullDownRefreshComplete();
                    mPullRefreshListView.onPullUpRefreshComplete();
                    mPullRefreshListView.setHasMoreData(false);
                    return;
                }
                LinkedList<IMongoChosen> results = new LinkedList<IMongoChosen>();
                results.addAll(ChosenParser.parse(response));
                if (_tRefreshSign) {
                    adapter.resetData(results);
                    _currentPageIndex = 1;
                } else {
                    adapter.addItemLast(results);
                    _currentPageIndex++;
                }
                adapter.notifyDataSetChanged();
                mPullRefreshListView.onPullDownRefreshComplete();
                mPullRefreshListView.onPullUpRefreshComplete();
                mPullRefreshListView.setHasMoreData(true);
                setLastUpdateTime();
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jor);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("S08Trend"); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("S08Trend"); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }

}

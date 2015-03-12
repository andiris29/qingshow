package com.focosee.qingshow.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.P01ModelListAdapter;
import com.focosee.qingshow.constants.code.PeopleTypeInU01PersonalActivity;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.PeopleParser;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.widget.MPullRefreshListView;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONObject;
import java.util.ArrayList;

public class P01ModelListActivity extends BaseActivity {

    private MPullRefreshListView pullRefreshListView;
    private ListView listView;
    private P01ModelListAdapter adapter;
    private int _currentIndex = 1;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(U01PersonalActivity.USER_UPDATE.equals(intent.getAction())){

                int position = intent.getIntExtra("position", 0);
                adapter.getItemData(position).setModelIsFollowedByCurrentUser(!adapter.getItemData(position).getModelIsFollowedByCurrentUser());
                adapter.getItemData(position).get__context().numFollowers = intent.getIntExtra("numFollowers", 0);
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p01_model_list);

        findViewById(R.id.P01_back_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                P01ModelListActivity.this.finish();
            }
        });

        pullRefreshListView = (MPullRefreshListView) findViewById(R.id.P01_model_list_view);

        pullRefreshListView.setPullLoadEnabled(true);
        pullRefreshListView.setPullRefreshEnabled(true);
        pullRefreshListView.setScrollLoadEnabled(true);

        listView = pullRefreshListView.getRefreshableView();

        adapter = new P01ModelListAdapter(this, new ArrayList<MongoPeople>(), ImageLoader.getInstance(), PeopleTypeInU01PersonalActivity.NOREMOVEITEM.getIndex());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(P01ModelListActivity.this, "test click", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(P01ModelListActivity.this, P02ModelActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(P02ModelActivity.INPUT_MODEL, ((MongoPeople) adapter.getItem(position)));
                intent.putExtras(bundle);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });

        pullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                doRefreshData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                doLoadMoreData();
            }
        });

        pullRefreshListView.doPullRefreshing(true, 0);

        registerReceiver(receiver, new IntentFilter(U01PersonalActivity.USER_UPDATE));
    }

    @Override
    public void reconn() {

    }

    private void doRefreshData() {
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getModelListApi("1", "10"), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ArrayList<MongoPeople> moreData = PeopleParser.parseQueryModels(response);
                adapter.resetData(moreData);
                adapter.notifyDataSetChanged();

                _currentIndex = 1;

                pullRefreshListView.onPullDownRefreshComplete();
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void doLoadMoreData() {
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getModelListApi(String.valueOf(_currentIndex + 1), "10"), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if (MetadataParser.hasError(response)) {
                    pullRefreshListView.onPullUpRefreshComplete();
                    Toast.makeText(P01ModelListActivity.this, "没有更多了！", Toast.LENGTH_SHORT).show();
                    return;
                }
                ArrayList<MongoPeople> moreData = PeopleParser.parseQueryModels(response);
                adapter.addData(moreData);
                adapter.notifyDataSetChanged();
                pullRefreshListView.onPullUpRefreshComplete();
                _currentIndex++;
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void handleErrorMsg(VolleyError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("P01ModelList"); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("P01ModelList"); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}

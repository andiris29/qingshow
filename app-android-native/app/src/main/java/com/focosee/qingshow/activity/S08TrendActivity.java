package com.focosee.qingshow.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S08TrendListAdapter;
import com.focosee.qingshow.entity.TrendEntity;
import com.focosee.qingshow.widget.MPullRefreshListView;

import java.util.ArrayList;

public class S08TrendActivity extends Activity {


    private MPullRefreshListView mPullRefreshListView;
    private ListView listView;

    private S08TrendListAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s08_trend);

        mPullRefreshListView = (MPullRefreshListView) findViewById(R.id.S08_content_list_view);
        listView = mPullRefreshListView.getRefreshableView();

        adapter = new S08TrendListAdapter(this, new ArrayList<TrendEntity>());
        listView.setAdapter(adapter);
    }

}

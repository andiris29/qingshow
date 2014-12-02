package com.focosee.qingshow.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.widget.MNavigationView;
import com.focosee.qingshow.widget.MPullRefreshListView;

public class P01ModelListActivity extends Activity {

    private MNavigationView navigationView;
    private MPullRefreshListView pullRefreshListView;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p01_model_list);

        navigationView = (MNavigationView) findViewById(R.id.P01_model_list_navigation);
        pullRefreshListView = (MPullRefreshListView) findViewById(R.id.P01_model_list_view);

        listView = pullRefreshListView.getRefreshableView();
    }
}

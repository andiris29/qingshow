package com.focosee.qingshow.activity;

import android.app.Activity;
import android.os.Bundle;

import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.P03BrandListAdapter;
import com.focosee.qingshow.entity.BrandEntity;
import com.focosee.qingshow.widget.MNavigationView;
import com.focosee.qingshow.widget.MPullRefreshMultiColumnListView;
import com.huewu.pla.lib.MultiColumnListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class P03BrandListActivity extends Activity {

    private MNavigationView navigationView;
    private MPullRefreshMultiColumnListView pullRefreshListView;
    private MultiColumnListView listView;

    private P03BrandListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p03_brand_list);

        navigationView = (MNavigationView) findViewById(R.id.P03_brand_list_navigation);
        pullRefreshListView = (MPullRefreshMultiColumnListView) findViewById(R.id.P03_brand_list_list_view);
        listView = pullRefreshListView.getRefreshableView();
        adapter = new P03BrandListAdapter(this, new ArrayList<BrandEntity>(), ImageLoader.getInstance());

        listView.setAdapter(adapter);
    }
}

package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.P04BrandItemListAdapter;
import com.focosee.qingshow.adapter.P04BrandViewPagerAdapter;
import com.focosee.qingshow.entity.BrandItemEntity;
import com.focosee.qingshow.widget.MPullRefreshListView;
import com.focosee.qingshow.widget.PullToRefreshBase;

import java.util.ArrayList;

public class P04BrandActivity extends Activity {

    private ViewPager viewPager;
    private MPullRefreshListView latestPullRefreshListView;
    private ListView latestListView;

    private P04BrandViewPagerAdapter viewPagerAdapter;
    private P04BrandItemListAdapter itemListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p04_brand);

        viewPager = (ViewPager) findViewById(R.id.P04_personalViewPager);

        ArrayList<View> pagerViewList = new ArrayList<View>();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        pagerViewList.add(inflater.inflate(R.layout.pager_p04_brand_item, null));
        viewPagerAdapter = new P04BrandViewPagerAdapter(pagerViewList);

        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        latestPullRefreshListView = (MPullRefreshListView) pagerViewList.get(0).findViewById(R.id.pager_P04_item_list);
        latestListView = latestPullRefreshListView.getRefreshableView();

        ArrayList<BrandItemEntity> itemEntities = new ArrayList<BrandItemEntity>();
        itemEntities = __createFakeData();
        itemListAdapter = new P04BrandItemListAdapter(this, itemEntities);

        latestListView.setAdapter(itemListAdapter);
        latestPullRefreshListView.setScrollLoadEnabled(true);
        latestPullRefreshListView.setPullRefreshEnabled(true);
        latestPullRefreshListView.setPullLoadEnabled(true);
        latestPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                itemListAdapter.resetData(__createFakeData());
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                itemListAdapter.addData(__createFakeData());
            }
        });

    }


    private ArrayList<BrandItemEntity> __createFakeData() {
        ArrayList<BrandItemEntity> tempData = new ArrayList<BrandItemEntity>();
        for (int i = 0; i < 30; i++) {
            BrandItemEntity brandItemEntity = new BrandItemEntity();
            brandItemEntity._id = "test" + String.valueOf(i);
            brandItemEntity.discount = String.valueOf(1.0 / (i + 1.0));
            brandItemEntity.image = "http://img1.imgtn.bdimg.com/it/u=3411049717,3668206888&fm=21&gp=0.jpg";
            tempData.add(brandItemEntity);
        }
        return tempData;
    }
}

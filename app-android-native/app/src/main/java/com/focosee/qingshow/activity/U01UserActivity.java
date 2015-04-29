package com.focosee.qingshow.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.fragment.U01FavoriteFragment;
import com.focosee.qingshow.activity.fragment.U01RecommendFragment;
import com.focosee.qingshow.activity.fragment.U01ScrollEvent;
import com.focosee.qingshow.adapter.U01UserAdapter;
import com.focosee.qingshow.widget.MViewPager_NoScroll;
import com.focosee.qingshow.widget.TabView;


import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;


public class U01UserActivity extends BaseActivity{

    @InjectView(R.id.user_head)
    SimpleDraweeView userHead;
    @InjectView(R.id.viewpager)
    MViewPager_NoScroll viewPager;
    @InjectView(R.id.u01_header)
    RelativeLayout header;
    @InjectView(R.id.recommend)
    TabView recommend;
    @InjectView(R.id.favorite)
    TabView favorite;

    private U01RecommendFragment recommendFragment;
    private U01FavoriteFragment favoriteFragment;

    public void reconn() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u01_user);
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);

        init();

        String str = "http://chingshow.com/folder-uploads/upload_0bce12cd331224d3e7aceb29a8517743.jpg";
        userHead.setImageURI(Uri.parse(str));
    }


    public void onEventMainThread(U01ScrollEvent event){
        final float offset = event.offset;
        Log.d("offset","" + offset);
        header.setY(-event.offset);
    }

    private void init() {

        U01UserAdapter pagerAdapter =  new U01UserAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setScrollble(false);
        recommend.setCheck(true);
        viewPager.setCurrentItem(0);

        recommendFragment = (U01RecommendFragment) pagerAdapter.getItem(0);
        favoriteFragment = (U01FavoriteFragment) pagerAdapter.getItem(1);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onTabClick(View v) {
        TabView view = (TabView) v;
        switch (view.getId()){
            case R.id.favorite:
               if(!view.isCheck()){
                   view.setCheck(true);
                   viewPager.setCurrentItem(1);
                   header.setY(0);
                   recommend.setCheck(false);
               }
                break;
            case R.id.recommend:
                if(!view.isCheck()){
                    view.setCheck(true);
                    viewPager.setCurrentItem(0);
                    header.setY(0);
                    favorite.setCheck(false);
                }
                break;
        }
    }
}

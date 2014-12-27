package com.focosee.qingshow.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class P04BrandViewPagerAdapter extends PagerAdapter{

    private ArrayList<View> pagerViewList;

    public P04BrandViewPagerAdapter(ArrayList<View> viewList) {
        pagerViewList = viewList;
    }

    @Override
    public int getCount() {
        return (null != pagerViewList) ? pagerViewList.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(pagerViewList.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(pagerViewList.get(position));
        return pagerViewList.get(position);
    }
}

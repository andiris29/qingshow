package com.focosee.qingshow.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2015/6/18.
 */
public class S21CategoryViewPagerAdapter extends PagerAdapter{
    private List<View> mViews;

    public S21CategoryViewPagerAdapter(List<View> mViews) {
        this.mViews = mViews;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)     {
        container.removeView(mViews.get(position));
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViews.get(position));
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return  mViews.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0==arg1;
    }
}

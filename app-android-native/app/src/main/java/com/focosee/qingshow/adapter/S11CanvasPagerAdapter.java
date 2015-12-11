package com.focosee.qingshow.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.focosee.qingshow.widget.QSCanvasView;

import java.util.List;

/**
 * Created by Administrator on 2015/12/7.
 */
public class S11CanvasPagerAdapter extends PagerAdapter {

    private List<FrameLayout> canvasList;

    public S11CanvasPagerAdapter(List<FrameLayout> canvasList) {
        this.canvasList = canvasList;
    }

    @Override
    public int getCount() {
        return canvasList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(canvasList.get(position), 0);
        return canvasList.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        int index = canvasList.indexOf(object);
        if (index == -1)
            return POSITION_NONE;
        else
            return index;
    }
}

package com.allthelucky.common.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2015/3/4.
 */
public class CtrlScrollViewPager extends ViewPager {

    private boolean scrollEnabled = true;

    public CtrlScrollViewPager(Context context) {
        super(context);
    }

    public CtrlScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollEnabled(boolean scrollEnabled) {
        this.scrollEnabled = scrollEnabled;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(scrollEnabled){
            return super.onInterceptTouchEvent(ev);
        }else{
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (scrollEnabled) {
        return super.onTouchEvent(ev);
        }else{
            return false;
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        if(scrollEnabled){
            super.scrollTo(x, y);
        }
    }
}

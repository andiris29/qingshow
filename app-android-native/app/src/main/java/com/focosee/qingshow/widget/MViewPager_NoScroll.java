package com.focosee.qingshow.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2015/3/4.
 */
public class MViewPager_NoScroll extends ViewPager {

    private boolean scrollble=true;

    public MViewPager_NoScroll(Context context) {
        super(context);
    }

    public MViewPager_NoScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!scrollble) {
            return false;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(!scrollble) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public boolean isScrollble() {
        return scrollble;
    }

    public void setScrollble(boolean scrollble) {
        this.scrollble = scrollble;
    }
}

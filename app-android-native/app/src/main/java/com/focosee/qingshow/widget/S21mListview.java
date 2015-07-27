package com.focosee.qingshow.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by Administrator on 2015/6/19.
 */
public class S21mListview extends ListView {

    private float xDistance, yDistance, xLast, yLast;

    public S21mListview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public S21mListview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public S21mListview(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();

                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                xLast = curX;
                yLast = curY;

                if (xDistance > yDistance) {
                    return false;
                }
        }

        return super.onInterceptTouchEvent(ev);
    }
}

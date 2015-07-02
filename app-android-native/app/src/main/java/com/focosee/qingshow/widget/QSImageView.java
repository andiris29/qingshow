package com.focosee.qingshow.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by Administrator on 2015/7/2.
 */
public class QSImageView extends ImageView {
    float lastX;
    float lastY;
    private int containerWidth;
    private int containerHeight;

    public QSImageView(Context context) {
        super(context);
    }

    public QSImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QSImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float distanceX = lastX - event.getRawX();
                    float distanceY = lastY - event.getRawY();

                    float nextY = getY() - distanceY;
                    float nextX = getX() - distanceX;

                    // 不能移出屏幕
                    if (nextY < 0) {
                        nextY = 0;
                    } else if (nextY > containerHeight - getHeight()) {
                        nextY = containerHeight - getHeight();
                    }
                    if (nextX < 0)
                        nextX = 0;
                    else if (nextX > containerWidth - getWidth())
                        nextX = containerWidth - getWidth();

                    // 属性动画移动
                    ObjectAnimator y = ObjectAnimator.ofFloat(this, "y", getY(), nextY);
                    ObjectAnimator x = ObjectAnimator.ofFloat(this, "x", getX(), nextX);

                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(x, y);
                    animatorSet.setDuration(0);
                    animatorSet.start();

                    lastX = event.getRawX();
                    lastY = event.getRawY();
            }
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            containerHeight = ((QSCanvasView)getParent()).getHeight();
            containerWidth = ((QSCanvasView)getParent()).getWidth();
        }
    }
}

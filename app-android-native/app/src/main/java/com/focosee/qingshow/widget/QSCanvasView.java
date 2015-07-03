package com.focosee.qingshow.widget;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.focosee.qingshow.widget.QSImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/7/2.
 */
public class QSCanvasView extends FrameLayout implements ScaleGestureDetector.OnScaleGestureListener {

    private List<QSImageView> views;
    private int lastCheckedIndex;
    private int checkedIndex;

    private QSImageView checkedView;

    private OnCheckedChangeListener onCheckedChangeListener;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;

    public interface OnCheckedChangeListener {
        public void checkedChanged(QSImageView view);
    }

    public QSCanvasView(Context context) {
        this(context, null);
    }

    public QSCanvasView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QSCanvasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        views = new ArrayList<>();
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(getContext(), this);
    }

    public void attach(QSImageView view) {
        views.add(view);
        this.addView(view);
    }

    private void detach(QSImageView view) {
        views.remove(view);
        this.removeView(view);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 分发前执行checked方法
     * @return false
     *
     * */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        notifyCheckedChange();
        return gestureDetector.onTouchEvent(ev);
    }

    /**
     * 监听缩放手势，同时消费touch事件
     * @return true
     *
     * */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return scaleGestureDetector.onTouchEvent(ev);
    }

    public void notifyCheckedChange() {
        boolean check[] = new boolean[views.size()];
        int checkedCount = 0;
        for (int i = 0; i < views.size(); i++) {
            QSImageView item = views.get(i);
            if (item.isChecked()) {
                ++checkedCount;
                check[i] = true;
            } else {
                check[i] = false;
            }
        }

        if (1 == checkedCount) {

            for (int i = 0; i < check.length; i++) {
                if (check[i]) {
                    lastCheckedIndex = i;
                    if (null != onCheckedChangeListener) {
                        onCheckedChangeListener.checkedChanged(views.get(i));
                        checkedView = views.get(i);
                    }
                }
            }
        }

        if (2 == checkedCount) {
            for (int i = 0; i < check.length; i++) {
                QSImageView item = views.get(i);
                if (i == lastCheckedIndex && check[i]) {
                    item.setChecked(false);
                } else if (i != lastCheckedIndex && check[i]) {
                    checkedIndex = i;
                    if (null != onCheckedChangeListener) {
                        onCheckedChangeListener.checkedChanged(views.get(i));
                        checkedView = views.get(i);
                    }
                }
            }
            lastCheckedIndex = checkedIndex;
        }
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }



}

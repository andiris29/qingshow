package com.qs.myapplication;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/7/2.
 */
public class QSCanvasView extends FrameLayout {

    private List<QSImageView> views;
    private int lastCheckedIndex;
    private int checkedIndex;

    private OnCheckedChangeListener onCheckedChangeListener;

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
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    public void notifyCheckedChange() {
        boolean check[] = new boolean[getChildCount()];
        int checkedCount = 0;
        Log.i("tag", checkedCount + "count");

        for (int i = 0; i < getChildCount(); i++) {
            QSImageView item = (QSImageView) getChildAt(i);
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
//                        onCheckedChangeListener.checkedChanged(i);
                    }
                }
            }
        }

        if (2 == checkedCount) {
            for (int i = 0; i < check.length; i++) {
                QSImageView item = (QSImageView) getChildAt(i);
                if (i == lastCheckedIndex && check[i]) {
                    item.setChecked(false);
                } else if (i != lastCheckedIndex && check[i]) {
                    checkedIndex = i;
                    if (null != onCheckedChangeListener) {
//                        onCheckedChangeListener.checkedChanged(i);
                    }
                }
            }
            lastCheckedIndex = checkedIndex;
        }
    }
}

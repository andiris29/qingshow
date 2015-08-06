package com.focosee.qingshow.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroupOverlay;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.focosee.qingshow.util.RectUtil;
import com.focosee.qingshow.widget.QSImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/7/2.
 */
public class QSCanvasView extends FrameLayout {

    public List<QSImageView> views;
    private int lastCheckedIndex = 0;
    private int checkedIndex = 0;

    private QSImageView checkedView;

    private OnCheckedChangeListener onCheckedChangeListener;
    private GestureDetector gestureDetector;

    public interface OnCheckedChangeListener {
        void checkedChanged(QSImageView view);
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
    }

    public void attach(QSImageView view) {
        views.add(view);
        this.addView(view);
    }

    public void detach(QSImageView view) {
        views.remove(view);
        this.removeView(view);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 分发前执行checked方法
     *
     * @return false
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        notifyCheckedChange();
        return false;
    }

    /**
     * 监听缩放手势，同时消费touch事件
     *
     * @return true
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return true;
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
                        notifyChildrenMoveable(views.get(i));
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
                        notifyChildrenMoveable(views.get(i));
                        checkedView = views.get(i);
                    }
                }
            }
            lastCheckedIndex = checkedIndex;
        }

        if (checkedIndex == 0 && views.size() > 0) {
            views.get(0).setChecked(true);
            onCheckedChangeListener.checkedChanged(views.get(0));
        }

    }

    public void notifyChildrenMoveable(QSImageView view) {
        for (QSImageView imageView : views) {
            if (view == imageView) {
                imageView.setMoveable(true);
            } else {
                imageView.setMoveable(false);
            }
        }
    }

    public void notifyChildrenUnClick() {
        for (QSImageView view : views) {
            view.setChecked(false);
        }
    }

    public void reselectView() {
        if (checkedIndex < views.size()){
            QSImageView view = views.get(checkedIndex);
            view.setChecked(true);
            view.goneDelBtn();
        }
    }

    public float calcUnOverlapArea(View view) {
        float area = 0f;
        int index = indexOfChild(view);
        Rect targetRect = new Rect();
        view.getGlobalVisibleRect(targetRect);
        List<Rect> rects = null;

        if (index == getChildCount() - 1) {
            return RectUtil.getRectArea(targetRect);
        }

        for (int i = index + 1; i < getChildCount(); i++) {
            Rect rect = new Rect();
            getChildAt(i).getGlobalVisibleRect(rect);
            if (RectUtil.clipRect(targetRect, rect) == null) {
                return 0f;
            }

            if (rects == null) {
                rects = RectUtil.clipRect(targetRect, rect);
                continue;
            }

            List<Rect> newRects = new ArrayList<>();
            for (Rect rectChlid : rects) {
                if (RectUtil.clipRect(rectChlid, rect) != null) {
                    newRects.addAll(RectUtil.clipRect(rectChlid, rect));
                }
            }
            rects = newRects;
        }

        for (Rect rect : rects) {
            area += RectUtil.getRectArea(rect);
        }
        return area;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }


}

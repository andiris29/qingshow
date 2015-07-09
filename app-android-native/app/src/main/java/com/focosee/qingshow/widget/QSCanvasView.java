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
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.focosee.qingshow.widget.QSImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/7/2.
 */
public class QSCanvasView extends FrameLayout {

    public List<QSImageView> views;
    private int lastCheckedIndex;
    private int checkedIndex;

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
        return gestureDetector.onTouchEvent(ev);
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

    public boolean checkOverlap(float percent){
        boolean result = false;
        List<float[]> areas = new ArrayList<>();

        for (QSImageView view : views) {
            areas.add(getOverlapArea(view));
        }

        for (int i = 0; i < areas.size(); i++) {
        }

        return result;
    }

    private void getRealArea(List<float[]> areas, int pos){
        for (int i = 0; i < areas.size(); i++) {
            float[] area = areas.get(i);

        }
    }

    public float[] getOverlapArea(QSImageView view) {
        float area[] = new float[views.size()];
        Rect targetRect = view.getRect();

        for (int i = 0; i < views.size(); i++) {
            QSImageView imageView = views.get(i);

            if (view == imageView) {
                area[i] = -1;
            }

            Rect rect = imageView.getRect();
            if (rect.right < targetRect.left || rect.top > targetRect.bottom || rect.left > targetRect.right || rect.bottom < targetRect.top) {
                area[i] = 0;
                continue;
            }

            float width = 0;
            float height = 0;

            int dw = rect.right - targetRect.right;
            int dh = rect.bottom - targetRect.bottom;

            if (dw > 0)
                width = imageView.getWidth() - Math.abs(dw);
            else
                width = view.getWidth() - Math.abs(dw);

            if (dh > 0)
                height = imageView.getHeight() - Math.abs(dh);
            else
                height = view.getHeight() - Math.abs(dh);

            area[i] = width * height;
        }

        for (int i = 0; i < area.length; i++) {
            Log.i("tag", "area " + " i: " + area[i]);
        }

        return area;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }


}

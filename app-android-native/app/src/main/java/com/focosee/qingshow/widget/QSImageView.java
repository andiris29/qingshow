package com.focosee.qingshow.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.focosee.qingshow.R;
import com.focosee.qingshow.util.AppUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2015/7/2.
 */
public class QSImageView extends RelativeLayout implements ScaleGestureDetector.OnScaleGestureListener {
    private float lastX;
    private float lastY;
    private float distanceX;
    private float distanceY;
    private int containerWidth;
    private int containerHeight;

    private ImageView imageView;
    private ImageView delBtn;

    private boolean isChecked = false;
    private String categoryId;

    private ScaleGestureDetector scaleGestureDetector;

    private float lastScaleFactor = 1.0f;
    private boolean isScaling = false;
    boolean isScalingJustEnd = false;

    private boolean moveable = false;


    private OnClickListener onDelClickListener;
    private int barHeight;

    public QSImageView(Context context) {
        this(context, null);
    }

    public QSImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QSImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        imageView = new ImageView(getContext());
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        imageView.setLayoutParams(params);
        imageView.setPadding((int) AppUtil.transformToDip(1, getContext()), (int) AppUtil.transformToDip(1 / lastScaleFactor, getContext())
                , (int) AppUtil.transformToDip(1, getContext()), (int) AppUtil.transformToDip(1 / lastScaleFactor, getContext()));
        addView(imageView);

        scaleGestureDetector = new ScaleGestureDetector(getContext(), this);
    }

    public void setImage(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        int pointerCount = event.getPointerCount();

        if (isScaling || pointerCount == 2) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    isScaling = false;
                    break;
            }
            return true;
        }

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                showDelBtn();
                setChecked(true);
                lastX = event.getRawX();
                lastY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isMoveable()) {
                    return false;
                }

                if (isScalingJustEnd) {
                    isScalingJustEnd = false;
                    return true;
                }

                if (event.getPointerCount() == 1) {
                    distanceX = lastX - event.getRawX();
                    distanceY = lastY - event.getRawY();
                    float nextY = getY() - distanceY;
                    float nextX = getX() - distanceX;

                    containerHeight = ((QSCanvasView) getParent()).getHeight();
                    containerWidth = ((QSCanvasView) getParent()).getWidth();

                    // 不能移出屏幕
                    if (nextY < -(getHeight() - getHeight() * lastScaleFactor) / 2)
                        nextY = -(getHeight() - getHeight() * lastScaleFactor) / 2;
                    else if (nextY > (containerHeight - getHeight() * lastScaleFactor) - (getHeight() - getHeight() * lastScaleFactor) / 2)
                        nextY = containerHeight - getHeight() * lastScaleFactor - (getHeight() - getHeight() * lastScaleFactor) / 2;

                    if (nextX < -(getWidth() - getWidth() * lastScaleFactor) / 2)
                        nextX = -(getWidth() - getWidth() * lastScaleFactor) / 2;
                    else if (nextX > (containerWidth - getWidth() * lastScaleFactor) - (getWidth() - getWidth() * lastScaleFactor) / 2)
                        nextX = (containerWidth - getWidth() * lastScaleFactor) - (getWidth() - getWidth() * lastScaleFactor) / 2;

                    ObjectAnimator y = ObjectAnimator.ofFloat(this, "y", getY(), nextY);
                    ObjectAnimator x = ObjectAnimator.ofFloat(this, "x", getX(), nextX);

                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(x, y);
                    animatorSet.setDuration(0);
                    animatorSet.start();

                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    return true;
                }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                goneDelBtn(1000);
                break;
        }
        return true;

    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            containerHeight = ((QSCanvasView) getParent()).getHeight();
            containerWidth = ((QSCanvasView) getParent()).getWidth();
        }
    }


    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
        onCheckedChanged(isChecked);
    }

    public boolean isChecked() {
        return isChecked;
    }

    private void onCheckedChanged(boolean isCheck) {
        if (isCheck) {
            this.setBackgroundResource(R.drawable.bg_canvas_item);
        } else {
            goneDelBtn();
            this.setBackgroundResource(0);
        }
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        goneDelBtn();
        float scaleFactor = detector.getScaleFactor();
        float temp = lastScaleFactor;
        lastScaleFactor *= scaleFactor;
        if (scaleFactor > 1) {
            if (checkBorder(scaleFactor)) {
                setScaleX(lastScaleFactor);
                setScaleY(lastScaleFactor);
            } else {
                lastScaleFactor = temp;
                scaleToBorder();
            }
        } else {
            setScaleX(lastScaleFactor);
            setScaleY(lastScaleFactor);
        }


        getArea();
        return true;
    }


    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        isScaling = true;
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        isScalingJustEnd = true;
    }

    private void scaleToBorder() {
        Rect rect = new Rect();
        this.getGlobalVisibleRect(rect);
        int result = rect.left;
        int index = 0;
        rect.top -= barHeight;
        int dis[] = new int[]{rect.left, rect.top, containerWidth - rect.right, containerHeight - rect.bottom};

        for (int i = 0; i < dis.length; i++) {
            if (dis[i] < result) {
                result = dis[i];
                index = i;
            }
        }

        if (index == 0 || index == 2) {
            lastScaleFactor *= result * 2 / rect.width() + 1;
        }
        if (index == 1 || index == 3) {
            lastScaleFactor *= (result * 2 / rect.height() + 1);
        }
        setScaleX(lastScaleFactor);
        setScaleY(lastScaleFactor);
    }

    private boolean checkBorder(float scaleFactor) {
        Rect rect = new Rect();
        this.getGlobalVisibleRect(rect);
        rect.top -= barHeight;

        float scaleX = rect.width() * (1 - scaleFactor) / 2;
        float scaleY = rect.height() * (1 - scaleFactor) / 2;

        if (!(rect.left > 0) || !(rect.right < containerWidth) || !(rect.top > 0) || !(rect.bottom < containerHeight)) {
            return false;
        }

        if (!(rect.left + scaleX > 0)) {
            return false;
        }

        if (rect.right + scaleX > containerWidth) {
            return false;
        }

        if (!(rect.top + scaleY > 0)) {
            return false;
        }

        if (rect.bottom + scaleY > containerHeight) {
            return false;
        }

        return true;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }


    public void setOnDelClickListener(OnClickListener onDelClickListener) {
        this.onDelClickListener = onDelClickListener;
    }

    public void showDelBtn() {
        goneDelBtn();
        delBtn = new ImageView(getContext());
        delBtn.setBackgroundResource(R.drawable.canvas_del);
        LayoutParams btnParams = new LayoutParams((int) AppUtil.transformToDip(50 / lastScaleFactor, getContext()),
                (int) AppUtil.transformToDip(50 / lastScaleFactor, getContext()));
        btnParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        delBtn.setLayoutParams(btnParams);
        delBtn.setOnClickListener(onDelClickListener);
        this.addView(delBtn);

    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (null != delBtn)
                QSImageView.this.removeView(delBtn);
            return false;
        }
    });

    public void goneDelBtn(int dalay) {
        Timer timer = new Timer("delBtn");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        }, dalay);
    }

    public void goneDelBtn(){
        if (null != delBtn)
            QSImageView.this.removeView(delBtn);
    }

    public boolean isMoveable() {
        return moveable;
    }

    public void setMoveable(boolean moveable) {
        this.moveable = moveable;
    }

    public float getArea() {
        Rect rect = new Rect();
        this.getGlobalVisibleRect(rect);
        return Math.abs((rect.right - rect.left)) * Math.abs((rect.bottom - rect.top));
    }

    public float getLastScaleFactor() {
        return lastScaleFactor;
    }


    public void setBarHeight(int barHeight) {
        this.barHeight = barHeight;
    }

    public void setLastScaleFactor(float lastScaleFactor) {
        this.lastScaleFactor = lastScaleFactor;
    }
}

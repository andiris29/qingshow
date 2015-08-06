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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.focosee.qingshow.R;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.RectUtil;

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

    private boolean moveable = false;
    boolean doubleFlag = false;

    private OnClickListener onDelClickListener;
    private int barHeight;
    private int padding = 2;

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
        imageView.setPadding((int) AppUtil.transformToDip(padding / lastScaleFactor, getContext()), (int) AppUtil.transformToDip(padding / lastScaleFactor, getContext())
                , (int) AppUtil.transformToDip(padding / lastScaleFactor, getContext()), (int) AppUtil.transformToDip(padding / lastScaleFactor, getContext()));
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
        if (pointerCount == 2) {
            doubleFlag = true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            doubleFlag = false;
        }

        if (doubleFlag) {
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

                if (pointerCount == 1) {
                    distanceX = lastX - event.getRawX();
                    distanceY = lastY - event.getRawY();

                    float nextY = getY() - distanceY;
                    float nextX = getX() - distanceX;
                    float dy = (getHeight() - getHeight() * lastScaleFactor) / 2;
                    float dx = (getWidth() - getWidth() * lastScaleFactor) / 2;

                    containerHeight = ((QSCanvasView) getParent()).getHeight();
                    containerWidth = ((QSCanvasView) getParent()).getWidth();

                    if (nextY < -dy)
                        nextY = -dy;
                    else if (nextY > (containerHeight - getHeight() * lastScaleFactor) - dy)
                        nextY = containerHeight - getHeight() * lastScaleFactor - dy;

                    if (nextX < -dx)
                        nextX = -dx;
                    else if (nextX > (containerWidth - getWidth() * lastScaleFactor) - dx)
                        nextX = (containerWidth - getWidth() * lastScaleFactor) - dx;

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

    public void resetPadding() {
        imageView.setPadding((int) AppUtil.transformToDip(padding / lastScaleFactor, getContext()), (int) AppUtil.transformToDip(padding / lastScaleFactor, getContext())
                , (int) AppUtil.transformToDip(padding / lastScaleFactor, getContext()), (int) AppUtil.transformToDip(padding / lastScaleFactor, getContext()));
    }

    @Override
    public void setScaleX(float scaleX) {
        super.setScaleX(scaleX);
        resetPadding();
    }

    @Override
    public void setScaleY(float scaleY) {
        super.setScaleY(scaleY);
        resetPadding();
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
        resetPadding();
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
            if (RectUtil.checkBorder(RectUtil.getRect(this), RectUtil.getParentRect(this), scaleFactor, barHeight)) {
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
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
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
            lastScaleFactor *= (result * 2 / rect.width() + 1);
        }
        if (index == 1 || index == 3) {
            lastScaleFactor *= (result * 2 / rect.height() + 1);
        }
        setScaleX(lastScaleFactor);
        setScaleY(lastScaleFactor);
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

    public void goneDelBtn() {
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
        Rect rect = RectUtil.getRect(this);
        return Math.abs((rect.right - rect.left)) * Math.abs((rect.bottom - rect.top));
    }


    public void setBarHeight(int barHeight) {
        this.barHeight = barHeight;
    }

    public void setLastScaleFactor(float lastScaleFactor) {
        this.lastScaleFactor = lastScaleFactor;
    }
}

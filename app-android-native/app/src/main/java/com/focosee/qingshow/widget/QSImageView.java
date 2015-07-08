package com.focosee.qingshow.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
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
    private boolean isScale = false;
    boolean isScaleJustEnd = false;
    private int doubleFlag = 2;

    private float intrinsicWidth = 0;
    private float intrinsicHeight = 0;

    private OnClickListener onDelClickListener;

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
        imageView.setPadding((int) AppUtil.transformToDip(1, getContext()), (int) AppUtil.transformToDip(1, getContext())
                , (int) AppUtil.transformToDip(1, getContext()), (int) AppUtil.transformToDip(1, getContext()));
        addView(imageView);

        delBtn = new ImageView(getContext());
        delBtn.setBackgroundResource(R.drawable.canvas_del);
        LayoutParams btnParams = new LayoutParams((int) AppUtil.transformToDip(50,getContext()),
                (int) AppUtil.transformToDip(50,getContext()));
        btnParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        delBtn.setLayoutParams(btnParams);
        delBtn.setOnClickListener(onDelClickListener);
        delBtn.setVisibility(GONE);
        addView(delBtn);

        scaleGestureDetector = new ScaleGestureDetector(getContext(), this);
    }

    public void setImage(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        if (isScale) {
            doubleFlag = 2;
            return true;
        }


        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                setChecked(true);
                lastX = event.getRawX();
                lastY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                goneDelBtn();
                if (isScaleJustEnd) {
                    isScaleJustEnd = false;
                    return true;
                }

                if (event.getPointerCount() == 1 && doubleFlag == 1) {
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

//                    Log.i("tag", "move " + " nextX: " + nextX + " nextY: " + nextY + " x: " + getX() + " y: " + getY() + " scale: " + lastScaleFactor);
                    ObjectAnimator y = ObjectAnimator.ofFloat(this, "y", getY(), nextY);
                    ObjectAnimator x = ObjectAnimator.ofFloat(this, "x", getX(), nextX);

                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(x, y);
                    animatorSet.setDuration(0);
                    animatorSet.start();

//                    Log.i("tag","x: " + getX() + "y: " + getY());
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    return true;
                }

                doubleFlag = event.getPointerCount();
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
            this.setBackgroundResource(0);
        }
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        goneDelBtn();
        float scaleFactor = detector.getScaleFactor();
        lastScaleFactor *= scaleFactor;

//        Log.i("tag", getActualMaxScale() + "");

        setScaleX(lastScaleFactor);
        setScaleY(lastScaleFactor);

        Log.i("tag","height: " + imageView.getDrawable().getIntrinsicHeight() + "width: " + imageView.getDrawable().getIntrinsicWidth());
//        Log.i("tag", "scale " + "x: " + getX() + " y: " + getY() + " scaleFacctor: " + scaleFactor);
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        isScale = true;
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        isScale = false;
        isScaleJustEnd = true;
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

    public void showDelBtn(){
        delBtn.setVisibility(VISIBLE);
        Timer timer = new Timer("delBtn");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        },2000);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            goneDelBtn();
            return false;
        }
    });

    public void goneDelBtn(){
        delBtn.setVisibility(GONE);
    }

    private void checkBorder(QSImageView view, float intrinsicWidth , float intrinsicHeight) {
        float nextX = view.getX();
        float nextY = view.getY();

        if (view.getX() + intrinsicWidth > ((QSCanvasView)getParent()).getWidth()) {
            nextX = ((QSCanvasView)getParent()).getWidth() - intrinsicWidth;
        }
        if (view.getY() + intrinsicHeight > ((QSCanvasView)getParent()).getHeight()) {
            nextY = ((QSCanvasView)getParent()).getHeight() - intrinsicHeight;
        }
        if (view.getX() < 0) {
            nextX = 0;
        }
        if (view.getY() < 0) {
            nextY = 0;
        }

        ObjectAnimator y = ObjectAnimator.ofFloat(view, "y", view.getY(), nextY);
        ObjectAnimator x = ObjectAnimator.ofFloat(view, "x", view.getX(), nextX);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(x, y);
        animatorSet.setDuration(0);
        animatorSet.start();
    }

    public void setContainerHeight(int containerHeight) {
        this.containerHeight = containerHeight;
    }

    public void setContainerWidth(int containerWidth) {
        this.containerWidth = containerWidth;
    }
}

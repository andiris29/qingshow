package com.focosee.qingshow.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.focosee.qingshow.R;

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

    private float mWidth;
    private float mHeight;

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
        addView(imageView);
//        delBtn = new ImageView(getContext());
//        delBtn.setBackgroundResource(R.drawable.canvas_del);
//        delBtn.setLayoutParams(params);
//        addView(delBtn);

        mHeight = getHeight();
        mWidth = getWidth();

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
                Log.i("tag", "down " + " x: " + lastX + " y: " + lastY);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isScaleJustEnd) {
                    isScaleJustEnd = false;
                    return true;
                }

                if (event.getPointerCount() == 1 && doubleFlag == 1) {
                    distanceX = lastX - event.getRawX();
                    distanceY = lastY - event.getRawY();
                    float nextY = getY() - distanceY;
                    float nextX = getX() - distanceX;

                    // 不能移出屏幕
//                    if (nextY < -(getHeight() - getHeight() * lastScaleFactor) / 2)
//                        nextY = -(getHeight() - getHeight() * lastScaleFactor) / 2;
//                    else if (nextY > (containerHeight - getHeight() * lastScaleFactor) - (getHeight() - getHeight() * lastScaleFactor) / 2)
//                        nextY = containerHeight - getHeight() * lastScaleFactor - (getHeight() - getHeight() * lastScaleFactor) / 2;
//
//                    if (nextX < -(getWidth() - getWidth() * lastScaleFactor) / 2)
//                        nextX = -(getWidth() - getWidth() * lastScaleFactor) / 2;
//                    else if (nextX > (containerWidth - getWidth() * lastScaleFactor) - (getWidth() - getWidth() * lastScaleFactor) / 2)
//                        nextX = (containerWidth - getWidth() * lastScaleFactor) - (getWidth() - getWidth() * lastScaleFactor) / 2;

                    Log.i("tag", "move " + " nextX: " + nextX + " nextY: " + nextY + " x: " + getX() + " y: " + getY() + " scale: " + lastScaleFactor);
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

                doubleFlag = event.getPointerCount();

            case MotionEvent.ACTION_UP:
                Log.i("tag", "up");
                break;
//                lastPointerCount = pointerCount;
            // 属性动画移动
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
        float scaleFactor = detector.getScaleFactor();
        lastScaleFactor *= scaleFactor;

//        Log.i("tag", getActualMaxScale() + "");

        ObjectAnimator y = ObjectAnimator.ofFloat(this, "scaleY", lastScaleFactor);
        ObjectAnimator x = ObjectAnimator.ofFloat(this, "scaleX", lastScaleFactor);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(x, y);
        animatorSet.setDuration(0);
        animatorSet.start();

        Log.i("tag", "scale " + "x: " + getX() + " y: " + getY() + " scaleFacctor: " + scaleFactor);
        return true;
    }

    private float getActualMaxScale() {
        float scaleArr[] = new float[4];
        float x = getX() - (getWidth() - getWidth() * lastScaleFactor) / 2;
        float y = getY() - (getHeight() - getHeight() * lastScaleFactor) / 2;

        scaleArr[0] = (x * 2 + getWidth()) / getWidth();
        scaleArr[1] = (y * 2 + getHeight()) / getHeight();
        scaleArr[2] = ((containerWidth - getWidth() - x) * 2 + getWidth()) / getWidth();
        scaleArr[3] = ((containerHeight - getHeight() - y) * 2 + getHeight()) / getHeight();

        for (int i = 0; i < scaleArr.length; i++) {
            for (int j = i + 1; j < scaleArr.length; j++) {
                float temp = scaleArr[i];
                scaleArr[i] = scaleArr[j];
                scaleArr[j] = temp;
            }
        }

        return scaleArr[0];
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        Log.i("tag", "begin");
        isScale = true;
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        Log.i("tag", "end");
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

}

package com.qs.myapplication;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2015/7/2.
 */
public class QSImageView extends RelativeLayout {
    float lastX;
    float lastY;
    private int containerWidth;
    private int containerHeight;

    private ImageView imageView;
    private ImageView delBtn;

    private boolean isChecked = false;
    private String id;

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
        imageView.setLayoutParams(params);
        addView(imageView);
        delBtn = new ImageView(getContext());
        delBtn.setBackgroundResource(R.drawable.canvas_del);
        LayoutParams paramsBtn = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        paramsBtn.addRule(RelativeLayout.CENTER_IN_PARENT);
        delBtn.setLayoutParams(paramsBtn);
        addView(delBtn);
    }

    public ImageView getImageView(){
        return imageView;
    }

    public void setImage(Bitmap bitmap){
        imageView.setImageBitmap(bitmap);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
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
        setChecked(true);
        return false;
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

    public boolean isChecked(){
        return  isChecked;
    }

    private void onCheckedChanged(boolean isCheck) {
        if(isCheck){
            imageView.setBackgroundResource(R.drawable.bg_canvas_item);
        }else {
            imageView.setBackgroundResource(0);
        }
    }

}

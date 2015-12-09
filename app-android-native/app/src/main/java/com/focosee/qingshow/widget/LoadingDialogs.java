package com.focosee.qingshow.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.focosee.qingshow.R;

/**
 * Created by Administrator on 2015/8/10.
 */
public class LoadingDialogs extends Dialog {

    private ImageView load;
    static final int ROTATION_ANIMATION_DURATION = 1200;
    static final Interpolator ANIMATION_INTERPOLATOR = new LinearInterpolator();
    private Animation rotateAnimation;

    public LoadingDialogs(Context context) {
        super(context, R.style.dialog);
        init();
    }

    public LoadingDialogs(Context context, int theme) {
        super(context, theme);
        init();
    }

    private void init() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_dialog);
        load = (ImageView) findViewById(R.id.bar);
        setUpAnim();
    }

    @Override
    public void show() {
        super.show();
        load.setAnimation(rotateAnimation);
        setCanceledOnTouchOutside(false);
        rotateAnimation.start();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (load != null)
        load.clearAnimation();
    }

    private void setUpAnim() {
        float pivotValue = 0.5f;    // SUPPRESS CHECKSTYLE
        float toDegree = 720.0f;
        rotateAnimation = new RotateAnimation(0.0f, toDegree, Animation.RELATIVE_TO_SELF, pivotValue,
                Animation.RELATIVE_TO_SELF, pivotValue);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
        rotateAnimation.setDuration(ROTATION_ANIMATION_DURATION);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setRepeatMode(Animation.RESTART);
    }
}

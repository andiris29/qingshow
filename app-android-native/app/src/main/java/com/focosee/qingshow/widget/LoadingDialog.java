package com.focosee.qingshow.widget;


import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.focosee.qingshow.R;

/**
 * Created by Administrator on 2015/7/30.
 */
public class LoadingDialog extends AbsDialog implements View.OnClickListener {

    static final int ROTATION_ANIMATION_DURATION = 1200;
    static final Interpolator ANIMATION_INTERPOLATOR = new LinearInterpolator();

    private ImageView imageView;
    private View rootView;

    private Animation rotateAnimation;

    public LoadingDialog(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageView = (ImageView) rootView.findViewById(R.id.bar);
        setUpAnim();
        imageView.setAnimation(rotateAnimation);
        rotateAnimation.start();
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

    @Override
    public View getRootView(LayoutInflater inflater, ViewGroup container) {
        rootView = inflater.inflate(R.layout.loading_dialog, container);
        return rootView;
    }
}

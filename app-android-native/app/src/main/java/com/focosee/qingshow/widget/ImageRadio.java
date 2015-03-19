package com.focosee.qingshow.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;


/**
 * Created by Administrator on 2015/3/13.
 */
public class ImageRadio extends ImageView {

    private boolean check = false;

    public ImageRadio(Context context) {
        this(context, null);
    }

    public ImageRadio(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageRadio(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(150, 150);
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(l, t, r, b);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        setCheck(true);
        return false;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public boolean isCheck() {
        return check;
    }
}

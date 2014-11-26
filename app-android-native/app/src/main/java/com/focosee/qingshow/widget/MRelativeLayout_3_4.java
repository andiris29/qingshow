package com.focosee.qingshow.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

/**
 * Created by jackyu on 11/26/14.
 */
public class MRelativeLayout_3_4 extends RelativeLayout {

    public MRelativeLayout_3_4(Context context) {
        super(context);
    }

    public MRelativeLayout_3_4(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MRelativeLayout_3_4(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.getSize(widthMeasureSpec * 4 / 3) + MeasureSpec.EXACTLY;

        Log.i("app", "width-all" + String.valueOf(widthMeasureSpec));
        Log.i("app", "height-all" + String.valueOf(heightMeasureSpec));
        Log.i("app", "width-mode" + MeasureSpec.getMode(widthMeasureSpec));
        Log.i("app", "height-mode" + MeasureSpec.getMode(heightMeasureSpec));
        Log.i("app", "width" + String.valueOf(MeasureSpec.getSize(widthMeasureSpec)));
        Log.i("app", "height" + String.valueOf(MeasureSpec.getSize(heightMeasureSpec)));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


}

package com.focosee.qingshow.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class MRelativeLayout_Custom extends RelativeLayout {

    private int originWidth;
    private int originHeight;

    public MRelativeLayout_Custom(Context context) {
        super(context);
    }

    public MRelativeLayout_Custom(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MRelativeLayout_Custom(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOriginWidth(int width) {
        originWidth = width;
    }

    public void setOriginHeight(int height) {
        originHeight = height;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.getSize(widthMeasureSpec * 4 / 3) + MeasureSpec.EXACTLY;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


}

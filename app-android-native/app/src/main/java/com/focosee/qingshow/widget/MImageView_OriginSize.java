package com.focosee.qingshow.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MImageView_OriginSize extends ImageView {

    private int originWidth;
    private int originHeight;

    public MImageView_OriginSize(Context context) {
        super(context);
    }

    public MImageView_OriginSize(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MImageView_OriginSize(Context context, AttributeSet attrs, int defStyle) {
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
        if (originWidth != 0 && originHeight != 0)
            heightMeasureSpec = MeasureSpec.getSize(widthMeasureSpec * originHeight / originWidth) + MeasureSpec.EXACTLY;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}

package com.focosee.qingshow.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2015/11/17.
 */
public class TagDotView extends View {

    private int radius;
    public static final int RADIUS_OFFSET = 10;

    public TagDotView(Context context) {
        super(context);
    }

    public TagDotView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TagDotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint dotPaint = new Paint();
        dotPaint.setColor(getResources().getColor(android.R.color.holo_red_light));
        canvas.drawCircle(500, 500, radius, dotPaint);
        canvas.save();
        canvas.restore();

        if (radius <= 60){
            radius += RADIUS_OFFSET;
            if (radius == 20){

            }
            postInvalidateDelayed(10);
        }else if(radius > 60){
            radius = 20;
            postInvalidateDelayed(10);
        }
    }
}

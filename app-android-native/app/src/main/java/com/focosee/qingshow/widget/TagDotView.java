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

    private static final int TYPE_RIPPLE_ACT = 1;
    private static final int TYPE_DOT_ACT = 2;

    private static final int DELAY = 30;
    private static final int RIPPLE_OFFSET = 5;
    private static final int RIPPLE_RETIMES = 2;

    private int rippleDefault = 0;
    private int dotDefault = 20;
    private int rippleMax = 65;
    private int alphaDefault = 135;

    private Paint dotPaint;
    private Paint ripplePaint;
    private int rippleRadius;
    private int dotRadius;
    private int alpha;
    private int alphaOffset;
    private int time;

    private int dotX;
    private int dotY;
    private int type;

    public TagDotView(Context context, int x, int y){
        this(context);
        setDotX(x);
        setDotY(y);
    }

    public TagDotView(Context context) {
        this(context, null);
    }

    public TagDotView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagDotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDraw();
    }

    public void initDraw() {
        dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotPaint.setColor(getResources().getColor(android.R.color.white));
        dotPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        ripplePaint = new Paint();
        ripplePaint.setColor(getResources().getColor(android.R.color.black));

        rippleRadius = rippleDefault;
        dotRadius = dotDefault;
        alpha = alphaDefault;
        alphaOffset = alphaDefault / ((rippleMax - rippleDefault) / RIPPLE_OFFSET);
        type = TYPE_RIPPLE_ACT;
        time = 1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        ripplePaint.setAlpha(alpha);
        canvas.drawCircle(getDotX(), getDotY(), rippleRadius, ripplePaint);
        canvas.drawCircle(getDotX(), getDotY(), dotRadius, dotPaint);
        canvas.restore();
        switch (type) {
            case TYPE_RIPPLE_ACT:
                dotRadius = dotDefault;
                if (rippleRadius + RIPPLE_OFFSET < rippleMax) {
                    rippleRadius += RIPPLE_OFFSET;
                    alpha -= alphaOffset;
                    postInvalidateDelayed(DELAY);
                } else {
                    alpha = alphaDefault;
                    rippleRadius = rippleDefault;
                    if (time < RIPPLE_RETIMES) {
                        time++;
                    }else {
                        time = 1;
                        type = TYPE_DOT_ACT;
                    }
                    postInvalidateDelayed(DELAY);
                }
                break;
            case TYPE_DOT_ACT:
                type = TYPE_RIPPLE_ACT;
                postInvalidateDelayed(DELAY * 5);
                break;
        }

    }

    public int getDotY() {
        return dotY;
    }

    public void setDotY(int dotY) {
        this.dotY = dotY;
    }

    public int getDotX() {
        return dotX;
    }

    public void setDotX(int dotX) {
        this.dotX = dotX;
    }

}

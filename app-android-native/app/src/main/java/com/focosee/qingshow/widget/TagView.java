package com.focosee.qingshow.widget;

import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.util.AppUtil;

/**
 * Created by Administrator on 2015/12/4.
 */
public class TagView extends FrameLayout {

    private static final float TAG_HEIGHT = 20f;
    private static final float TAG_WIDTH = 45f;
    private RectF rectF;
    private TextView tag;

    public TagView(Context context){
        this(context, null);
    }

    public TagView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initByRectF(RectF rectF){
        this.rectF = rectF;
        this.setX(rectF.left);
        this.setY(rectF.top);
        LayoutParams params = new LayoutParams((int) rectF.width(), (int) rectF.height());
        this.setLayoutParams(params);
        setBackgroundColor(getResources().getColor(R.color.master_blue));
        initTag(rectF);
    }

    private void initTag(RectF rectF){
        tag = new TextView(getContext());
        tag.setX(rectF.centerX());
        tag.setY(rectF.centerY());
        LayoutParams params = new LayoutParams((int) AppUtil.transformToDip(TAG_WIDTH, getContext()),
                (int)AppUtil.transformToDip(TAG_HEIGHT, getContext()));
        tag.setLayoutParams(params);
        tag.setBackgroundDrawable(getResources().getDrawable(R.drawable.show_tag_background));
        this.addView(tag);
    }

    public void setText(CharSequence text){
        tag.setText(text);
    }

}

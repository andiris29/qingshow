package com.focosee.qingshow.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.gesture.GestureUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.util.FontsUtil;

import java.sql.Array;

/**
 * Created by Administrator on 2015/7/28.
 */
public class QSTextView extends TextView {

    private float clickAlpha;
    private float unClickAlpha;
    private boolean clickble = false;//can click

    public QSTextView(Context context) {
        this(context, null);
    }

    public QSTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QSTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (null != attrs){
            final int attrIds[] = new int[]{R.attr.check_color, R.attr.uncheck_color};
            TypedArray array = context.obtainStyledAttributes(attrs,
                    attrIds);
            clickAlpha = array.getColor(0,0);
            unClickAlpha = getAlpha();
            array.recycle();
        }
        setFont("fonts/black_fangzheng_simple.TTF");
    }

    public void setFont(String path) {
        FontsUtil.changeFont(getContext(), this, path);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        if(isClickable()) {
//            if (event.getAction() == )
//                setAlpha(0.5f);
//            else
//                setAlpha(unClickAlpha);
//        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean callOnClick() {
        return super.callOnClick();
    }
}

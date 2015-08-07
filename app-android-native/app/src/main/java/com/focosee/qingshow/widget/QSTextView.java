package com.focosee.qingshow.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.gesture.GestureUtils;
import android.graphics.Paint;
import android.text.TextUtils;
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
    private boolean delLine = false;
    private CharSequence preText="";

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
            TypedArray array = context.obtainStyledAttributes(attrs,
                    R.styleable.QSTextView);
//            clickAlpha = array.getColor(0, 0);
//            unClickAlpha = getAlpha();
            delLine = array.getBoolean(R.styleable.QSTextView_del_line, false);
            preText = array.getString(R.styleable.QSTextView_preText);
            System.out.println("QSTextView_delLine:"+delLine);
            System.out.println("QSTextView_preText:"+preText);
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

    @Override
    public void setText(CharSequence text, BufferType type) {
        if(delLine){
            getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
        if(TextUtils.isEmpty(preText))
            super.setText(text, type);
        else
            super.setText(preText.toString() + text, type);
    }
}

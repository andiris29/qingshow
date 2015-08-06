package com.focosee.qingshow.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.focosee.qingshow.util.FontsUtil;

/**
 * Created by Administrator on 2015/7/28.
 */
public class QSTextView extends TextView {
    public QSTextView(Context context) {
        this(context, null);
    }

    public QSTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QSTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //setFont("fonts/black_fangzheng_simple.TTF");
    }

    public void setFont(String path){
        FontsUtil.changeFont(getContext(),this,path);
    }
}

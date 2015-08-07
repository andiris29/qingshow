package com.focosee.qingshow.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import com.focosee.qingshow.util.FontsUtil;

/**
 * Created by Administrator on 2015/8/7.
 */
public class QSEditText extends EditText {

    public QSEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QSEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setFont("fonts/black_fangzheng_simple.TTF");
    }

    public void setFont(String path) {
        FontsUtil.changeFont(getContext(), this, path);
    }

}

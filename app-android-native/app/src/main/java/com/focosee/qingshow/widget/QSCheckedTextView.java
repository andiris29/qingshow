package com.focosee.qingshow.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckedTextView;
import com.focosee.qingshow.util.FontsUtil;

/**
 * Created by Administrator on 2015/8/19.
 */
public class QSCheckedTextView extends CheckedTextView {
    public QSCheckedTextView(Context context) {
        this(context, null);
    }

    public QSCheckedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QSCheckedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFont("fonts/black_fangzheng_simple.TTF");
    }

    public void setFont(String path) {
        FontsUtil.changeFont(getContext(), this, path);
    }
}

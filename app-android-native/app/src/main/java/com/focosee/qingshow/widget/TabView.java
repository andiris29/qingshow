package com.focosee.qingshow.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.focosee.qingshow.R;

/**
 * Created by Administrator on 2015/4/29.
 */
public class TabView extends RelativeLayout {
    private boolean check = false;
    private int checkColor;
    private int unCheckColor;

    public TabView(Context context) {
        this(context, null, 0);
    }

    public TabView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context,attrs);
        setCheck(false);
    }

    private void init(Context context,AttributeSet attrs) {
        if (null != attrs){
            final int attrIds[] = new int[]{R.attr.check_color, R.attr.uncheck_color};
            TypedArray array = context.obtainStyledAttributes(attrs,
                    attrIds);
            checkColor = array.getColor(0,0);
            unCheckColor = array.getColor(1,0);
            array.recycle();
        }
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
        if(check){
            setBackgroundColor(checkColor);
        }else {
            setBackgroundColor(unCheckColor);
        }
    }
}

package com.focosee.qingshow.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;
import com.focosee.qingshow.R;
import com.focosee.qingshow.util.FontsUtil;

/**
 * Created by Administrator on 2015/8/7.
 */
public class QSButton extends Button{

    private int clickGround;

    public QSButton(Context context) {
        this(context, null);
    }

    public QSButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public QSButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (null != attrs){
            TypedArray array = context.obtainStyledAttributes(attrs,
                    R.styleable.QSButton);
            clickGround = array.getResourceId(R.styleable.QSButton_clickGround, R.color.gray_background);
            array.recycle();
        }
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void init(Context context){
        System.out.println("clickGround:" + clickGround);
        setFont("fonts/black_fangzheng_simple.TTF");
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{-android.R.attr.state_pressed},
               getBackground());
        drawable.addState(new int[]{android.R.attr.state_pressed},
                context.getResources().getDrawable(clickGround));
        setBackground(drawable);
    }

    public void setFont(String path) {
        FontsUtil.changeFont(getContext(), this, path);
    }

}

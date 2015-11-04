package com.focosee.qingshow.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageButton;

import com.focosee.qingshow.R;
import com.focosee.qingshow.util.FontsUtil;

/**
 * Created by Administrator on 2015/8/7.
 */
public class QSImageButton extends ImageButton{

    private int clickGround = R.color.btn_background;

    public QSImageButton(Context context) {
        this(context, null);
    }

    public QSImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public QSImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void init(Context context, AttributeSet attrs){
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{-android.R.attr.state_pressed},
               getBackground());
        drawable.addState(new int[]{android.R.attr.state_pressed},
                context.getResources().getDrawable(clickGround));
        if(Build.VERSION.SDK_INT >= 16)
            setBackground(drawable);
        else
            setBackgroundDrawable(drawable);
    }
}

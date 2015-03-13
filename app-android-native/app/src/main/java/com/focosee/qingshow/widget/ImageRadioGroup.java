package com.focosee.qingshow.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.focosee.qingshow.R;

/**
 * Created by Administrator on 2015/3/13.
 */
public class ImageRadioGroup extends FlowRadioGroup {

    public ImageRadioGroup(Context context) {
        super(context);
    }

    public ImageRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        for (int i = 0; i < getChildCount(); i++) {
            ImageRadio item = (ImageRadio) getChildAt(i);
            item.setCheck(false);
            item.setImageResource(0);
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        for (int i = 0; i < getChildCount(); i++) {
            ImageRadio item = (ImageRadio) getChildAt(i);
            if (item.isCheck()) {
                item.setImageResource(R.drawable.s11_item_chek);
            } else {
                item.setImageResource(0);
            }
        }
        return super.onTouchEvent(event);
    }
}

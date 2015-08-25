package com.focosee.qingshow.widget.Flow;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.focosee.qingshow.widget.radio.IRadioViewHelper;

/**
 * Created by Administrator on 2015/3/13.
 */
public class FlowRadioGroup extends FlowLayout {

    private int lastCheckedIndex;
    private int checkedIndex;

    private OnCheckedChangeListener onCheckedChangeListener;

    public interface OnCheckedChangeListener {
        public void checkedChanged(int index);
    }

    public FlowRadioGroup(Context context) {
        this(context, null);
    }

    public FlowRadioGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowRadioGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean check[] = new boolean[getChildCount()];
        int checkedCount = 0;

        for (int i = 0; i < getChildCount(); i++) {
            IRadioViewHelper item = (IRadioViewHelper) getChildAt(i);
            if (item.isChecked()) {
                ++checkedCount;
                check[i] = true;
            } else {
                check[i] = false;
            }
        }

        if (1 == checkedCount) {

            for (int i = 0; i < check.length; i++) {
                if (check[i]) {
                    lastCheckedIndex = i;
                    if (null != onCheckedChangeListener) {
                        onCheckedChangeListener.checkedChanged(i);
                    }
                }
            }
        }

        if (2 == checkedCount) {
            for (int i = 0; i < check.length; i++) {
                IRadioViewHelper item = (IRadioViewHelper) getChildAt(i);
                if (i == lastCheckedIndex && check[i]) {
                    item.setChecked(false);
                } else if (i != lastCheckedIndex && check[i]) {
                    checkedIndex = i;
                    if (null != onCheckedChangeListener) {
                        onCheckedChangeListener.checkedChanged(i);
                    }
                }
            }
            lastCheckedIndex = checkedIndex;
        }

        return super.onTouchEvent(event);
    }


}

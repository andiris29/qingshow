package com.focosee.qingshow.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.focosee.qingshow.R;

/**
 * Created by Administrator on 2015/3/13.
 */
public class ImageRadioGroup extends FlowRadioGroup {

    private int lastCheckedIndex;
    private int checkedIndex;

    private OnCheckedChangeListener onCheckedChangeListener;

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public interface OnCheckedChangeListener {
        public void checkedChanged(View view, int index);
    }

    public ImageRadioGroup(Context context) {
        super(context);
    }

    public ImageRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.i("tagimg", "reCheck~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        for (int i = 0; i < getChildCount(); i++) {
            ImageRadio item = (ImageRadio) getChildAt(i);
            item.setImageResource(0);
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean check[] = new boolean[getChildCount()];
        int checkedCount = 0;


        for (int i = 0; i < getChildCount(); i++) {
            ImageRadio item = (ImageRadio) getChildAt(i);
            if (item.isCheck()) {
                ++checkedCount;
                check[i] = true;
            } else {
                check[i] = false;
            }
        }

        Log.i("tagimg", "checkedCount" + checkedCount);
        if (1 == checkedCount) {

            for (int i = 0; i < check.length; i++) {
                if (check[i]) {
                    Log.i("tagimg", "chek" + i);
                    ImageRadio item = (ImageRadio) getChildAt(i);
                    item.setImageResource(R.drawable.s11_item_chek);
                    lastCheckedIndex = i;
                    if (null != onCheckedChangeListener) {
                        onCheckedChangeListener.checkedChanged(item, i);
                    }
                }
            }
        }
        if (2 == checkedCount) {
            for (int i = 0; i < check.length; i++) {
                if (i == lastCheckedIndex && check[i]) {
                    ImageRadio item = (ImageRadio) getChildAt(i);
                    item.setCheck(false);
                    Log.i("tagimg", "rechek" + i);
                    item.setImageResource(0);
                } else if (i != lastCheckedIndex && check[i]) {
                    Log.i("tagimg", "chek" + i);
                    ImageRadio item = (ImageRadio) getChildAt(i);
                    item.setImageResource(R.drawable.s11_item_chek);
                    checkedIndex = i;
                    if (null != onCheckedChangeListener) {
                        onCheckedChangeListener.checkedChanged(item, i);
                    }
                }
            }
            lastCheckedIndex = checkedIndex;
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}

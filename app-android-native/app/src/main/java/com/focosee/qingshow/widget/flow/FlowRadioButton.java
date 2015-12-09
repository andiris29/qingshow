package com.focosee.qingshow.widget.flow;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;
import com.focosee.qingshow.R;
import com.focosee.qingshow.widget.radio.IRadioViewHelper;

/**
 * Created by Administrator on 2015/3/20.
 */
public class FlowRadioButton extends Button implements IRadioViewHelper {

    private boolean check = false;
    private boolean enable = true;

    public FlowRadioButton(Context context) {
        this(context, null);
    }

    public FlowRadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowRadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        setChecked(!check);
        return false;
    }


    @Override
    public void setChecked(boolean check) {
        this.check = check;
        onCheckedChanged(check);
    }

    @Override
    public void setEnable(boolean enable) {
        this.enable = enable;
        if (enable) {
            setBackgroundResource(R.drawable.gay_btn_ring);
            setTextColor(getResources().getColor(R.color.gary));
        } else {
            setBackgroundResource(R.drawable.s11_gray_btn);
            setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void onCheckedChanged(boolean isCheck) {
        if (!isEnable()) return;
        if (isCheck) {
            setBackgroundResource(R.drawable.blue_btn_fall);
            setTextColor(Color.WHITE);
        } else {
            setBackgroundResource(R.drawable.gay_btn_ring);
            setTextColor(Color.GRAY);
        }
    }

    @Override
    public boolean isChecked() {
        return check;
    }

    @Override
    public boolean isEnable() {
        return enable;
    }
}

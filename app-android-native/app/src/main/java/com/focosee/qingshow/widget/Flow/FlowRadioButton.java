package com.focosee.qingshow.widget.flow;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

import com.focosee.qingshow.R;

/**
 * Created by Administrator on 2015/3/20.
 */
public class FlowRadioButton extends Button implements IRadioViewHelper{

    private boolean check = false;

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
        setChecked(true);
        return false;
    }


    @Override
    public void setChecked(boolean check) {
        this.check = check;
        onCheckedChanged(check);
    }

    private void onCheckedChanged(boolean isCheck) {
        if(isCheck){
            setBackgroundResource(R.drawable.s11_size_item_check_bg);
            setTextColor(Color.WHITE);
        }else {
            setBackgroundResource(R.drawable.s11_size_item_bg);
            setTextColor(Color.BLACK);
        }
    }

    @Override
    public boolean isChecked() {
        return check;
    }
}

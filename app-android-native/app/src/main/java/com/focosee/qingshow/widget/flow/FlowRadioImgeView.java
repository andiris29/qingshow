package com.focosee.qingshow.widget.flow;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.widget.radio.IRadioViewHelper;


/**
 * Created by Administrator on 2015/3/13.
 */
public class FlowRadioImgeView extends ImageView implements IRadioViewHelper {


    private boolean check = false;
    private boolean isEnable = true;

    public FlowRadioImgeView(Context context) {
        this(context, null);
    }

    public FlowRadioImgeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowRadioImgeView(Context context, AttributeSet attrs, int defStyle) {
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

    @Override
    public void setEnable(boolean enable) {
        this.isEnable = enable;
    }

    private void onCheckedChanged(boolean isCheck) {
        if(isCheck){
            setImageResource(R.drawable.s11_item_chek);
        }else {
            setImageResource(0);
        }
    }

    @Override
    public boolean isChecked() {
        return check;
    }

    @Override
    public boolean isEnable() {
        return false;
    }
}

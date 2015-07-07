package com.focosee.qingshow.widget.radio;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.focosee.qingshow.R;

/**
 * Created by DylanJiang on 15/5/7.
 */
public class RadioLayout extends RelativeLayout implements IRadioViewHelper {

    private boolean check;

    public RadioLayout(Context context) {
        this(context, null, 0);
    }

    public RadioLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadioLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setChecked(boolean check) {
        this.check = check;
        onCheckedChanged(check);
    }

    @Override
    public boolean isChecked() {
        return check;
    }
    private void onCheckedChanged(boolean isCheck) {
        if(isCheck){
            setBackgroundResource(R.drawable.pinck_btn);
        }else {
            setBackgroundResource(R.drawable.gray_btn_ring);
        }
    }
}

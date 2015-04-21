package com.focosee.qingshow.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2015/3/13.
 */
public class PaymentGroup extends LinearLayout {

    private OnCheckedChangeListener onCheckedChangeListener;

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public interface OnCheckedChangeListener {
        public void checkedChanged(PaymentRadio view, String paymentMode);
    }

    public PaymentGroup(Context context) {
        super(context);
    }

    public PaymentGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PaymentGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof PaymentRadio) {
                PaymentRadio item = (PaymentRadio) getChildAt(i);
                item.setCheck(false);
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof PaymentRadio) {

                PaymentRadio item = (PaymentRadio) getChildAt(i);
                if (item.isCheck()) {
                    if (null != onCheckedChangeListener) {
                        onCheckedChangeListener.checkedChanged(item, item.getPaymentMode());
                    }
                } else {
                    item.setCheck(false);
                }
            }
        }

        return super.onTouchEvent(event);
    }
}

package com.focosee.qingshow.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.focosee.qingshow.R;

/**
 * Created by Administrator on 2015/3/12.
 */
public class PaymentRadio extends RelativeLayout {

    private RelativeLayout rootView;
    private RelativeLayout layout;
    private ImageView logoView;

    private Context context;

    private int logoRes;
    private String nameStr;
    private String infoStr;
    private int checkedRes;
    private int inCheckRes;
    private String paymentMode;

    private boolean check;


    public PaymentRadio(Context context) {
        this(context, null);
    }

    public PaymentRadio(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaymentRadio(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initAttrbutes(attrs);
        init();
    }


    private void initAttrbutes(AttributeSet attrs) {
        if (null != attrs) {
            final int attrIds[] = new int[]{R.attr.logo_res, R.attr.name_text, R.attr.info_text,
                    R.attr.checked, R.attr.button_res, R.attr.mode};
            TypedArray array = context.obtainStyledAttributes(attrs,
                    attrIds);
            logoRes = array.getResourceId(0, 0);
            nameStr = array.getString(1);
            infoStr = array.getString(2);
            checkedRes = array.getResourceId(3, 0);
            inCheckRes = array.getResourceId(4, 0);
            paymentMode = array.getString(5);
            array.recycle();
        }
    }

    private void init() {
        rootView = (RelativeLayout) inflate(getContext(), R.layout.item_s17_payment, this);
        logoView = (ImageView) rootView.findViewById(R.id.s11_paymet_logo);
        layout = (RelativeLayout) rootView.findViewById(R.id.payment_rl);

        logoView.setImageResource(logoRes);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        setCheck(true);
        return false;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
        if (check) {
            layout.setBackgroundResource(checkedRes);
        } else {
            layout.setBackgroundResource(inCheckRes);
        }
    }

    public String getPaymentMode() {
        return paymentMode;
    }
}

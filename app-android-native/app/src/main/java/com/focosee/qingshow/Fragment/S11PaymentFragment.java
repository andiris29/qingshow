package com.focosee.qingshow.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.focosee.qingshow.R;
import com.focosee.qingshow.widget.PaymentGroup;
import com.focosee.qingshow.widget.PaymentRadio;

/**
 * Created by Administrator on 2015/3/11.
 */
public class S11PaymentFragment extends Fragment {

    private View rootView;
    private PaymentGroup paymentGroup;
    private static final String WEI_XIN = "weixin";
    private static final String ALI_PAY = "alipay";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_s11_payment,container,false);
        initView();
        return rootView;
    }

    private void initView() {
        paymentGroup = (PaymentGroup) rootView.findViewById(R.id.s11_payment);
        paymentGroup.setOnCheckedChangeListener(new PaymentGroup.OnCheckedChangeListener() {

            @Override
            public void checkedChanged(PaymentRadio view, String paymentMode) {
                if (paymentMode.equals(WEI_XIN)) {

                } else if (paymentMode.equals(ALI_PAY)) {

                }
            }
        });
    }


}

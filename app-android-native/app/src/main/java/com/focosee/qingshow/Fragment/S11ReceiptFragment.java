package com.focosee.qingshow.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.focosee.qingshow.R;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;

/**
 * Created by Administrator on 2015/3/11.
 */
public class S11ReceiptFragment extends Fragment {

    private View rootView;
    private EditText nameView;
    private EditText phoneView;
    private EditText addressView;
    private String provinceStr = "广西壮族自治区柳州市市辖区";
    private MongoPeople.Receiver receiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_s11_receipt,container,false);
        MongoPeople people = new MongoPeople();
        receiver = people.new Receiver();
        init();
        return rootView;
    }

    private void init() {
        nameView = (EditText) rootView.findViewById(R.id.s11_receipt_name);
        phoneView = (EditText) rootView.findViewById(R.id.s11_receipt_phone);
        addressView = (EditText) rootView.findViewById(R.id.s11_receipt_address);

    }

    public MongoPeople.Receiver getReceiver(){

        receiver.name = nameView.getText().toString();
        receiver.phone = phoneView.getText().toString();
        receiver.address = addressView.getText().toString();
        receiver.province = provinceStr;

        return receiver;
    }

}

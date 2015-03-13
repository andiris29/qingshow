package com.focosee.qingshow.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.focosee.qingshow.R;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;

/**
 * Created by Administrator on 2015/3/11.
 */
public class S11ReceiptFragment extends Fragment{

    private View rootView;
    private EditText nameView;
    private EditText phoneView;
    private EditText addressView;
    private String provinceView;
    private MongoPeople.Receiver receiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_s11_receipt,container,false);
        init();
        return rootView;
    }

    private void init() {
        nameView = (EditText) rootView.findViewById(R.id.s11_receipt_name);
        phoneView = (EditText) rootView.findViewById(R.id.s11_receipt_phone);
        addressView = (EditText) rootView.findViewById(R.id.s11_receipt_address);

        receiver.name = nameView.getText().toString();
        receiver.phone = phoneView.getText().toString();
        receiver.address = addressView.getText().toString();
    }

}

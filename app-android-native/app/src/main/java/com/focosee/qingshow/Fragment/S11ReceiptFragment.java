package com.focosee.qingshow.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S11NewTradeActivity;
import com.focosee.qingshow.activity.U10AddressListActivity;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;

/**
 * Created by Administrator on 2015/3/11.
 */
public class S11ReceiptFragment extends Fragment implements View.OnClickListener{

    private View rootView;
    private EditText nameView;
    private EditText phoneView;
    private EditText addressView;
    private String provinceStr = "上海市普陀区";
    private MongoPeople.Receiver receiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_s11_receipt,container,false);
        receiver = (MongoPeople.Receiver) getActivity().getIntent().getExtras().get(S11NewTradeActivity.INPUT_RECEIVER_ENTITY);
        init();
        return rootView;
    }

    private void init() {
        nameView = (EditText) rootView.findViewById(R.id.s11_receipt_name);
        phoneView = (EditText) rootView.findViewById(R.id.s11_receipt_phone);
        addressView = (EditText) rootView.findViewById(R.id.s11_receipt_address);

        rootView.findViewById(R.id.s11_receipt_manage).setOnClickListener(this);

    }

    public MongoPeople.Receiver getReceiver(){

        if (null == receiver) {
            MongoPeople people = new MongoPeople();
            receiver = people.new Receiver();
        }

        receiver.name = nameView.getText().toString();
        receiver.phone = phoneView.getText().toString();
        receiver.address = addressView.getText().toString();
        receiver.province = provinceStr;

        return receiver;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.s11_receipt_manage:
                startActivity(new Intent(getActivity(), U10AddressListActivity.class));
                break;
        }
    }
}

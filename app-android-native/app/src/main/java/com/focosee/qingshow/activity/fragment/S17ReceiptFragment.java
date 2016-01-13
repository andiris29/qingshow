package com.focosee.qingshow.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.CityActivity;
import com.focosee.qingshow.activity.CityEvent;
import com.focosee.qingshow.activity.U10AddressListActivity;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/3/11.
 */
public class S17ReceiptFragment extends Fragment implements View.OnClickListener{

    private View rootView;
    private EditText nameView;
    private EditText phoneView;
    private EditText addressView;
    private TextView provinceView;
    private LinearLayout provinceLayout;

    private String provinceStr = "上海市普陀区";
    private MongoPeople.Receiver receiver;
    public static final String TO_U10 = "TO_U10";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        rootView = inflater.inflate(R.layout.fragment_s17_receipt,container,false);
        init();
        return rootView;
    }

    private void init() {
        nameView = (EditText) rootView.findViewById(R.id.s11_receipt_name);
        phoneView = (EditText) rootView.findViewById(R.id.s11_receipt_phone);
        addressView = (EditText) rootView.findViewById(R.id.s11_receipt_address);
        provinceView = (TextView) rootView.findViewById(R.id.s11_receipt_province);
        provinceView.setOnClickListener(this);
        provinceLayout = (LinearLayout) rootView.findViewById(R.id.s11_receipt_province_layout);

        rootView.findViewById(R.id.s11_receipt_manage).setOnClickListener(this);
        provinceLayout.setOnClickListener(this);
        initDefaultReceiver();

    }

    private void initDefaultReceiver(){
        if(null != QSModel.INSTANCE.getUser()){
            MongoPeople.Receiver defaultReceiver = null;
            for (MongoPeople.Receiver receiver : QSModel.INSTANCE.getUser().receivers) {
                if(receiver.isDefault){
                    defaultReceiver = receiver;
                }
            }
            if (null != defaultReceiver){
                nameView.setText(defaultReceiver.name);
                phoneView.setText(defaultReceiver.phone);
                addressView.setText(defaultReceiver.address);
                provinceView.setText(defaultReceiver.province);
                provinceStr = defaultReceiver.province;
            }
        }
    }

    public MongoPeople.Receiver getReceiver(){

        if (null == receiver) {
            MongoPeople people = new MongoPeople();
            receiver = people.new Receiver();
        }
        if (nameView.getText() != null && !nameView.getText().toString().isEmpty()) receiver.name = nameView.getText().toString();
        if (phoneView.getText() != null && !phoneView.getText().toString().isEmpty()) receiver.phone = phoneView.getText().toString();
        if (addressView.getText() != null && !addressView.getText().toString().isEmpty()) receiver.address = addressView.getText().toString();
        if(!TextUtils.isEmpty(provinceStr)) receiver.province = provinceStr;

        return receiver;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.s11_receipt_manage:
                if(!QSModel.INSTANCE.loggedin()){
                    return;
                }
                Intent intent = new Intent(getActivity(), U10AddressListActivity.class);
                intent.putExtra(TO_U10,TO_U10);
                startActivity(intent);
                break;
            case R.id.s11_receipt_province:
                getActivity().startActivity(new Intent(getActivity(),CityActivity.class));
                break;
        }
    }

    public void onEventMainThread(CityEvent event){
        if(null == event)return;
        provinceView.setText(event.address);
    }

    public void onEventMainThread(MongoPeople.Receiver receiver){
        this.receiver = receiver;
        nameView.setText(receiver.name);
        phoneView.setText(receiver.phone);
        addressView.setText(receiver.address);
        provinceStr = receiver.province;
        provinceView.setText(provinceStr);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }
}

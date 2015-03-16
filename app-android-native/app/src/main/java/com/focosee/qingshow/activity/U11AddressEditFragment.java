package com.focosee.qingshow.activity;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.widget.CityPicker;

/**
 * Created by 华榕 on 2015/3/11.
 */
public class U11AddressEditFragment extends Fragment implements View.OnFocusChangeListener {

    enum ViewName{
        AREA
    }

    private TextView titleTV;
    private EditText consigeeNameET;
    private EditText consigeePhoneET;
    private TextView consigeeAreaTV;
    private EditText consigeeDetailAreaET;
    private CityPicker cityPicker;
    private LinearLayout area_layout;
    private MongoPeople people;
    private String id = null;

    public static U11AddressEditFragment newInstace(){
        return new U11AddressEditFragment();
    }

    public U11AddressEditFragment(){}


    @Override
    public void onCreate(Bundle savedInstanceState) {

        people = QSModel.INSTANCE.getUser();

        super.onCreate(savedInstanceState);

        if(null != getArguments()){
            id = getArguments().getString("id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_u11_edit_create, container, false);
        matchUI(view);
        return view;
    }

    private void matchUI(View view){

        view.findViewById(R.id.U11ec_back_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        if(null == id){
            ((TextView) view.findViewById(R.id.U11_title_tv)).setText(getResources().getString(R.string.title_name_activity_addaddress));
        } else {
            ((TextView) view.findViewById(R.id.U11_title_tv)).setText(getResources().getString(R.string.title_name_activity_editaddress));
        }
        consigeeNameET = (EditText) view.findViewById(R.id.consigee_name_editText);
        consigeePhoneET = (EditText) view.findViewById(R.id.consigee_phoneNum_editText);
        consigeeAreaTV = (TextView) view.findViewById(R.id.U11_area_tv);
        consigeeDetailAreaET = (EditText) view.findViewById(R.id.consigee_detailArea_editText);
        area_layout = (LinearLayout) view.findViewById(R.id.consignee_area_layout);
        cityPicker = (CityPicker) view.findViewById(R.id.U11_citypicker);

        consigeeNameET.setOnFocusChangeListener(this);
        consigeePhoneET.setOnFocusChangeListener(this);
        consigeeDetailAreaET.setOnFocusChangeListener(this);
        area_layout.setOnFocusChangeListener(this);
        area_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityPicker.setVisibility(View.VISIBLE);
                consigeeAreaTV.setTextColor(getResources().getColor(R.color.pink));
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(consigeeNameET.getWindowToken(), 0); //强制隐藏键盘
                imm.hideSoftInputFromWindow(consigeePhoneET.getWindowToken(), 0); //强制隐藏键盘
                imm.hideSoftInputFromWindow(consigeeDetailAreaET.getWindowToken(), 0); //强制隐藏键盘
            }
        });


        area_layout.setTag(ViewName.AREA);

        cityPicker.setOnSelectingListener(new CityPicker.OnSelectingListener() {
            @Override
            public void selected(boolean selected) {
                consigeeAreaTV.setText(cityPicker.getCity_string());
                consigeeAreaTV.setTag(cityPicker.getCity_code_string());
            }
        });

        cityPicker.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(!(ViewName.AREA == v.getTag() && hasFocus)){
            cityPicker.setVisibility(View.INVISIBLE);
            consigeeAreaTV.setTextColor(getResources().getColor(R.color.black));
        }
    }
}

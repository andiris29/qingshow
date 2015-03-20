package com.focosee.qingshow.activity.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.focosee.qingshow.R;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.command.UserReceiverCommand;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.widget.CityPicker;
import com.focosee.qingshow.widget.DialogCityPicker;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 华榕 on 2015/3/11.
 */
public class U11AddressEditFragment extends Fragment implements View.OnFocusChangeListener, TextView.OnEditorActionListener {

    private final String NAME_STR = "name";
    private final String PHONE_STR = "phone";
    private final String PROVINCE_STR = "province";
    private final String ADDRESS_STR = "address";

    public static final String ASK_REFRESH = "ask_refresh";

    enum ViewName{
        AREA
    }

    private final String MPREFERENCES = "mPreferences";

    private SharedPreferences preferences;
    private EditText consigeeNameET;
    private EditText consigeePhoneET;
    private TextView consigeeAreaTV;
    private EditText consigeeDetailAreaET;
    private CityPicker cityPicker;
    private LinearLayout area_layout;
    private MongoPeople people;
    private String id = null;
    private boolean isSaved = false;

    private MongoPeople.Receiver receiver;

    public static U11AddressEditFragment newInstace(){
        return new U11AddressEditFragment();
    }

    public U11AddressEditFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        people = QSModel.INSTANCE.getUser();

        receiver = (MongoPeople.Receiver) getActivity().getIntent().getSerializableExtra("receiver");

        preferences = getActivity().getSharedPreferences(MPREFERENCES,Context.MODE_PRIVATE);

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

        view.findViewById(R.id.U11ec_save_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityPicker.setVisibility(View.GONE);
                commitForm();
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
//                final DialogCityPicker dialogCityPicker = new DialogCityPicker(getActivity());
//
//                dialogCityPicker.setOnClickListener(new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        consigeeAreaTV.setText(dialogCityPicker.getValue());
//
//                    }
//                });
//                consigeeAreaTV.setTextColor(getResources().getColor(R.color.pink));
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(consigeeNameET.getWindowToken(), 0); //强制隐藏键盘
                imm.hideSoftInputFromWindow(consigeePhoneET.getWindowToken(), 0); //强制隐藏键盘
                imm.hideSoftInputFromWindow(consigeeDetailAreaET.getWindowToken(), 0); //强制隐藏键盘
//                dialogCityPicker.show();
            }
        });


        area_layout.setTag(ViewName.AREA);

        cityPicker.setOnSelectingListener(new CityPicker.OnSelectingListener() {
            @Override
            public void selected(boolean selected) {
                consigeeAreaTV.setText(cityPicker.getCity_string());
                consigeeAreaTV.setTag(cityPicker.getCity_string());
            }
        });

        cityPicker.setVisibility(View.INVISIBLE);

            setData();
    }

    private void setData(){

        if(null != receiver){//编辑页面
            consigeeNameET.setText(receiver.name);
            consigeePhoneET.setText(receiver.phone);
            if(!"".equals(receiver.province) && null != receiver.province){
                consigeeAreaTV.setText(receiver.province);
            }
            consigeeDetailAreaET.setText(receiver.address);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(NAME_STR, consigeeNameET.getText().toString());
            editor.putString(PHONE_STR, consigeePhoneET.getText().toString());
            editor.putString(PROVINCE_STR, consigeeAreaTV.getText().toString());
            editor.putString(ADDRESS_STR, consigeeDetailAreaET.getText().toString());
            return;
        }

        if(preferences.contains(NAME_STR))
            consigeeNameET.setText(preferences.getString(NAME_STR,""));
        if(preferences.contains(PHONE_STR))
            consigeePhoneET.setText(preferences.getString(PHONE_STR, ""));
        if(preferences.contains(PROVINCE_STR)) {
            consigeeAreaTV.setText(preferences.getString(PROVINCE_STR, ""));
        }
//        if(preferences.contains("province_code"))
//            consigeeAreaTV.setTag(preferences.getString("province_code",""));
        if(preferences.contains(ADDRESS_STR))
            consigeeDetailAreaET.setText(preferences.getString(ADDRESS_STR,""));
    }

    private void commitForm(){

        Map<String, String> params = new HashMap<String, String>();
        if(null != receiver){//编辑页面
            params.put("uuid", receiver.uuid);
        }
        if(null != consigeeNameET.getText() && !"".equals(consigeeNameET.getText().toString()))
            params.put(NAME_STR, consigeeNameET.getText().toString());
        if(null != consigeePhoneET.getText() && !"".equals(consigeePhoneET.getText().toString()))
            params.put(PHONE_STR, consigeePhoneET.getText().toString());
        if(null != consigeeAreaTV.getTag())
            params.put(PROVINCE_STR, consigeeAreaTV.getTag().toString());
        if(null != consigeeDetailAreaET.getText() && !"".equals(consigeeDetailAreaET.getText().toString()))
            params.put(ADDRESS_STR, consigeeDetailAreaET.getText().toString());
        UserReceiverCommand.saveReceiver(params, new Callback() {
            @Override
            public void onError(int errorCode) {
                ErrorHandler.handle(getActivity(), errorCode);
            }

            @Override
            public void onComplete(JSONObject response) {
                super.onComplete(response);
                UserCommand.refresh(new Callback(){
                    @Override
                    public void onComplete() {
                        getActivity().sendBroadcast(new Intent(ASK_REFRESH));
                    }
                });
                Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
                isSaved = true;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = preferences.edit();
        //name
        if(null != consigeeNameET.getText() && !"".equals(consigeeNameET.getText()) || !preferences.contains(NAME_STR)) {
            editor.putString(NAME_STR, consigeeNameET.getText().toString());
            isSaved = false;
        }
        if(preferences.contains(NAME_STR)){
            if(!preferences.getString(NAME_STR, "").equals(consigeeNameET.getText().toString())){
                editor.putString(NAME_STR, consigeeNameET.getText().toString());
                isSaved = false;
            }
        }
        //phone
        if(null != consigeePhoneET.getText() && !"".equals(consigeePhoneET.getText()) || !preferences.contains(PHONE_STR)) {
            editor.putString(PHONE_STR, consigeePhoneET.getText().toString());
            isSaved = false;
        }
        if(preferences.contains(PHONE_STR)){
            if(!preferences.getString(PHONE_STR, "").equals(consigeePhoneET.getText().toString())){
                editor.putString(PHONE_STR, consigeePhoneET.getText().toString());
                isSaved = false;
            }
        }
        //province
        if(null != consigeeAreaTV.getText() && !"".equals(consigeeAreaTV.getText()) || !preferences.contains(PROVINCE_STR)) {
            editor.putString(PROVINCE_STR, consigeeAreaTV.getText().toString());
            isSaved = false;
        }
        if(preferences.contains(PROVINCE_STR)){
            if(!preferences.getString(PROVINCE_STR, "").equals(consigeeAreaTV.getText().toString())){
                editor.putString(PROVINCE_STR, consigeeAreaTV.getText().toString());
                isSaved = false;
            }
        }
        //address
        if(null != consigeeDetailAreaET.getText() && !"".equals(consigeeDetailAreaET.getText()) || !preferences.contains(ADDRESS_STR)) {
            editor.putString(ADDRESS_STR, consigeeDetailAreaET.getText().toString());
            isSaved = false;
        }
        if(preferences.contains(ADDRESS_STR)){
            if(!preferences.getString(ADDRESS_STR, "").equals(consigeeDetailAreaET.getText().toString())){
                editor.putString(ADDRESS_STR, consigeeDetailAreaET.getText().toString());
                isSaved = false;
            }
        }
        if(isSaved){
            editor.clear();
        }
        editor.commit();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(!(ViewName.AREA == v.getTag() && hasFocus)){
//            cityPicker.setVisibility(View.INVISIBLE);

//            consigeeAreaTV.setTextColor(getResources().getColor(R.color.p01_text_color_model_height));
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        return false;
    }
}

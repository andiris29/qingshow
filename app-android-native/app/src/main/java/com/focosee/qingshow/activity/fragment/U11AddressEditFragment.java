package com.focosee.qingshow.activity.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.widget.CityPicker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by 华榕 on 2015/3/11.
 */
public class U11AddressEditFragment extends Fragment implements View.OnFocusChangeListener {

    private final String NAME_STR = "name";
    private final String PHONE_STR = "phone";
    private final String PROVINCE_STR = "province";
    private final String ADDRESS_STR = "address";

    public static final String ASK_REFRESH = "ask_refresh";

    enum ViewName{
        AREA
    }

//    private final String MPREFERENCES = "mPreferences";

//    private SharedPreferences preferences;
    private EditText consigeeNameET;
    private EditText consigeePhoneET;
    private TextView consigeeAreaTV;
    private EditText consigeeDetailAreaET;
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

//        preferences = getActivity().getSharedPreferences(MPREFERENCES,Context.MODE_PRIVATE);

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

        consigeeNameET.setOnFocusChangeListener(this);
        consigeePhoneET.setOnFocusChangeListener(this);
        consigeeDetailAreaET.setOnFocusChangeListener(this);
        area_layout.setOnFocusChangeListener(this);
        area_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CityPickerFragment cityPickerFragment = new CityPickerFragment();
                cityPickerFragment.setOnSelectedListener(new CityPicker.OnSelectingListener() {
                    @Override
                    public void selected(boolean selected) {
                        consigeeAreaTV.setText(cityPickerFragment.getValue());
                        consigeeAreaTV.setTag(cityPickerFragment.getValue());
                    }
                });
                cityPickerFragment.show(getFragmentManager(), "U11", getActivity());
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(consigeeNameET.getWindowToken(), 0); //强制隐藏键盘
                imm.hideSoftInputFromWindow(consigeePhoneET.getWindowToken(), 0); //强制隐藏键盘
                imm.hideSoftInputFromWindow(consigeeDetailAreaET.getWindowToken(), 0); //强制隐藏键盘
            }
        });


        area_layout.setTag(ViewName.AREA);

        setData();
    }

    private void setData(){

        if(null != receiver) {//编辑页面
            consigeeNameET.setText(receiver.name);
            consigeePhoneET.setText(receiver.phone);
            if (!"".equals(receiver.province) && null != receiver.province) {
                consigeeAreaTV.setTag(receiver.province);
                consigeeAreaTV.setText(receiver.province);
            }
            consigeeDetailAreaET.setText(receiver.address);
        }
    }

    private void commitForm(){

        final Map<String, String> params = new HashMap<String, String>();
        if(null != receiver){//编辑页面
            params.put("uuid", receiver.uuid);
        }
        if(null != consigeeNameET.getText() && !"".equals(consigeeNameET.getText().toString()))
            params.put(NAME_STR, consigeeNameET.getText().toString());
        else {
            Toast.makeText(getActivity(), "请填写收货人姓名", Toast.LENGTH_SHORT).show();
            return;
        }
        if(consigeePhoneET.getText().length() == 11)
            params.put(PHONE_STR, consigeePhoneET.getText().toString());
        else {
            Toast.makeText(getActivity(), "请正确填写收货人电话", Toast.LENGTH_SHORT).show();
            return;
        }
        if(null != consigeeAreaTV.getTag())
            params.put(PROVINCE_STR, consigeeAreaTV.getTag().toString());
        else {
            Toast.makeText(getActivity(), "请选择所在区域", Toast.LENGTH_SHORT).show();
            return;
        }
        if(null != consigeeDetailAreaET.getText() && !"".equals(consigeeDetailAreaET.getText().toString()))
            params.put(ADDRESS_STR, consigeeDetailAreaET.getText().toString());
        else {
            Toast.makeText(getActivity(), "请填写详细地址", Toast.LENGTH_SHORT).show();
            return;
        }
        UserReceiverCommand.saveReceiver(params, new Callback() {
            @Override
            public void onError(int errorCode) {
                ErrorHandler.handle(getActivity(), errorCode);
            }
            @Override
            public void onComplete(JSONObject response) {
                if(MetadataParser.hasError(response)){
                    ErrorHandler.handle(getActivity(), MetadataParser.getError(response));
                    return;
                }

                UserCommand.refresh(new Callback() {
                    @Override
                    public void onComplete() {
                        getActivity().sendBroadcast(new Intent(ASK_REFRESH));
                    }
                });
                Gson gson = new Gson();
                receiver = gson.fromJson(gson.toJson(params), new TypeToken<MongoPeople.Receiver>(){}.getType());
                EventBus.getDefault().post(receiver);
                Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
//        commitForm();

        if(!(ViewName.AREA == v.getTag() && hasFocus)){
//            cityPicker.setVisibility(View.INVISIBLE);

//            consigeeAreaTV.setTextColor(getResources().getColor(R.color.p01_text_color_model_height));
        }
    }
}

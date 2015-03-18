package com.focosee.qingshow.activity;

import android.app.DownloadManager;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.widget.CityPicker;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 华榕 on 2015/3/11.
 */
public class U11AddressEditFragment extends Fragment implements View.OnFocusChangeListener, TextView.OnEditorActionListener {

    private String province_text;

    enum ViewName{
        AREA
    }

    private final String savedInstanceState_key = "receicer";
    private final String MPREFERENCES = "mPreferences";

    private SharedPreferences preferences;
    private TextView titleTV;
    private EditText consigeeNameET;
    private EditText consigeePhoneET;
    private TextView consigeeAreaTV;
    private EditText consigeeDetailAreaET;
    private CityPicker cityPicker;
    private LinearLayout area_layout;
    private MongoPeople people;
    private String id = null;
    private boolean isSaved = false;

    public static U11AddressEditFragment newInstace(){
        return new U11AddressEditFragment();
    }

    public U11AddressEditFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        people = QSModel.INSTANCE.getUser();

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
                consigeeAreaTV.setTag(cityPicker.getCity_string());
            }
        });

        cityPicker.setVisibility(View.INVISIBLE);

            setData();
    }

    private void setData(){
        if(preferences.contains("name"))
            consigeeNameET.setText(preferences.getString("name",""));
        if(preferences.contains("phone"))
            consigeePhoneET.setText(preferences.getString("phone", ""));
        if(preferences.contains("province_text")) {
            consigeeAreaTV.setText(preferences.getString("province_text", ""));
            consigeeAreaTV.setTextColor(getResources().getColor(R.color.pink));
        }
//        if(preferences.contains("province_code"))
//            consigeeAreaTV.setTag(preferences.getString("province_code",""));
        if(preferences.contains("address"))
            consigeeDetailAreaET.setText(preferences.getString("address",""));
    }

    private void commitForm(){

        Map<String, String> params = new HashMap<String, String>();
        if(null != consigeeNameET.getText() && !"".equals(consigeeNameET.getText().toString()))
            params.put("name", consigeeNameET.getText().toString());
        if(null != consigeePhoneET.getText() && !"".equals(consigeePhoneET.getText().toString()))
            params.put("phone", consigeePhoneET.getText().toString());
        if(null != consigeeAreaTV.getTag())
            params.put("province", consigeeAreaTV.getTag().toString());
        if(null != consigeeDetailAreaET.getText() && !"".equals(consigeeDetailAreaET.getText().toString()))
            params.put("address", consigeeDetailAreaET.getText().toString());
        System.out.println("userSaveReceiver:"+QSAppWebAPI.getUserSaveReceiverApi());
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getUserSaveReceiverApi(), new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(MetadataParser.hasError(response)){
                    Toast.makeText(getActivity(), "保存失败，请重试", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
                UserCommand.refresh();
                isSaved = true;
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = preferences.edit();

        if(null != consigeeNameET.getText() && !"".equals(consigeeNameET.getText()) || !preferences.contains("name")) {
            editor.putString("name", consigeeNameET.getText().toString());
            isSaved = false;
        }
        if(preferences.contains("name")){
            if(!preferences.getString("name", "").equals(consigeeNameET.getText().toString())){
                editor.putString("name", consigeeNameET.getText().toString());
                isSaved = false;
            }
        }

        if(null != consigeePhoneET.getText() && !"".equals(consigeePhoneET.getText()) || !preferences.contains("phone")) {
            editor.putString("phone", consigeePhoneET.getText().toString());
            isSaved = false;
        }
        if(preferences.contains("phone")){
            if(!preferences.getString("phone", "").equals(consigeePhoneET.getText().toString())){
                editor.putString("phone", consigeePhoneET.getText().toString());
                isSaved = false;
            }
        }

        if(null != consigeeAreaTV.getText() && !"".equals(consigeeAreaTV.getText()) || !preferences.contains("province_text")) {
            editor.putString("province_text", consigeeAreaTV.getText().toString());
            isSaved = false;
        }
        if(preferences.contains("province_text")){
            if(!preferences.getString("province_text", "").equals(consigeePhoneET.getText().toString())){
                editor.putString("province_text", consigeePhoneET.getText().toString());
                isSaved = false;
            }
        }

        if(null != consigeeDetailAreaET.getText() && !"".equals(consigeeDetailAreaET.getText()) || !preferences.contains("address")) {
            editor.putString("address", consigeeDetailAreaET.getText().toString());
            isSaved = false;
        }
        if(preferences.contains("address")){
            if(!preferences.getString("address", "").equals(consigeeDetailAreaET.getText().toString())){
                editor.putString("address", consigeeDetailAreaET.getText().toString());
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
            cityPicker.setVisibility(View.INVISIBLE);
            consigeeAreaTV.setTextColor(getResources().getColor(R.color.black));
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        return false;
    }
}

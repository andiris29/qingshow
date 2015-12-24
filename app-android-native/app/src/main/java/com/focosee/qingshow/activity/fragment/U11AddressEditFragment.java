package com.focosee.qingshow.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.CityActivity;
import com.focosee.qingshow.activity.CityEvent;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.command.UserReceiverCommand;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.util.ToastUtil;
import com.focosee.qingshow.util.ValueUtil;
import com.focosee.qingshow.util.VerificationHelper;
import com.focosee.qingshow.widget.QSButton;
import com.focosee.qingshow.widget.QSEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
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

    enum ViewName {
        AREA
    }

    private EditText consigeeNameET;
    private EditText consigeePhoneET;
    private TextView consigeeAreaTV;
    private EditText consigeeDetailAreaET;
    private LinearLayout area_layout;
    private MongoPeople people;
    private String id = null;
    private boolean isSaved = false;
    private TextView errorText;

    private MongoPeople.Receiver receiver;
    private QSButton verificationBtn;
    private QSButton saveBtn;
    private LinearLayout verification_layout;

    public static U11AddressEditFragment newInstace() {
        return new U11AddressEditFragment();
    }

    public U11AddressEditFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        people = QSModel.INSTANCE.getUser();

        receiver = (MongoPeople.Receiver) getActivity().getIntent().getSerializableExtra("receiver");

        if (null != getArguments()) {
            id = getArguments().getString("id");
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_u11_edit_create, container, false);
        matchUI(view);

        return view;
    }

    private void matchUI(View view) {

        view.findViewById(R.id.U11ec_back_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        saveBtn = (QSButton) view.findViewById(R.id.U11ec_save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBtn.setEnabled(false);
                commitForm();
            }
        });

        if (null == receiver) {
            ((TextView) view.findViewById(R.id.U11_title_tv)).setText(getResources().getString(R.string.title_name_activity_addaddress));
        } else {
            ((TextView) view.findViewById(R.id.U11_title_tv)).setText(getResources().getString(R.string.title_name_activity_editaddress));
        }
        consigeeNameET = (EditText) view.findViewById(R.id.consigee_name_editText);
        consigeePhoneET = (EditText) view.findViewById(R.id.consigee_phoneNum_editText);
        consigeeAreaTV = (TextView) view.findViewById(R.id.U11_area_tv);
        consigeeDetailAreaET = (EditText) view.findViewById(R.id.consigee_detailArea_editText);
        area_layout = (LinearLayout) view.findViewById(R.id.consignee_area_layout);
        errorText = (TextView) view.findViewById(R.id.error_text);
        verificationBtn = (QSButton) view.findViewById(R.id.verification_code_btn);

        consigeeNameET.setOnFocusChangeListener(this);
        consigeePhoneET.setOnFocusChangeListener(this);
        consigeeDetailAreaET.setOnFocusChangeListener(this);
        area_layout.setOnFocusChangeListener(this);
        area_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CityActivity.class));
            }
        });

        area_layout.setTag(ViewName.AREA);

        setData();
    }

    private void showError(String msg) {
        errorText.setVisibility(View.VISIBLE);
        errorText.setText(msg);
        errorText.postDelayed(new Runnable() {
            @Override
            public void run() {
                errorText.setVisibility(View.INVISIBLE);
            }
        }, ValueUtil.SHOW_ERROR_TIME);
    }

    private void setData() {

        if (null != receiver) {//编辑页面
            consigeeNameET.setText(receiver.name);
            consigeePhoneET.setText(receiver.phone);
            consigeeAreaTV.setText(receiver.province);
            consigeeDetailAreaET.setText(receiver.address);
        }
    }

    private void commitForm() {

        final Map params = new HashMap();
        if (null != receiver) {//编辑页面
            params.put("uuid", receiver.uuid);
            params.put("isDefault", receiver.isDefault);
        }
        if (null != consigeeNameET.getText() && !"".equals(consigeeNameET.getText().toString()) && consigeeNameET.getText().toString().length() <= 20)
            params.put(NAME_STR, consigeeNameET.getText().toString());
        else {
            showError("请正确填写收货人姓名，长度应小于20位");
            saveBtn.setEnabled(true);
            return;
        }
        if (consigeePhoneET.getText().length() == 11)
            params.put(PHONE_STR, consigeePhoneET.getText().toString());
        else {
            showError("请正确填写手机号码");
            saveBtn.setEnabled(true);
            return;
        }
        if (null != consigeeAreaTV.getText())
            params.put(PROVINCE_STR, consigeeAreaTV.getText().toString());
        else {
            showError("请选择所在区域");
            saveBtn.setEnabled(true);
            return;
        }
        if (null != consigeeDetailAreaET.getText() && !"".equals(consigeeDetailAreaET.getText().toString())
                && consigeeDetailAreaET.getText().toString().length() <= 50)
            params.put(ADDRESS_STR, consigeeDetailAreaET.getText().toString());
        else {
            showError("请正确填写详细地址，长度应小于50");
            saveBtn.setEnabled(true);
            return;
        }
        commit(params);
    }

    private void commit(final Map params) {
        UserReceiverCommand.saveReceiver(params, new Callback() {
            @Override
            public void onError(int errorCode) {
                ErrorHandler.handle(getActivity(), errorCode);
            }

            @Override
            public void onComplete(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(getActivity(), MetadataParser.getError(response));
                    return;
                }

                UserCommand.refresh(new Callback());
                Gson gson = new Gson();
                receiver = gson.fromJson(gson.toJson(params), new TypeToken<MongoPeople.Receiver>() {
                }.getType());
                EventBus.getDefault().post(receiver);
                saveBtn.setEnabled(true);
                getActivity().finish();
            }
        });
    }

    public void onEventMainThread(CityEvent event) {
        if (null == event) return;
        consigeeAreaTV.setText(event.address);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!(ViewName.AREA == v.getTag() && hasFocus)) {
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("U11AddressEditFragment"); //统计页面
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("U11AddressEditFragment");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

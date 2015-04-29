package com.focosee.qingshow.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.R;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSStringRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.UserParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

public class U07RegisterActivity extends BaseActivity {

    private static final String DEBUG_TAG = "注册页";
    private int shoeSizes[] = {34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};
    private RequestQueue requestQueue;

    private Button submitButton;
    private EditText accountEditText;
    private EditText passwordEditText;
    private EditText confirmEditText;
    private RadioGroup sexRadioGroup;
    private RadioGroup clothesSizeRadioGroup;
    private RadioGroup shoesSizeRadioGroup;

    private Context context;

    private Gson gson;

    String rawCookie = "";

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(U06LoginActivity.LOGIN_SUCCESS.equals(intent.getAction())){
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        context = getApplicationContext();

        submitButton = (Button) findViewById(R.id.submitButton);
        accountEditText = (EditText) findViewById(R.id.accountEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        confirmEditText = (EditText) findViewById(R.id.confirmEditText);
//        sexRadioGroup = (RadioGroup) findViewById(R.id.sexRadioGroup);
//        clothesSizeRadioGroup = (RadioGroup) findViewById(R.id.clothesSizeRadioGroup);
//        shoesSizeRadioGroup = (RadioGroup) findViewById(R.id.shoesSizeRadioGroup);

        ImageView backImageView = (ImageView) findViewById(R.id.backImageView);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ((TextView) findViewById(R.id.register_login_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(U07RegisterActivity.this, U06LoginActivity.class));
            }
        });

        requestQueue = RequestQueueManager.INSTANCE.getQueue();

        clothesSizeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                setClothesSizeRadioGroupListener();
            }
        });

        shoesSizeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                setShoesSizeRadioGroupListener();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!passwordEditText.getText().toString().equals(confirmEditText.getText().toString())) {
                    Toast.makeText(context, "请确认两次密码是否一致", Toast.LENGTH_LONG).show();
                } else {
                    QSStringRequest stringRequest = new QSStringRequest(Request.Method.POST, QSAppWebAPI.REGISTER_SERVICE_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            MongoPeople user = UserParser.parseRegister(response);
                            if (user == null) {
                                ErrorHandler.handle(context, MetadataParser.getError(response));
                            } else {
                                QSModel.INSTANCE.setUser(user);
                                updateSettings();
                                Toast.makeText(context, "注册成功", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(U07RegisterActivity.this, U01UserActivity.class));
                                finish();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, "请重新尝试", Toast.LENGTH_LONG).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("id", accountEditText.getText().toString());
                            map.put("password", passwordEditText.getText().toString());

                            return map;
                        }
                    };
                    requestQueue.add(stringRequest);
                }
            }
        });
        registerReceiver(receiver, new IntentFilter(U06LoginActivity.LOGIN_SUCCESS));
    }

    @Override
    public void reconn() {

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private int getSexRadioButtonVal() {
        int whichChecked = sexRadioGroup.getCheckedRadioButtonId();
        int result = -1;
        switch (whichChecked) {
//            case R.id.femaleRadioButton:
//                result = 0;
//                break;
            case R.id.maleButton:
                result = 1;
                break;
            default:
                break;
        }
        return result;
    }

    private int getClothesSizeRadioButtonVal() {
        int whichChecked = clothesSizeRadioGroup.getCheckedRadioButtonId();
        int result = -1;
//        switch (whichChecked) {
//            case R.id.xxsSizeRadioButton:
//                result = 0;
//                break;
//            case R.id.xsSizeRadioButton:
//                result = 1;
//                break;
//            case R.id.sSizeRadioButton:
//                result = 2;
//                break;
//            case R.id.mSizeRadioButton:
//                result = 3;
//                break;
//            case R.id.lSizeRadioButton:
//                result = 4;
//                break;
//            case R.id.xlSizeRadioButton:
//                result = 5;
//                break;
//            case R.id.xxlSizeRadioButton:
//                result = 6;
//                break;
//            case R.id.xxxlSizeRadioButton:
//                result = 7;
//                break;
//            default:
//                break;
//        }
        return result;
    }

    private int getShoesSizeRadioButtonVal() {
        int whichChecked = shoesSizeRadioGroup.getCheckedRadioButtonId();
        int result = -1;
//        switch (whichChecked) {
//            case R.id.size34RadioButton:
//                result = 0;
//                break;
//            case R.id.size35RadioButton:
//                result = 1;
//                break;
//            case R.id.size36RadioButton:
//                result = 2;
//                break;
//            case R.id.size37RadioButton:
//                result = 3;
//                break;
//            case R.id.size38RadioButton:
//                result = 4;
//                break;
//            case R.id.size39RadioButton:
//                result = 5;
//                break;
//            case R.id.size40RadioButton:
//                result = 6;
//                break;
//            case R.id.size41RadioButton:
//                result = 7;
//                break;
//            case R.id.size42RadioButton:
//                result = 8;
//                break;
//            case R.id.size43RadioButton:
//                result = 9;
//                break;
//            case R.id.size44RadioButton:
//                result = 10;
//                break;
//            default:
//                break;
//        }
        return result;
    }

    private void setClothesSizeRadioGroupListener() {
        for (int i = 0; i < clothesSizeRadioGroup.getChildCount(); i++) {
            ((RadioButton) clothesSizeRadioGroup.getChildAt(i)).setTextColor(getResources().getColor(R.color.darker_gray));
        }
        ((RadioButton) clothesSizeRadioGroup.getChildAt(getClothesSizeRadioButtonVal())).setTextColor(getResources().getColor(R.color.white));
    }

    private void setShoesSizeRadioGroupListener() {
        for (int i = 0; i < shoesSizeRadioGroup.getChildCount(); i++) {
            ((RadioButton) shoesSizeRadioGroup.getChildAt(i)).setTextColor(getResources().getColor(R.color.darker_gray));
            getShoesSizeRadioButtonVal();
        }
        ((RadioButton) shoesSizeRadioGroup.getChildAt(getShoesSizeRadioButtonVal())).setTextColor(getResources().getColor(R.color.white));
    }

    private void updateSettings() {
        int gender = getSexRadioButtonVal();
        int clothesSize = getClothesSizeRadioButtonVal();
        int shoesSize = shoeSizes[getShoesSizeRadioButtonVal()];

        Map<String, String> params = new HashMap<String, String>();
        if (gender >= 0)
            params.put("gender", gender + "");
        if (clothesSize >= 0)
            params.put("clothingSize", clothesSize + "");
        if (shoesSize >= 0)
            params.put("shoeSize", shoesSize + "");


        UserCommand.update(params,new Callback(){

            @Override
            public void onError(int errorCode) {
                super.onError(errorCode);
                ErrorHandler.handle(U07RegisterActivity.this,errorCode);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("U07Register"); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("U07Register"); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }

}

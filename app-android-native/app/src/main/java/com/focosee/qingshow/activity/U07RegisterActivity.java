package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.focosee.qingshow.R;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.entity.LoginResponse;
import com.focosee.qingshow.entity.RegisterResponse;
import com.focosee.qingshow.error.ErrorHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class U07RegisterActivity extends Activity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        context = getApplicationContext();

        submitButton = (Button) findViewById(R.id.submitButton);
        accountEditText = (EditText) findViewById(R.id.accountEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        confirmEditText = (EditText) findViewById(R.id.confirmEditText);
        sexRadioGroup = (RadioGroup) findViewById(R.id.sexRadioGroup);
        clothesSizeRadioGroup = (RadioGroup) findViewById(R.id.clothesSizeRadioGroup);
        shoesSizeRadioGroup = (RadioGroup) findViewById(R.id.shoesSizeRadioGroup);

        requestQueue = Volley.newRequestQueue(context);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!passwordEditText.getText().toString().equals(confirmEditText.getText().toString())) {
                    Toast.makeText(context, "请确认两次密码是否一致", Toast.LENGTH_LONG).show();
                } else {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, QSAppWebAPI.REGISTER_SERVICE_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            RegisterResponse registerResponse = new Gson().fromJson(response, new TypeToken<RegisterResponse>() {
                            }.getType());
                            if (registerResponse == null || registerResponse.data == null) {
                                if (registerResponse == null) {
                                    Toast.makeText(context, "请重新尝试", Toast.LENGTH_LONG).show();
                                } else {
                                    ErrorHandler.handle(context, registerResponse.metadata.error);
                                }
                            } else {
                                //QSApplication.get().setPeople(registerResponse.data.people);
                                Toast.makeText(context, "保存成功", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, "请重新尝试", Toast.LENGTH_LONG).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
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
    }

    private int getSexRadioButtonVal() {
        int whichChecked = sexRadioGroup.getCheckedRadioButtonId();

        switch (whichChecked) {
            case R.id.femaleRadioButton:

                break;
            case R.id.maleButton:

                break;
            default:
                break;
        }
        return whichChecked;
    }

    private int getClothesSizeRadioButtonVal() {
        int whichChecked = clothesSizeRadioGroup.getCheckedRadioButtonId();
        switch (whichChecked) {
            case R.id.xxsSizeRadioButton:

                break;
            case R.id.xsSizeRadioButton:

                break;
            case R.id.sSizeRadioButton:

                break;
            case R.id.mSizeRadioButton:

                break;
            case R.id.lSizeRadioButton:

                break;
            case R.id.xlSizeRadioButton:

                break;
            case R.id.xxlSizeRadioButton:

                break;
            case R.id.xxxlSizeRadioButton:

                break;
            default:
                break;
        }
        return whichChecked;
    }

    private int getShoesSizeRadioButtonVal() {
        int whichChecked = shoesSizeRadioGroup.getCheckedRadioButtonId();
        switch (whichChecked) {
            case R.id.size34RadioButton:

                break;
            case R.id.size35RadioButton:

                break;
            case R.id.size36RadioButton:

                break;
            case R.id.size37RadioButton:

                break;
            case R.id.size38RadioButton:

                break;
            case R.id.size39RadioButton:

                break;
            case R.id.size40RadioButton:

                break;
            case R.id.size41RadioButton:

                break;
            case R.id.size42RadioButton:

                break;
            case R.id.size43RadioButton:

                break;
            case R.id.size44RadioButton:

                break;
            default:
                break;
        }
        return whichChecked;
    }

}

package com.focosee.qingshow.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.focosee.qingshow.R;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.entity.mongo.MongoPeople;
import com.focosee.qingshow.httpapi.response.error.ErrorCode;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.UserParser;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class U06LoginActivity extends BaseActivity {
    private EditText accountEditText;
    private EditText passwordEditText;
    private Context context;
    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;

    String rawCookie = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = getApplicationContext();
        sharedPreferences = getSharedPreferences("personal", Context.MODE_PRIVATE);

        TextView registerTextView;
        Button submitButton;

        submitButton = (Button) findViewById(R.id.submitButton);
        accountEditText = (EditText) findViewById(R.id.accountEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        requestQueue = Volley.newRequestQueue(context);

        TextView backTextView = (TextView) findViewById(R.id.backTextView);
        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("id", accountEditText.getText().toString());
                params.put("password", passwordEditText.getText().toString());
                JSONObject jsonObject = new JSONObject(params);

                final ProgressDialog pDialog = new ProgressDialog(U06LoginActivity.this);
                pDialog.setMessage("加载中...");
                pDialog.show();

                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        QSAppWebAPI.LOGIN_SERVICE_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                pDialog.dismiss();

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("id", accountEditText.getText().toString());
                                editor.putString("password", passwordEditText.getText().toString());
                                editor.putString("Cookie", rawCookie);
                                //editor.putString("_id", loginResponse.data.people._id);
                                //editor.putString("connect.sid", rawCookie);
                                editor.commit();

                                MongoPeople user = UserParser.parseLogin(response);
                                if (user == null) {
                                    if (MetadataParser.getError(response) == ErrorCode.IncorrectMailOrPassword) {
                                        Toast.makeText(context, "账号或密码错误", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(context, "请重新尝试", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    QSApplication.get().setPeople(user);
                                    Intent intent = new Intent(U06LoginActivity.this, U01PersonalActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
                    }
                }) {
                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        try {
                            Map<String, String> responseHeaders = response.headers;
                            rawCookie = responseHeaders.get("Set-Cookie").split(";")[0];//.split("=")[1];
                            String dataString = new String(response.data, "UTF-8");
                            return Response.success(dataString, HttpHeaderParser.parseCacheHeaders(response));
                        } catch (UnsupportedEncodingException e) {
                            return Response.error(new ParseError(e));
                        }
                    }

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
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("U06Login"); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("U06Login"); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }
}

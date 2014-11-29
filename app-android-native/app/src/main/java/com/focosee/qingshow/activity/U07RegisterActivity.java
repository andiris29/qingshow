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
import com.focosee.qingshow.config.QSAppWebAPI;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class U07RegisterActivity extends Activity {
    private RequestQueue requestQueue;
    private Button submitButton;
    private EditText accountEditText;
    private EditText passwordEditText;
    private EditText confirmEditText;
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

        requestQueue = Volley.newRequestQueue(context);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!passwordEditText.getText().toString().equals(confirmEditText.getText().toString())) {
                    Toast.makeText(context, "请确认两次密码是否一致", Toast.LENGTH_LONG).show();
                } else {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("id", accountEditText.getText().toString());
                    params.put("password", passwordEditText.getText().toString());
                    JSONObject jsonObject = new JSONObject(params);

                    JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST,
                            QSAppWebAPI.REGISTER_SERVICE_URL, jsonObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("TAG", response.toString());
                                    try {
                                        if (response.getJSONObject("data") == null) {
                                            Log.v("TAG", "error");
                                            String errorCode = response.getJSONObject("metadata").getString("error");
                                            Log.v("TAG", "error" + errorCode);
                                            if (errorCode.equals("1010")) {
                                                Toast.makeText(context, "账号已存在", Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Intent intent = new Intent(U07RegisterActivity.this, U06LoginActivity.class);
                                            startActivity(intent);
                                        }
                                    } catch (Exception e) {
                                        Log.v("TAG", "exception");
                                        try {
                                            Log.v("TAG", "error");
                                            String errorCode = response.getJSONObject("metadata").getString("error");
                                            Log.v("TAG", "error" + errorCode);
                                            if (errorCode.equals("1010")) {
                                                Toast.makeText(context, "账号已存在", Toast.LENGTH_LONG).show();
                                            }
                                        } catch (Exception e1) {
                                            Log.v("TAG", "e1");
                                        }
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("TAG", error.getMessage(), error);
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("id", accountEditText.getText().toString());
                            map.put("password", passwordEditText.getText().toString());
                            return map;
                        }

                        @Override
                        public Map<String, String> getHeaders() {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Accept", "application/json");
                            headers.put("Content-Type", "application/json; charset=UTF-8");
                            return headers;
                        }
                    };

                    requestQueue.add(stringRequest);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
}

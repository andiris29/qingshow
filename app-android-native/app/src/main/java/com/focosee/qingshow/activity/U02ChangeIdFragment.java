package com.focosee.qingshow.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.focosee.qingshow.R;
import com.focosee.qingshow.config.QSAppWebAPI;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class U02ChangeIdFragment extends Fragment {
    private Context context;
    private RequestQueue requestQueue;

    private EditText currentIdEditText;
    private EditText newIdEditText;
    private EditText confirmIdEditText;
    private TextView saveTextView;

    public U02ChangeIdFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_u02_change_id, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = (Context) getActivity().getApplicationContext();
        requestQueue = Volley.newRequestQueue(context);

        currentIdEditText = (EditText) getActivity().findViewById(R.id.currentIdEditText);
        newIdEditText = (EditText) getActivity().findViewById(R.id.newIdEditText);
        confirmIdEditText = (EditText) getActivity().findViewById(R.id.confirmIdEditText);

        saveTextView = (TextView) getActivity().findViewById(R.id.saveTextView);
        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("id", currentIdEditText.getText().toString());
                params.put("newId", newIdEditText.getText().toString());
                JSONObject jsonObject = new JSONObject(params);
                JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST,
                        QSAppWebAPI.LOGIN_SERVICE_URL, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.v("TAG", response.toString());
                                try {
                                    if (response.getJSONObject("data") == null) {
                                        Log.v("TAG", "error");
                                        String errorCode = response.getJSONObject("metadata").getString("error");
                                        Log.v("TAG", "error" + errorCode);
                                        if (errorCode.equals("1001")) {
                                            Toast.makeText(context, "账号或密码错误", Toast.LENGTH_LONG).show();
                                        }
                                    } else {

                                    }
                                } catch (Exception e) {
                                    Log.v("TAG", "exception");
                                    try {
                                        Log.v("TAG", "error");
                                        String errorCode = response.getJSONObject("metadata").getString("error");
                                        Log.v("TAG", "error" + errorCode);
                                        if (errorCode.equals("1001")) {
                                            Toast.makeText(context, "账号或密码错误", Toast.LENGTH_LONG).show();
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
                    protected Map<String, String> getParams() {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("id", currentIdEditText.getText().toString());
                        map.put("newId", newIdEditText.getText().toString());
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
        });
    }
}

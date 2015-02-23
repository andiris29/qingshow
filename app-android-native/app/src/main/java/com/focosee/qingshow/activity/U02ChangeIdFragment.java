package com.focosee.qingshow.activity;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.focosee.qingshow.model.QSModel;
import com.umeng.analytics.MobclickAgent;

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
        currentIdEditText.setText(QSModel.INSTANCE.getUser().userInfo.id);

        saveTextView = (TextView) getActivity().findViewById(R.id.saveTextView);
        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!newIdEditText.getText().toString().equals(confirmIdEditText.getText().toString())) {
                    Toast.makeText(context, "请确认两次输入地址是否一致", Toast.LENGTH_LONG).show();
                } else {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("id", QSModel.INSTANCE.getUser().userInfo.id);
                    params.put("newId", newIdEditText.getText().toString());
                    JSONObject jsonObject = new JSONObject(params);
                    JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST,
                            QSAppWebAPI.LOGIN_SERVICE_URL, jsonObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

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
                            map.put("id", QSModel.INSTANCE.getUser().userInfo.id);
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
            }
        });
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("U04ChangeMail"); //统计页面
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("U04ChangeMail");
    }
}

package com.focosee.qingshow.activity;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.focosee.qingshow.R;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.entity.UpdateResponse;
import com.focosee.qingshow.error.ErrorHandler;
import com.focosee.qingshow.request.QXStringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class U02ChangePasswordFragment extends Fragment {
    private Context context;
    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;

    private EditText currentPasswordEditText;
    private EditText newPasswordEditText;
    private EditText confirmPasswordEditText;
    private TextView saveTextView;
    private TextView backTextView;

    public U02ChangePasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_u02_change_password, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = (Context) getActivity().getApplicationContext();
        requestQueue = Volley.newRequestQueue(context);
        sharedPreferences = getActivity().getSharedPreferences("personal", Context.MODE_PRIVATE);

        currentPasswordEditText = (EditText) getActivity().findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = (EditText) getActivity().findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = (EditText) getActivity().findViewById(R.id.confirmPasswordEditText);

        backTextView = (TextView) getActivity().findViewById(R.id.backTextView);
        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U02SettingsFragment settingsFragment = new U02SettingsFragment();
                getFragmentManager().beginTransaction().replace(R.id.settingsScrollView, settingsFragment).commit();
            }
        });

        saveTextView = (TextView) getActivity().findViewById(R.id.saveTextView);
        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!newPasswordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())) {
                    Toast.makeText(context, "请确认两次输入密码是否一致", Toast.LENGTH_LONG).show();
                } else {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("currentPassword", newPasswordEditText.getText().toString());
                    params.put("password", sharedPreferences.getString("password", ""));

                    QXStringRequest qxStringRequest = new QXStringRequest(params, Request.Method.POST, QSAppWebAPI.UPDATE_SERVICE_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            UpdateResponse updateResponse = new Gson().fromJson(response, new TypeToken<UpdateResponse>() {
                            }.getType());

                            if (updateResponse == null || updateResponse.data == null) {
                                if (updateResponse == null) {
                                    Toast.makeText(context, "请重新尝试", Toast.LENGTH_LONG).show();
                                } else {
                                    ErrorHandler.handle(context, updateResponse.metadata.error);
                                }
                            } else {
                                QSApplication.get().setPeople(updateResponse.data.people);
                                Toast.makeText(context, "保存成功", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, "请检查网络", Toast.LENGTH_LONG).show();
                        }
                    });
                    requestQueue.add(qxStringRequest);
                }
            }
        });
    }
}

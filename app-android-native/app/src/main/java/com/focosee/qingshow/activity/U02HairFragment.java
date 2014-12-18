package com.focosee.qingshow.activity;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
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
import com.focosee.qingshow.config.QSAppWebAPI;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class U02HairFragment extends Fragment {
    private Context context;
    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;

    private TextView backTextView;
    private TextView saveTextView;

    private CheckBox allCheckBox;
    private CheckBox middleCheckBox;
    private CheckBox longCheckBox;
    private CheckBox extremeCheckBox;

    private RelativeLayout allRelativeLayout;
    private RelativeLayout middleRelativeLayout;
    private RelativeLayout longRelativeLayout;
    private RelativeLayout extremeRelativeLayout;

    public U02HairFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_u02_hair, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = (Context) getActivity().getApplicationContext();
        requestQueue = Volley.newRequestQueue(context);
        sharedPreferences = getActivity().getSharedPreferences("personal", Context.MODE_PRIVATE);

        backTextView = (TextView) getActivity().findViewById(R.id.backTextView);
        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U02SettingsFragment settingsFragment = new U02SettingsFragment();
                getFragmentManager().beginTransaction().replace(R.id.settingsScrollView, settingsFragment).commit();
            }
        });

        allCheckBox = (CheckBox) getActivity().findViewById(R.id.allCheckBox);
        middleCheckBox = (CheckBox) getActivity().findViewById(R.id.middleCheckBox);
        longCheckBox = (CheckBox) getActivity().findViewById(R.id.longCheckBox);
        extremeCheckBox = (CheckBox) getActivity().findViewById(R.id.extremeCheckBox);

        allRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.allRelativeLayout);
        middleRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.middleRelativeLayout);
        longRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.longRelativeLayout);
        extremeRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.extremeRelativeLayout);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        saveTextView = (TextView) getActivity().findViewById(R.id.saveTextView);
        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("id", sharedPreferences.getString("id", ""));
                String hairTypes = getHairTypes();

                params.put("hairTypes", hairTypes);
                JSONObject jsonObject = new JSONObject(params);

                JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST,
                        QSAppWebAPI.UPDATE_SERVICE_URL, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(context, "保存成功", Toast.LENGTH_LONG).show();

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
                        map.put("id", sharedPreferences.getString("id", ""));
                        return map;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        String rawCookie = sharedPreferences.getString("Cookie", "");
                        if (rawCookie != null && rawCookie.length() > 0) {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Cookie", rawCookie);
                            headers.put("Accept", "application/json");
                            headers.put("Content-Type", "application/json; charset=UTF-8");
                            return headers;
                        }
                        return super.getHeaders();
                    }
                };
                requestQueue.add(stringRequest);
            }
        });
    }

    private String getHairTypes() {
        String hairTypes = "";

        return hairTypes;
    }
}

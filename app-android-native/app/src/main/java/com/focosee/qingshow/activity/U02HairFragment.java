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
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.config.HairType;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.entity.UpdateResponse;
import com.focosee.qingshow.error.ErrorHandler;
import com.focosee.qingshow.request.QXStringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;

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
    private CheckBox longCheckBox;
    private CheckBox extremeCheckBox;
    private CheckBox middleCheckBox;
    private CheckBox shortCheckBox;
    private CheckBox baldCheckBox;
    private CheckBox othersCheckBox;

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
        longCheckBox = (CheckBox) getActivity().findViewById(R.id.longCheckBox);
        extremeCheckBox = (CheckBox) getActivity().findViewById(R.id.extremeCheckBox);
        middleCheckBox = (CheckBox) getActivity().findViewById(R.id.middleCheckBox);
        shortCheckBox = (CheckBox) getActivity().findViewById(R.id.shortCheckBox);
        baldCheckBox = (CheckBox) getActivity().findViewById(R.id.baldCheckBox);
        othersCheckBox = (CheckBox) getActivity().findViewById(R.id.othersCheckBox);

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
                String hairTypes = getHairTypes();

                params.put("hairTypes", hairTypes);

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
        });
    }

    private String getHairTypes() {
        String hairTypes = "";
        allCheckBox = (CheckBox) getActivity().findViewById(R.id.allCheckBox);
        longCheckBox = (CheckBox) getActivity().findViewById(R.id.longCheckBox);
        extremeCheckBox = (CheckBox) getActivity().findViewById(R.id.extremeCheckBox);
        middleCheckBox = (CheckBox) getActivity().findViewById(R.id.middleCheckBox);
        shortCheckBox = (CheckBox) getActivity().findViewById(R.id.shortCheckBox);
        baldCheckBox = (CheckBox) getActivity().findViewById(R.id.baldCheckBox);
        othersCheckBox.isChecked();

        if (allCheckBox.isChecked()) {
            hairTypes += HairType.ALL_HAIR + ",";
        }
        if (longCheckBox.isChecked()) {
            hairTypes += HairType.LONG_HAIR + ",";
        }
        if (extremeCheckBox.isChecked()) {
            hairTypes += HairType.EXTREME_LONG_HAIR + ",";
        }
        if (middleCheckBox.isChecked()) {
            hairTypes += HairType.MIDDLE_LONG_HAIR + ",";
        }
        if (shortCheckBox.isChecked()) {
            hairTypes += HairType.SHORT_HAIR + ",";
        }
        if (baldCheckBox.isChecked()) {
            hairTypes += HairType.BALD_HAIR + ",";
        }
        if (othersCheckBox.isChecked()) {
            hairTypes += HairType.OTHERS_HAIR + ",";
        }
        return hairTypes;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MainScreen"); //统计页面
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainScreen");
    }
}

package com.focosee.qingshow.activity;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.focosee.qingshow.R;
import com.focosee.qingshow.config.QSAppWebAPI;

import java.util.HashMap;
import java.util.Map;

public class U02SettingsFragment extends Fragment {
    private Context context;
    private RequestQueue requestQueue;

    private TextView saveTextView;
    private RelativeLayout hairRelativeLayout;
    private RelativeLayout changePasswordRelativeLayout;
    private RelativeLayout changeEmailRelativeLayout;
    private RelativeLayout informRelativeLayout;
    private RelativeLayout rulesRelativeLayout;
    private RelativeLayout helpRelativeLayout;
    private RelativeLayout aboutVIPRelativeLayout;

    public U02SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_u02_settings, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = (Context) getActivity().getApplicationContext();
        requestQueue = Volley.newRequestQueue(context);

        hairRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.hairRelativeLayout);
        hairRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U02HairFragment hairFragment = new U02HairFragment();
                getFragmentManager().beginTransaction().replace(R.id.settingsScrollView, hairFragment).commit();
            }
        });
        changePasswordRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.changePasswordRelativeLayout);
        changePasswordRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U02ChangePasswordFragment fragment = new U02ChangePasswordFragment();
                getFragmentManager().beginTransaction().replace(R.id.settingsScrollView, fragment).commit();
            }
        });
        changeEmailRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.changeEmailRelativeLayout);
        changeEmailRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U02ChangeIdFragment fragment = new U02ChangeIdFragment();
                getFragmentManager().beginTransaction().replace(R.id.settingsScrollView, fragment).commit();
            }
        });
        informRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.informRelativeLayout);
        informRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U02NoticeFragment fragment = new U02NoticeFragment();
                getFragmentManager().beginTransaction().replace(R.id.settingsScrollView, fragment).commit();
            }
        });
        rulesRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.rulesRelativeLayout);
        rulesRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U02TermsFragment hairFragment = new U02TermsFragment();
                getFragmentManager().beginTransaction().replace(R.id.settingsScrollView, hairFragment).commit();
            }
        });
        helpRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.helpRelativeLayout);
        helpRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U02HelpFragment fragment = new U02HelpFragment();
                getFragmentManager().beginTransaction().replace(R.id.settingsScrollView, fragment).commit();
            }
        });
        aboutVIPRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.aboutVIPRelativeLayout);
        aboutVIPRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U02AboutVIPFragment fragment = new U02AboutVIPFragment();
                getFragmentManager().beginTransaction().replace(R.id.settingsScrollView, fragment).commit();
            }
        });

        saveTextView = (TextView) getActivity().findViewById(R.id.saveTextView);
        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        QSAppWebAPI.UPDATE_SERVICE_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("TAG", response);
                                Toast.makeText(context, "success", Toast.LENGTH_LONG).show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "wrong", Toast.LENGTH_LONG).show();
                        Log.e("TAG", error.getMessage(), error);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<String, String>();

                        map.put("id", "");
                        map.put("currentPassword", "");
                        map.put("password", "");
                        map.put("name", "");
                        map.put("portrait", "");
                        map.put("gender", "");
                        map.put("height", "");
                        map.put("weight", "");
                        map.put("roles", "");
                        map.put("hairTypes", "");
                        return map;
                    }
                };

                requestQueue.add(stringRequest);
            }
        });
    }
}

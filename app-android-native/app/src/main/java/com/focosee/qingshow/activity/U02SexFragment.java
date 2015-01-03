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

public class U02SexFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private Context context;
    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;

    private TextView backTextView;
    private TextView saveTextView;

    private Button femaleButton;
    private Button maleButton;

    public static U02SexFragment newInstance(String param1, String param2) {
        U02SexFragment fragment = new U02SexFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public U02SexFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_u02_sex, container, false);
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

        femaleButton = (Button) getActivity().findViewById(R.id.femaleButton);
        maleButton = (Button) getActivity().findViewById(R.id.maleButton);

        if (!sharedPreferences.contains("gender")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("gender", "1");
            editor.commit();
            setGender(1);
        }
        femaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("gender", "1");
                editor.commit();
                setGender(1);
            }
        });
        maleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("gender", "0");
                editor.commit();
                setGender(0);
            }
        });

        saveTextView = (TextView) getActivity().findViewById(R.id.saveTextView);
        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("gender", sharedPreferences.getString("gender", "0"));

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

    private void setGender(int gender) {
        if (gender == 1) {
            femaleButton.setBackgroundResource(R.drawable.btn_choose_focused);
            maleButton.setBackgroundResource(R.drawable.btn_choose_default);
        } else if (gender == 0) {
            femaleButton.setBackgroundResource(R.drawable.btn_choose_default);
            maleButton.setBackgroundResource(R.drawable.btn_choose_focused);
        }
    }
}

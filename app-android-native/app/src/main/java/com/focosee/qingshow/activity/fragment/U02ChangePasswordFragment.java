package com.focosee.qingshow.activity.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.R;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSStringRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.UserParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.U02Model;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.umeng.analytics.MobclickAgent;
import java.util.HashMap;

public class U02ChangePasswordFragment extends Fragment {
    private Context context;
    private RequestQueue requestQueue;

    private TextView errorTextView;
    private EditText currentPasswordEditText;
    private EditText newPasswordEditText;
    private EditText confirmPasswordEditText;
    private Button saveTextView;
    private ImageButton backTextView;

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
        context = getActivity().getApplicationContext();
        requestQueue = RequestQueueManager.INSTANCE.getQueue();

        currentPasswordEditText = (EditText) getActivity().findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = (EditText) getActivity().findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = (EditText) getActivity().findViewById(R.id.confirmPasswordEditText);
        errorTextView = (TextView) getActivity().findViewById(R.id.error_text);

        backTextView = (ImageButton) getActivity().findViewById(R.id.backTextView);
        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U02Model.INSTANCE.set_class(U02SettingsFragment.class);
                U02SettingsFragment settingsFragment;
                if(null == getFragmentManager().findFragmentByTag(U02ChangePasswordFragment.class.getSimpleName()))
                    settingsFragment = new U02SettingsFragment();
                else
                    settingsFragment = (U02SettingsFragment)getFragmentManager().findFragmentByTag(U02SettingsFragment.class.getSimpleName());
                getFragmentManager().beginTransaction().setCustomAnimations(R.anim.push_left_in, 0, 0, 0).
                        replace(R.id.settingsScrollView, settingsFragment).commit();
            }
        });

        saveTextView = (Button) getActivity().findViewById(R.id.saveTextView);
        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(newPasswordEditText.getText().toString()) ||
                        TextUtils.isEmpty(confirmPasswordEditText.getText().toString()) || TextUtils.isEmpty(currentPasswordEditText.getText().toString())) {
                    showError("请输入正确的内容");
                    return;
                }
                if (!newPasswordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())) {
                    showError("请确认两次输入密码是否一致");
                    return;
                }

                if(confirmPasswordEditText.getText().toString().length() > 20){
                    showError("密码长度应小于20位");
                    return;
                }

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("currentPassword", currentPasswordEditText.getText().toString());
                params.put("password", newPasswordEditText.getText().toString());

                QSStringRequest qxStringRequest = new QSStringRequest(params, Request.Method.POST, QSAppWebAPI.UPDATE_SERVICE_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        MongoPeople user = UserParser.parseUpdate(response);
                        if (user == null) {
                            ErrorHandler.handle(context, MetadataParser.getError(response));
                        } else {
                            QSModel.INSTANCE.setUser(user);
                            U02SettingsFragment settingsFragment = new U02SettingsFragment();
                            getFragmentManager().beginTransaction().replace(R.id.settingsScrollView, settingsFragment).commit();
                            Toast.makeText(context, "保存成功", Toast.LENGTH_LONG).show();
                            U02Model.INSTANCE.set_class(U02SettingsFragment.class);
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

    public void showError(String errorMsg){
        errorTextView.setVisibility(View.VISIBLE);
        errorTextView.setText(errorMsg);
        errorTextView.postDelayed(new Runnable() {
            @Override
            public void run() {
                errorTextView.setVisibility(View.GONE);
            }
        }, 5000);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("U02ChangePasswordFragment"); //统计页面
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("U02ChangePasswordFragment");
    }
}

package com.focosee.qingshow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.R;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSStringRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.UserParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.util.ToastUtil;
import com.focosee.qingshow.widget.QSButton;
import com.focosee.qingshow.widget.QSEditText;
import com.focosee.qingshow.widget.QSTextView;
import com.umeng.analytics.MobclickAgent;
import java.util.HashMap;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class U18ResetPasswordStep2Activity extends BaseActivity {

    @InjectView(R.id.backTextView)
    ImageButton backTextView;
    @InjectView(R.id.passwordEditText)
    QSEditText passwordEditText;
    @InjectView(R.id.error_text)
    QSTextView errorText;
    @InjectView(R.id.submitButton)
    QSButton submitButton;

    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u18_reset_password_step2);
        ButterKnife.inject(this);

        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        password = getIntent().getStringExtra("password");

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(passwordEditText.getText().toString())) {
                    showError("请输入密码");
                    return;
                }

                if(passwordEditText.getText().toString().length() > 20){
                    showError("密码长度应小于20位");
                    return;
                }

                HashMap<String, String> params = new HashMap<>();
                params.put("currentPassword", password);
                params.put("password", passwordEditText.getText().toString());

                QSStringRequest qxStringRequest = new QSStringRequest(params, Request.Method.POST, QSAppWebAPI.getUpdateServiceUrl(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(U18ResetPasswordStep2Activity.class.getSimpleName(), "response:" + response);
                        MongoPeople user = UserParser.parseUpdate(response);
                        if (user == null) {
                            ErrorHandler.handle(U18ResetPasswordStep2Activity.this, MetadataParser.getError(response));
                        } else {
                            ToastUtil.showShortToast(U18ResetPasswordStep2Activity.this, "密码修改成功");
                            UserCommand.logOut(new Callback() {
                                @Override
                                public void onComplete() {
                                    startActivity(new Intent(U18ResetPasswordStep2Activity.this, U19LoginGuideActivity.class));
                                    finish();
                                }
                            });
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtil.showShortToast(U18ResetPasswordStep2Activity.this.getApplicationContext(), "请检查网络");
                    }
                });
                RequestQueueManager.INSTANCE.getQueue().add(qxStringRequest);
            }
        });
    }

    @Override
    public void reconn() {

    }

    private void showError(String msg){
        errorText.setText(msg);
        errorText.postDelayed(new Runnable() {
            @Override
            public void run() {
                errorText.setText("");
            }
        }, 5000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("U18ResetPasswordStep2Activity");
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("U18ResetPasswordStep2Activity");
        MobclickAgent.onPause(this);
    }
}

package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import com.android.volley.Request;
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
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.util.ToastUtil;
import com.focosee.qingshow.widget.QSButton;
import com.focosee.qingshow.widget.QSEditText;
import com.focosee.qingshow.widget.QSTextView;
import java.util.HashMap;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class U18ResetPasswordStep2 extends BaseActivity {

    @InjectView(R.id.backTextView)
    ImageButton backTextView;
    @InjectView(R.id.passwordEditText)
    QSEditText passwordEditText;
    @InjectView(R.id.reConfirmEditText)
    QSEditText reConfirmEditText;
    @InjectView(R.id.error_text)
    QSTextView errorText;
    @InjectView(R.id.submitButton)
    QSButton submitButton;

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

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(passwordEditText.getText().toString()) ||
                        TextUtils.isEmpty(reConfirmEditText.getText().toString())) {
                    showError("请输入正确的内容");
                    return;
                }
                if (!passwordEditText.getText().toString().equals(reConfirmEditText.getText().toString())) {
                    showError("请确认两次输入密码是否一致");
                    return;
                }

                if(reConfirmEditText.getText().toString().length() > 20){
                    showError("密码长度应小于20位");
                    return;
                }

                HashMap<String, String> params = new HashMap<>();
                params.put("password", passwordEditText.getText().toString());

                QSStringRequest qxStringRequest = new QSStringRequest(params, Request.Method.POST, QSAppWebAPI.getUpdateServiceUrl(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        MongoPeople user = UserParser.parseUpdate(response);
                        if (user == null) {
                            ErrorHandler.handle(U18ResetPasswordStep2.this, MetadataParser.getError(response));
                        } else {
                            QSModel.INSTANCE.setUser(user);
                            if(null == getParent())return;
                            if(null == getParent().getParent())return;
                            getParent().getParent().finish();
                            getParent().finish();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtil.showShortToast(U18ResetPasswordStep2.this.getApplicationContext(), "请检查网络");
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
}

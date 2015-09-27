package com.focosee.qingshow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import com.focosee.qingshow.R;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.util.ToastUtil;
import com.focosee.qingshow.util.VerificationHelper;
import com.focosee.qingshow.widget.QSButton;
import com.focosee.qingshow.widget.QSEditText;
import com.focosee.qingshow.widget.QSTextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class U17ResetPasswordStep1Activity extends BaseActivity {

    @InjectView(R.id.backImageView)
    ImageButton backImageView;
    @InjectView(R.id.phoneEditText)
    QSEditText phoneEditText;
    @InjectView(R.id.verification_code)
    QSEditText verificationCode;
    @InjectView(R.id.verification_code_btn)
    QSButton verificationCodeBtn;
    @InjectView(R.id.submitButton)
    QSButton submitButton;
    @InjectView(R.id.error_text)
    QSTextView errorText;

    private VerificationHelper verificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u17_reset_password_step1);
        ButterKnife.inject(this);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        verificationHelper = new VerificationHelper();

        verificationCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(phoneEditText.getText().toString())) {
                    verificationHelper.getVerification(verificationCode.getText().toString(), verificationCodeBtn, U17ResetPasswordStep1Activity.this);
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(phoneEditText.getText().toString())) {
                    showError(R.string.need_input_phone);
                    return;
                }
                if(TextUtils.isEmpty(verificationCode.getText().toString())){
                    showError(R.string.need_input_verification);
                    return;
                }

                Map<String, String> params = new HashMap<>();
                params.put("mobile", phoneEditText.getText().toString());
                params.put("verificationCode", verificationCode.getText().toString());
                UserCommand.resetPassword(params, new Callback() {
                    @Override
                    public void onComplete(JSONObject response) {
                        if (MetadataParser.hasError(response)) {
                            ToastUtil.showShortToast(U17ResetPasswordStep1Activity.this, "验证失败，请重试");
                            return;
                        }

                        try {
                            if (response.getJSONObject("data").getBoolean("success")) {
                                startActivity(new Intent(U17ResetPasswordStep1Activity.this, U18ResetPasswordStep2.class));
                            } else {
                                ToastUtil.showShortToast(U17ResetPasswordStep1Activity.this, "验证失败，请重试");
                                return;
                            }
                        } catch (JSONException e) {
                            ToastUtil.showShortToast(U17ResetPasswordStep1Activity.this, "验证失败，请重试");
                        }
                    }
                });
            }
        });
    }

    @Override
    public void reconn() {

    }

    private void showError(int msg){
        errorText.setText(getText(msg));
        errorText.postDelayed(new Runnable() {
            @Override
            public void run() {
                errorText.setText("");
            }
        }, 5000);
    }
}

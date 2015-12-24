package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.focosee.qingshow.R;
import com.focosee.qingshow.httpapi.QSRxApi;
import com.focosee.qingshow.util.ToastUtil;
import com.focosee.qingshow.util.VerificationHelper;
import com.focosee.qingshow.widget.QSButton;
import com.focosee.qingshow.widget.QSEditText;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class U23BindMobile extends AppCompatActivity implements View.OnClickListener{

    @InjectView(R.id.backImageView)
    ImageButton backImageView;
    @InjectView(R.id.confirmTextView)
    ImageView confirmTextView;
    @InjectView(R.id.phoneEditText)
    QSEditText phoneEditText;
    @InjectView(R.id.confirmRelativeLayout)
    RelativeLayout confirmRelativeLayout;
    @InjectView(R.id.verification_code)
    QSEditText verificationCode;
    @InjectView(R.id.verification_code_btn)
    QSButton verificationCodeBtn;
    @InjectView(R.id.verificationCodeRelativeLayout)
    PercentRelativeLayout verificationCodeRelativeLayout;
    @InjectView(R.id.submitButton)
    QSButton submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u23_bind_mobile);
        ButterKnife.inject(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.verification_code_btn:
                if(TextUtils.isEmpty(phoneEditText.getText())){
                    ToastUtil.showShortToast(U23BindMobile.this, "请输入手机号码");
                    return;
                }
                new VerificationHelper().getVerification(phoneEditText.getText().toString()
                        , verificationCodeBtn, U23BindMobile.this);
                break;
            case R.id.submitButton:
                if(TextUtils.isEmpty(phoneEditText.getText())){
                    ToastUtil.showShortToast(U23BindMobile.this, "请输入手机号码");
                    return;
                }
                if(TextUtils.isEmpty(verificationCode.getText())){
                    ToastUtil.showShortToast(U23BindMobile.this, "请输入验证码");
                    return;
                }
                QSRxApi.bindMobile(phoneEditText.getText().toString()
                        , verificationCode.getText().toString());
                break;
        }

    }
}

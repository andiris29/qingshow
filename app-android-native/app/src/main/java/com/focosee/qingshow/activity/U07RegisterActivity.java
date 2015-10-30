package com.focosee.qingshow.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
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
import com.focosee.qingshow.model.PushModel;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.util.FileUtil;
import com.focosee.qingshow.util.ToastUtil;
import com.focosee.qingshow.util.VerificationHelper;
import com.focosee.qingshow.widget.LoadingDialogs;
import com.focosee.qingshow.widget.QSButton;
import com.focosee.qingshow.widget.QSEditText;
import com.focosee.qingshow.wxapi.WxLoginedEvent;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class U07RegisterActivity extends BaseActivity implements View.OnClickListener {

    @InjectView(R.id.passwordEditText)
    EditText passwordEditText;
    @InjectView(R.id.phoneEditText)
    EditText phoneEditText;
    @InjectView(R.id.verification_code)
    QSEditText verificationCode;
    @InjectView(R.id.verification_code_btn)
    QSButton verificationCodeBtn;
    private RequestQueue requestQueue;

    private Context context;

    private LoadingDialogs dialogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);

        context = getApplicationContext();
        dialogs = new LoadingDialogs(U07RegisterActivity.this);


        requestQueue = RequestQueueManager.INSTANCE.getQueue();
    }

    private void register() {

        QSStringRequest stringRequest = new QSStringRequest(Request.Method.POST, QSAppWebAPI.getRegisterServiceUrl(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(U07RegisterActivity.class.getSimpleName(), "register_response:" + response);
                MongoPeople user = UserParser.parseRegister(response);
                if (user == null) {
                    ErrorHandler.handle(context, MetadataParser.getError(response));
                    return;
                } else {
                    FileUtil.uploadDefaultPortrait(U07RegisterActivity.this);
                    updateUser_phone();
                }
                QSModel.INSTANCE.login(user);
                startActivity(new Intent(U07RegisterActivity.this, U13PersonalizeActivity.class));
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtil.showShortToast(getApplicationContext(), "请重试");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();

                map.put("id", phoneEditText.getText().toString());
                map.put("password", passwordEditText.getText().toString());
                map.put("registrationId", PushModel.INSTANCE.getRegId());
                map.put("mobile", phoneEditText.getText().toString());
                map.put("verificationCode", verificationCode.getText().toString());
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void updateUser_phone(){
        Map<String, String> params = new HashMap<>();
        params.put("mobile", phoneEditText.getText().toString());
        UserCommand.update(params, new Callback() {
            @Override
            public void onComplete(JSONObject response) {
                updateSettings();
                startActivity(new Intent(U07RegisterActivity.this, U13PersonalizeActivity.class));
                finish();
            }

            @Override
            public void onError() {
                ToastUtil.showShortToast(getApplicationContext(), "请重试");
            }
        });
    }

    public void submit() {
        if (TextUtils.isEmpty(passwordEditText.getText().toString())) {
            ToastUtil.showShortToast(getApplicationContext(), "密码不能为空");
            return;
        }
        if (TextUtils.isEmpty(phoneEditText.getText().toString())) {
            ToastUtil.showShortToast(getApplicationContext(), "手机不能为空");
            return;
        }

        if (TextUtils.isEmpty(verificationCode.getText().toString())) {
            ToastUtil.showShortToast(getApplicationContext(), "请输入验证码");
            return;
        }

        register();
    }

    public void onEventMainThread(WxLoginedEvent event) {
        if ("error".equals(event.code)) {
            if (null != dialogs) {
                if (dialogs.isShowing()) {
                    dialogs.dismiss();
                }
            }
        }
        finish();
    }

    @Override
    public void reconn() {

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        if (null == dialogs) return;
        if (dialogs.isShowing()) {
            dialogs.dismiss();
        }
    }

    private void updateSettings() {
        Map<String, String> params = new HashMap<String, String>();

        UserCommand.update(params, new Callback() {

            @Override
            public void onError(int errorCode) {
                super.onError(errorCode);
                ErrorHandler.handle(U07RegisterActivity.this, errorCode);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("U07Register"); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("U07Register"); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backImageView:
                finish();
                break;
            case R.id.submitButton:
                submit();
                break;
            case R.id.verification_code_btn:
                if (TextUtils.isEmpty(phoneEditText.getText().toString())) {
                    ToastUtil.showShortToast(U07RegisterActivity.this, "请输入手机号码");
                    return;
                }
                new VerificationHelper().getVerification(phoneEditText.getText().toString(), verificationCodeBtn, U07RegisterActivity.this);
                break;
        }
    }


}

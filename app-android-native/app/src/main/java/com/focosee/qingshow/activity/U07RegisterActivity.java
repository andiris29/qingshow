package com.focosee.qingshow.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.constants.config.ShareConfig;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.QSStringRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.UserParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.GoToWhereAfterLoginModel;
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
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class U07RegisterActivity extends BaseActivity implements View.OnClickListener {

    @InjectView(R.id.accountEditText)
    EditText accountEditText;
    @InjectView(R.id.passwordEditText)
    EditText passwordEditText;
    @InjectView(R.id.phoneEditText)
    EditText phoneEditText;
    @InjectView(R.id.verification_code)
    QSEditText verificationCode;
    @InjectView(R.id.verification_code_btn)
    QSButton verificationCodeBtn;
    private RequestQueue requestQueue;
    private IWXAPI wxApi;

    private Context context;

    private LoadingDialogs dialogs;

    private AuthInfo mAuthInfo;
    /**
     * 注意：SsoHandler 仅当 SDK 支持 SSO 时有效
     */
    private SsoHandler mSsoHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);

        context = getApplicationContext();
        dialogs = new LoadingDialogs(U07RegisterActivity.this);

        wxApi = QSApplication.instance().getWxApi();

        // 创建微博实例
        //mWeiboAuth = new WeiboAuth(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        // 快速授权时，请不要传入 SCOPE，否则可能会授权不成功
        mAuthInfo = new AuthInfo(this, ShareConfig.SINA_APP_KEY, ShareConfig.SINA_REDIRECT_URL, ShareConfig.SCOPE);
        mSsoHandler = new SsoHandler(U07RegisterActivity.this, mAuthInfo);
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
                map.put("nickname", accountEditText.getText().toString());
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
        if (TextUtils.isEmpty(accountEditText.getText().toString())) {
            ToastUtil.showShortToast(getApplicationContext(), "昵称不能为空");
            return;
        }
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

    public void weiChatLogin() {
        // send oauth request
        if (!wxApi.isWXAppInstalled()) {
            //提醒用户没有按照微信
            ToastUtil.showShortToast(getApplicationContext(), "您还没有安装微信，请先安装微信");
            return;
        }

        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "qingshow_wxlogin";
        wxApi.sendReq(req);

        dialogs.show();
    }

    public void weiBoLogin() {
        mSsoHandler.authorize(new AuthListener());
        dialogs.show();
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
            case R.id.register_login_btn:
                startActivity(new Intent(U07RegisterActivity.this, U06LoginActivity.class));
                finish();
                break;
            case R.id.submitButton:
                submit();
                break;
            case R.id.weixinLoginButton:
                weiChatLogin();
                break;
            case R.id.weiboLoginButton:
                weiBoLogin();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(null != dialogs && resultCode != -1){
            if(dialogs.isShowing()) dialogs.dismiss();
        }
        mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     *    该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */

    /**
     * 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能
     */
    private Oauth2AccessToken mAccessToken;

    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                Map<String, String> map = new HashMap<>();
                map.put("access_token", mAccessToken.getToken());
                map.put("uid", mAccessToken.getUid());
                map.put("registrationId", PushModel.INSTANCE.getRegId());
                QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getUserLoginWbApi(), new JSONObject(map), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (MetadataParser.hasError(response)) {
                            ErrorHandler.handle(U07RegisterActivity.this, MetadataParser.getError(response));
                            if(null != dialogs){
                                if(dialogs.isShowing()) dialogs.dismiss();
                            }
                            return;
                        }

                        ToastUtil.showShortToast(U07RegisterActivity.this, getString(R.string.login_successed));
                        MongoPeople user = UserParser._parsePeople(response);
                        QSModel.INSTANCE.login(user);
                        if (null != GoToWhereAfterLoginModel.INSTANCE.get_class()) {
                            Intent intent = new Intent(U07RegisterActivity.this, GoToWhereAfterLoginModel.INSTANCE.get_class());
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("user", user);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        finish();
                        return;
                    }
                });

                RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                if(null != dialogs){
                    if(dialogs.isShowing()) dialogs.dismiss();
                }
                String code = values.getString("code");
                String message = "微博登录出错";
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                ToastUtil.showShortToast(getApplicationContext(), message);
            }
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onWeiboException(WeiboException e) {
        }
    }
}

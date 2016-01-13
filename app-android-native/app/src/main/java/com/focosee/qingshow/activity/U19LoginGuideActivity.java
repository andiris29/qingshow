package com.focosee.qingshow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.constants.config.ShareConfig;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.UserParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.GoToWhereAfterLoginModel;
import com.focosee.qingshow.model.PushModel;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.util.ToastUtil;
import com.focosee.qingshow.widget.LoadingDialogs;
import com.focosee.qingshow.widget.QSButton;
import com.focosee.qingshow.widget.QSTextView;
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

public class U19LoginGuideActivity extends BaseActivity implements View.OnClickListener {

    @InjectView(R.id.close)
    ImageView close;
    @InjectView(R.id.u19_mobile_login)
    ImageView u19MobileLogin;
    @InjectView(R.id.u19_mobile_login_text)
    QSTextView u19MobileLoginText;
    @InjectView(R.id.u19_weixin_login)
    ImageView u19WeixinLogin;
    @InjectView(R.id.u19_weixin_login_text)
    QSTextView u19WeixinLoginText;
    @InjectView(R.id.u19_loging_register_btn)
    QSButton u19LogingRegisterBtn;

    private LoadingDialogs dialogs;

    private IWXAPI wxApi;
    private AuthInfo mAuthInfo;
    /**
     * 注意：SsoHandler 仅当 SDK 支持 SSO 时有效
     */
    private SsoHandler mSsoHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u19_login_guide);
        ButterKnife.inject(this);

        wxApi = QSApplication.instance().getWxApi();
        dialogs = new LoadingDialogs(this);

        // 创建微博实例
        //mWeiboAuth = new WeiboAuth(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        // 快速授权时，请不要传入 SCOPE，否则可能会授权不成功
        mAuthInfo = new AuthInfo(this, ShareConfig.SINA_APP_KEY, ShareConfig.SINA_REDIRECT_URL, ShareConfig.SCOPE);
        mSsoHandler = new SsoHandler(U19LoginGuideActivity.this, mAuthInfo);

        if (QSModel.INSTANCE.getUserStatus() == MongoPeople.MATCH_FINISHED) {
            QSModel.INSTANCE.setUserStatus(MongoPeople.LOGIN_GUIDE_FINISHED);
        }

//        matchUI();
    }

    private void matchUI() {
        if (QSModel.INSTANCE.isGuest()) {
            u19WeixinLogin.setImageResource(R.drawable.weixin_hui);
            u19WeixinLogin.setEnabled(false);
            u19WeixinLoginText.setTextColor(getResources().getColor(R.color.gary));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.u19_mobile_login:
                startActivity(new Intent(U19LoginGuideActivity.this, U06LoginActivity.class));
                finish();
                break;
            case R.id.u19_weixin_login:
                weiChatLogin();
                break;
            case R.id.u19_loging_register_btn:
                startActivity(new Intent(U19LoginGuideActivity.this, U07RegisterActivity.class));
                finish();
                break;
            case R.id.close:
                finish();
                break;
        }
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

      //  dialogs.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != dialogs && resultCode != -1) {
            if (dialogs.isShowing()) dialogs.dismiss();
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
                            ErrorHandler.handle(U19LoginGuideActivity.this, MetadataParser.getError(response));
                            if (null != dialogs) {
                                if (dialogs.isShowing()) dialogs.dismiss();
                            }
                            return;
                        }

                        ToastUtil.showShortToast(U19LoginGuideActivity.this, getString(R.string.login_successed));
                        MongoPeople user = UserParser._parsePeople(response);
                        QSModel.INSTANCE.login(user);
                        if (QSModel.INSTANCE.isGuest()) {
                            startActivity(new Intent(U19LoginGuideActivity.this, U07RegisterActivity.class));
                            finish();
                            return;
                        }
                        if (null != GoToWhereAfterLoginModel.INSTANCE.get_class()) {
                            Intent intent = new Intent(U19LoginGuideActivity.this, GoToWhereAfterLoginModel.INSTANCE.get_class());
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
                if (null != dialogs) {
                    if (dialogs.isShowing()) dialogs.dismiss();
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

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("U19LoginGuideActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("U19LoginGuideActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    public void reconn() {

    }
}

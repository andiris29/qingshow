package com.focosee.qingshow.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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
import com.focosee.qingshow.httpapi.request.QSMultipartEntity;
import com.focosee.qingshow.httpapi.request.QSMultipartRequest;
import com.focosee.qingshow.httpapi.request.QSStringRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.UserParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.EventModel;
import com.focosee.qingshow.model.GoToWhereAfterLoginModel;
import com.focosee.qingshow.model.PushModel;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.util.BitMapUtil;
import com.focosee.qingshow.util.ImgUtil;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class U07RegisterActivity extends BaseActivity implements View.OnClickListener{

    private static final String DEBUG_TAG = "注册页";
    public static final String FINISH_CODE = "u07finish";
    @InjectView(R.id.accountEditText)
    EditText accountEditText;
    @InjectView(R.id.passwordEditText)
    EditText passwordEditText;
    @InjectView(R.id.reConfirmEditText)
    EditText reConfirmEditText;
    @InjectView(R.id.phoneEditText)
    EditText phoneEditText;
    private RequestQueue requestQueue;
    private IWXAPI wxApi;

    private Context context;

    private AuthInfo mAuthInfo;
    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;
    private int[] portraits = {R.drawable.default_head_1, R.drawable.default_head_2, R.drawable.default_head_3, R.drawable.default_head_4
            ,R.drawable.default_head_5, R.drawable.default_head_6};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);

        context = getApplicationContext();

        wxApi = QSApplication.instance().getWxApi();

        findViewById(R.id.register_login_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(U07RegisterActivity.this, U06LoginActivity.class));
                finish();
            }
        });

        // 创建微博实例
        //mWeiboAuth = new WeiboAuth(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        // 快速授权时，请不要传入 SCOPE，否则可能会授权不成功
        mAuthInfo = new AuthInfo(this, ShareConfig.SINA_APP_KEY, ShareConfig.SINA_REDIRECT_URL, ShareConfig.SCOPE);
        mSsoHandler = new SsoHandler(U07RegisterActivity.this, mAuthInfo);
        requestQueue = RequestQueueManager.INSTANCE.getQueue();
    }

    public void submit(){
        if (null == accountEditText.getText().toString() || "".equals(accountEditText.getText().toString())) {
            Toast.makeText(context, "昵称不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if (null == passwordEditText.getText().toString() || "".equals(passwordEditText.getText().toString())) {
            Toast.makeText(context, "密码不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if (null == phoneEditText.getText().toString() || "".equals(phoneEditText.getText().toString())) {
            Toast.makeText(context, "手机或邮箱不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if (!passwordEditText.getText().toString().equals(reConfirmEditText.getText().toString())) {
            Toast.makeText(context, "请确认两次密码是否一致", Toast.LENGTH_LONG).show();
            return;
        } else {
            QSStringRequest stringRequest = new QSStringRequest(Request.Method.POST, QSAppWebAPI.REGISTER_SERVICE_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    MongoPeople user = UserParser.parseRegister(response);
                    if (user == null) {
                        ErrorHandler.handle(context, MetadataParser.getError(response));
                    } else {
                        uploadImage();
                        QSModel.INSTANCE.setUser(user);
                        updateSettings();
                        Toast.makeText(context, "注册成功", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(U07RegisterActivity.this, U13PersonalizeActivity.class));
                        finish();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "请重新尝试", Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> map = new HashMap<>();

                    map.put("id", phoneEditText.getText().toString());
                    map.put("nickname", accountEditText.getText().toString());
                    map.put("password", passwordEditText.getText().toString());
                    map.put("registrationId", PushModel.INSTANCE.getRegId());

                    return map;
                }
            };
            requestQueue.add(stringRequest);
        }
    }

    private void uploadImage() {

        QSMultipartRequest multipartRequest = new QSMultipartRequest(Request.Method.POST,
                QSAppWebAPI.getUserUpdateportrait(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(MetadataParser.hasError(response))return;
                MongoPeople user = UserParser._parsePeople(response);
                if (user != null) {
                    UserCommand.refresh();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        int i = (int)Math.random() * 5;
        QSMultipartEntity multipartEntity = multipartRequest.getMultiPartEntity();
        multipartEntity.addBinaryPart("portrait", BitMapUtil.bmpToByteArray(BitmapFactory.decodeResource(getResources(), portraits[i]), false, Bitmap.CompressFormat.JPEG));
        RequestQueueManager.INSTANCE.getQueue().add(multipartRequest);
    }

    public void weiChatLogin() {
        // send oauth request
        if (!wxApi.isWXAppInstalled()) {
            //提醒用户没有按照微信
            Toast.makeText(this, "您还没有安装微信，请先安装微信", Toast.LENGTH_SHORT).show();
            return;
        }

        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "qingshow_wxlogin";
        wxApi.sendReq(req);
    }

    public void weiBoLogin() {
        mSsoHandler.authorize(new AuthListener());
    }

    private void loginWX(String code) {

        Map<String, String> map = new HashMap<>();
        map.put("code", code);
        map.put("registrationId", PushModel.INSTANCE.getRegId());
        JSONObject jsonObject = new JSONObject(map);
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getUserLoginWxApi(), jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(U07RegisterActivity.this, MetadataParser.getError(response));
                    finish();
                    return;
                }

                Toast.makeText(U07RegisterActivity.this, R.string.login_successed, Toast.LENGTH_SHORT).show();
                MongoPeople user = UserParser._parsePeople(response);
                if(TextUtils.isEmpty(user.portrait)){
                    uploadImage();
                }
                QSModel.INSTANCE.setUser(user);
                Intent intent = new Intent(U07RegisterActivity.this, GoToWhereAfterLoginModel.INSTANCE.get_class());
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    public void onEventMainThread(EventModel<String> eventModel) {
        if(eventModel.tag.equals(U07RegisterActivity.class.getSimpleName())){
            loginWX(eventModel.msg);
        }
    }

    @Override
    public void reconn() {

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
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
        switch (v.getId()){
            case R.id.backImageView:
                finish();
                break;
            case R.id.register_login_btn:
                Toast.makeText(U07RegisterActivity.this, "login", Toast.LENGTH_SHORT).show();
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
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
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
                        if(MetadataParser.hasError(response)){
                            ErrorHandler.handle(U07RegisterActivity.this, MetadataParser.getError(response));
                            return;
                        }

                        Toast.makeText(U07RegisterActivity.this, R.string.login_successed, Toast.LENGTH_SHORT).show();
                        MongoPeople user = UserParser._parsePeople(response);
                        QSModel.INSTANCE.setUser(user);
                        Intent intent = new Intent(U07RegisterActivity.this, GoToWhereAfterLoginModel.INSTANCE.get_class());
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("user", user);
                        intent.putExtras(bundle);
                        startActivity(intent);
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
                String code = values.getString("code");
                String message = "微博登录出错";
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(U07RegisterActivity.this, message, Toast.LENGTH_LONG).show();
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

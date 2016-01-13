package com.focosee.qingshow.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.R;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.QSMultipartEntity;
import com.focosee.qingshow.httpapi.request.QSMultipartRequest;
import com.focosee.qingshow.httpapi.request.QSStringRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.UserParser;
import com.focosee.qingshow.httpapi.response.error.ErrorCode;
import com.focosee.qingshow.model.GoToWhereAfterLoginModel;
import com.focosee.qingshow.model.PushModel;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.util.BitMapUtil;
import com.focosee.qingshow.util.ToastUtil;
import com.focosee.qingshow.widget.LoadingDialogs;
import com.focosee.qingshow.widget.QSButton;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class U06LoginActivity extends BaseActivity {

    public static String LOGIN_SUCCESS = "logined";
    @InjectView(R.id.forget_password_btn)
    QSButton forgetPasswordBtn;
    private EditText accountEditText;
    private EditText passwordEditText;
    private Context context;
    private RequestQueue requestQueue;
    private LoadingDialogs pDialog;
    private int[] portraits = {R.drawable.default_head_1, R.drawable.default_head_2, R.drawable.default_head_3, R.drawable.default_head_4
            , R.drawable.default_head_5, R.drawable.default_head_6};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        findViewById(R.id.backImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        context = getApplicationContext();

        Button submitButton;
        pDialog = new LoadingDialogs(this, R.style.dialog);

        submitButton = (Button) findViewById(R.id.submitButton);
        accountEditText = (EditText) findViewById(R.id.accountEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        requestQueue = RequestQueueManager.INSTANCE.getQueue();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pDialog.show();

                Map<String, String> map = new HashMap<>();
                map.put("id", accountEditText.getText().toString());
                map.put("password", passwordEditText.getText().toString());
                Log.i("JPush_QS", "login" + PushModel.INSTANCE.getRegId());

                QSStringRequest stringRequest = new QSStringRequest(map, Request.Method.POST,
                        QSAppWebAPI.getLoginServiceUrl(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                pDialog.dismiss();
                                Log.d(U06LoginActivity.class.getSimpleName(), "response:" + response);
                                MongoPeople user = UserParser.parseLogin(response);
                                if (user == null) {
                                    if (MetadataParser.getError(response) == ErrorCode.IncorrectMailOrPassword) {
                                        ToastUtil.showShortToast(getApplicationContext(), "账号或密码错误");
                                    } else {
                                        ToastUtil.showShortToast(getApplicationContext(), "请重试");
                                    }
                                } else {
                                    QSModel.INSTANCE.login(user);
                                    if (null != GoToWhereAfterLoginModel.INSTANCE.get_class()) {
                                        Intent intent = new Intent(U06LoginActivity.this, GoToWhereAfterLoginModel.INSTANCE.get_class());
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("user", user);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                        GoToWhereAfterLoginModel.INSTANCE.set_class(null);
                                        if (TextUtils.isEmpty(user.portrait)) {
                                            uploadImage();
                                        }
                                    }
                                    sendBroadcast(new Intent(LOGIN_SUCCESS));
                                    /**
                                     * 登录成功后发送registrationid给服务端
                                     */
                                    Map<String, String> params = new HashMap<>();
                                    params.put("registrationId", PushModel.INSTANCE.getRegId());
                                    QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getUserUpdateregistrationidApi()
                                            , new JSONObject(params), new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {

                                        }
                                    });
                                    RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
                                    finish();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
                    }
                });
                requestQueue.add(stringRequest);
            }
        });

        forgetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(U06LoginActivity.this, U17ResetPasswordStep1Activity.class));
                finish();
            }
        });
    }

    private void uploadImage() {

        QSMultipartRequest multipartRequest = new QSMultipartRequest(Request.Method.POST,
                QSAppWebAPI.getUserUpdateportrait(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) return;
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

        int i = (int) Math.random() * 5;
        QSMultipartEntity multipartEntity = multipartRequest.getMultiPartEntity();
        multipartEntity.addBinaryPart("portrait", BitMapUtil.bmpToByteArray(BitmapFactory.decodeResource(getResources(), portraits[i]), false, Bitmap.CompressFormat.JPEG));
        RequestQueueManager.INSTANCE.getQueue().add(multipartRequest);
    }

    @Override
    public void reconn() {

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("U06Login"); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("U06Login"); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

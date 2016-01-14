package com.focosee.qingshow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.R;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
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

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/1/7.
 */
public class U08ChangPasswordActivity extends BaseActivity {
    private QSEditText etCur, etNew;
    private ImageButton ibBack;
    private QSTextView tvTitle;
    private QSButton tvSava;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u08_chang_password);
        etCur = (QSEditText) findViewById(R.id.et_u08_cur_password);
        etNew = (QSEditText) findViewById(R.id.et_u08_new_password);
        ibBack = (ImageButton) findViewById(R.id.left_btn);
        tvTitle = (QSTextView) findViewById(R.id.title);
        tvSava = (QSButton) findViewById(R.id.right_btn);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("修改密码");
        tvSava.setVisibility(View.VISIBLE);
        tvSava.setText("保存");
        tvSava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cur = etCur.getText().toString();
                String newP = etNew.getText().toString();
                if (TextUtils.isEmpty(cur)) {
                    ToastUtil.showShortToast(U08ChangPasswordActivity.this, "请输入当前密码");
                    return;
                }
                if (TextUtils.isEmpty(newP)) {
                    ToastUtil.showShortToast(U08ChangPasswordActivity.this, "请输入新密码");
                    return;
                }

                if (newP.length() < 6) {
                    ToastUtil.showShortToast(U08ChangPasswordActivity.this, "新密码长度不能小于6");
                    Map<String, String> params = new HashMap<>();
                    return;
                }
                getSava(cur ,newP);


            }
        });

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void getSava(String curP, String newP) {
        HashMap<String, String> params = new HashMap<>();
        params.put("currentPassword", curP);
        params.put("password", newP);

        QSStringRequest qxStringRequest = new QSStringRequest(params, Request.Method.POST, QSAppWebAPI.getUpdateServiceUrl(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(U18ResetPasswordStep2Activity.class.getSimpleName(), "response:" + response);
                MongoPeople user = UserParser.parseUpdate(response);
                if (user == null) {
                    ErrorHandler.handle(U08ChangPasswordActivity.this, MetadataParser.getError(response));
                } else {
                    ToastUtil.showShortToast(U08ChangPasswordActivity.this, "密码修改成功");
//                    UserCommand.logOut(new Callback() {
//                        @Override
//                        public void onComplete() {
//                            startActivity(new Intent(U08ChangPasswordActivity.this, U19LoginGuideActivity.class));
//                            finish();
//                        }
//                    });
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtil.showShortToast(U08ChangPasswordActivity.this.getApplicationContext(), "请检查网络");
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(qxStringRequest);
    }


    @Override
    public void reconn() {

    }
}

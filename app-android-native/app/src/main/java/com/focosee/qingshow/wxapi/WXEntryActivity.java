package com.focosee.qingshow.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S03SHowActivity;
import com.focosee.qingshow.activity.U07RegisterActivity;
import com.focosee.qingshow.activity.U11EditAddressActivity;
import com.focosee.qingshow.activity.U14PayActivity;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.UserParser;
import com.focosee.qingshow.model.EventModel;
import com.focosee.qingshow.model.GoToWhereAfterLoginModel;
import com.focosee.qingshow.model.PushModel;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.util.FileUtil;
import com.focosee.qingshow.util.ToastUtil;
import com.focosee.qingshow.util.ValueUtil;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/7/8.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    public static final String ISSHARE_SUCCESSED = "WXEntryActivity_is_share_successed";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wxentry);
        QSApplication.instance().getWxApi().handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp instanceof SendAuth.Resp) {
            SendAuth.Resp resp = (SendAuth.Resp) baseResp;
            loginWX(resp.code);
            finish();
            return;
        }
        if (baseResp instanceof SendMessageToWX.Resp) {
            SendMessageToWX.Resp resp = (SendMessageToWX.Resp) baseResp;
            //trade
            if (resp.transaction.equals(ValueUtil.SHARE_TRADE)) {
                if (resp.errCode == SendMessageToWX.Resp.ErrCode.ERR_OK) {
                    EventBus.getDefault().post(new ShareTradeEvent(true));
                }
            }
            //bonus
            if (resp.transaction.equals(ValueUtil.SHARE_BONUS)) {
                if (resp.errCode == SendMessageToWX.Resp.ErrCode.ERR_OK) {
                    EventBus.getDefault().post(new ShareBonusEvent(resp.errCode));
                } else {
                    finish();
                }
            }
            //show
            if (resp.transaction.equals(ValueUtil.SHARE_SHOW)) {
                EventModel<Integer> eventModel;
                eventModel = new EventModel<>(S03SHowActivity.class.getSimpleName(), resp.errCode);
                eventModel.from = WXEntryActivity.class.getSimpleName();
                EventBus.getDefault().post(eventModel);
            }
            finish();
            return;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void loginWX(final String code) {

        Map<String, String> map = new HashMap<>();
        map.put("code", code);
        map.put("registrationId", PushModel.INSTANCE.getRegId());
        JSONObject jsonObject = new JSONObject(map);
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getUserLoginWxApi(), jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(WXEntryActivity.class.getSimpleName(), "response:" + response);
                if (MetadataParser.hasError(response)) {
                    ToastUtil.showShortToast(getApplicationContext(), "登录失败，请重试！");
                    EventBus.getDefault().post(new WxLoginedEvent("error"));
                    finish();
                    return;
                }

                MongoPeople user = UserParser._parsePeople(response);
                if (TextUtils.isEmpty(user.portrait)) {
                    FileUtil.uploadDefaultPortrait(WXEntryActivity.this);
                }
                QSModel.INSTANCE.login(user);
                if(TextUtils.isEmpty(user.mobile)){
                    Intent intent = new Intent(WXEntryActivity.this, U11EditAddressActivity.class);
                    startActivity(intent);
                    return;
                }
                if(QSModel.INSTANCE.isGuest()){
                    startActivity(new Intent(WXEntryActivity.this, U07RegisterActivity.class));
                    finish();
                    return;
                }
                if (null != GoToWhereAfterLoginModel.INSTANCE.get_class()) {
                    Intent intent = new Intent(WXEntryActivity.this, GoToWhereAfterLoginModel.INSTANCE.get_class());
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user", user);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                EventBus.getDefault().post(new WxLoginedEvent(null));
                finish();
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(150000, 0, 1f));
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }
}

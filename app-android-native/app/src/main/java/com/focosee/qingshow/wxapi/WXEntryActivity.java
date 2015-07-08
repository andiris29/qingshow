package com.focosee.qingshow.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.U01UserActivity;
import com.focosee.qingshow.activity.U07RegisterActivity;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.PeopleParser;
import com.focosee.qingshow.httpapi.response.dataparser.UserParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.U01Model;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/7/8.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

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
        System.out.println("baseResp");
        if(baseResp instanceof SendAuth.Resp){
            SendAuth.Resp resp = (SendAuth.Resp)baseResp;
            loginWX(resp.code);
        }
    }

    private void loginWX(String code){

        Map<String, String > map = new HashMap<>();
        map.put("code", code);
        JSONObject jsonObject = new JSONObject(map);

        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getUserLoginWxApi(code), jsonObject, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {
                if(MetadataParser.hasError(response)) {
                    ErrorHandler.handle(WXEntryActivity.this, MetadataParser.getError(response));
                    finish();
                    return;
                }

                Toast.makeText(WXEntryActivity.this, R.string.login_successed, Toast.LENGTH_SHORT).show();
                MongoPeople user = UserParser._parsePeople(response);
                QSModel.INSTANCE.setUser(user);
                U01Model.INSTANCE.setUser(user);
                startActivity(new Intent(WXEntryActivity.this, U01UserActivity.class));
                EventBus.getDefault().post(U07RegisterActivity.FINISH_CODE);
                finish();
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }
}

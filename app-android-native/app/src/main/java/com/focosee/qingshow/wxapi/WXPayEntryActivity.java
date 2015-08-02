package com.focosee.qingshow.wxapi;

import android.os.Bundle;
import android.widget.Toast;

import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.BaseActivity;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/7/23.
 */
public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxentry);
        QSApplication.instance().getWxApi().handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }


    @Override
    public void onResp(BaseResp baseResp) {
        Toast.makeText(WXPayEntryActivity.this, "fdsafdsafdsa", Toast.LENGTH_SHORT).show();
        EventBus.getDefault().post(new WXPayEvent(baseResp));
        finish();
    }

    @Override
    public void reconn() {

    }
}

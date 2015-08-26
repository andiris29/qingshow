package com.focosee.qingshow.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.widget.Toast;

import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S03SHowActivity;
import com.focosee.qingshow.activity.S17PayActivity;
import com.focosee.qingshow.activity.U07RegisterActivity;
import com.focosee.qingshow.activity.U09TradeListActivity;
import com.focosee.qingshow.adapter.U09TradeListAdapter;
import com.focosee.qingshow.model.EventModel;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.util.ValueUtil;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelbiz.AddCardToWXCardPackage;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import java.util.LinkedList;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/7/8.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private MongoTrade trade;
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
            EventModel<String> eventModel = new EventModel<>();
            eventModel.msg = resp.code;
            eventModel.tag = U07RegisterActivity.class.getSimpleName();
            EventBus.getDefault().post(eventModel);
            finish();
            return;
        }
        if (baseResp instanceof SendMessageToWX.Resp) {
            SendMessageToWX.Resp resp = (SendMessageToWX.Resp) baseResp;
            EventBus.getDefault().post(new PushEvent(resp));
            if (resp.transaction.equals(ValueUtil.SHARE_TRADE)) {
                if (resp.errCode == SendMessageToWX.Resp.ErrCode.ERR_OK) {
                    EventBus.getDefault().post(new ShareTradeEvent(true));
                }
            } else {
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
        trade = null;
    }
}

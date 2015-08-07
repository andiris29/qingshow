package com.focosee.qingshow.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S03SHowActivity;
import com.focosee.qingshow.activity.U07RegisterActivity;
import com.focosee.qingshow.activity.U09TradeListActivity;
import com.focosee.qingshow.adapter.U09TradeListAdapter;
import com.focosee.qingshow.model.EventModel;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxentry);
        QSApplication.instance().getWxApi().handleIntent(getIntent(), this);
        EventBus.getDefault().register(this);
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
            if (resp.transaction.equals(U09TradeListAdapter.transaction)) {
                if (resp.errCode == SendMessageToWX.Resp.ErrCode.ERR_OK) {
                    Toast.makeText(WXEntryActivity.this, "分享成功，您可以付款了。", Toast.LENGTH_SHORT).show();

                    EventBus.getDefault().post(trade);
                } else {
                    Toast.makeText(WXEntryActivity.this, "分享失败，请重试。", Toast.LENGTH_SHORT).show();
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

    public void onEventMainThread(MongoTrade trade) {
        this.trade = trade;
    }

    @Override
    protected void onPause() {
        super.onPause();
        trade = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        trade = null;
    }
}

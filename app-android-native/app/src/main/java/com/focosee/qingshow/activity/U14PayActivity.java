package com.focosee.qingshow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.fragment.S17PaymentFragment;
import com.focosee.qingshow.activity.fragment.S17ReceiptFragment;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.PayCommand;
import com.focosee.qingshow.command.TradeRefreshCommand;
import com.focosee.qingshow.command.UserReceiverCommand;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.TradeParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.util.StringUtil;
import com.focosee.qingshow.util.ToastUtil;
import com.focosee.qingshow.util.ValueUtil;
import com.focosee.qingshow.widget.ConfirmDialog;
import com.focosee.qingshow.widget.LoadingDialogs;
import com.focosee.qingshow.wxapi.WXPayEvent;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/3/11.
 */
public class U14PayActivity extends BaseActivity implements View.OnClickListener, IWXAPIEventHandler{

    public static final String INPUT_ITEM_ENTITY = "INPUT_ENTITY";

    private Button submit;
    private TextView priceTV;
    private S17PaymentFragment paymentFragment;
    private S17ReceiptFragment receiptFragment;
    private View dialog;
    private MongoPeople.Receiver receiver;
    private Map params;
    private MongoTrade trade;
    private String selectedPeopleReceiverUuid;
    private double totalFee;

    @Override
    public void reconn() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_s17_trade);
        EventBus.getDefault().register(this);

        trade = (MongoTrade) getIntent().getSerializableExtra(INPUT_ITEM_ENTITY);
        Log.d(U14PayActivity.class.getSimpleName(), "trade:" + trade);

        getTradeFromNet();
    }

    private void getTradeFromNet(){

        final LoadingDialogs dialogs = new LoadingDialogs(U14PayActivity.this);
        dialogs.show();

        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getTradeApi(trade._id), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialogs.dismiss();
                if(MetadataParser.hasError(response)){
                    ErrorHandler.handle(U14PayActivity.this, MetadataParser.getError(response));
                    trade = null;
                    return;
                }
                trade = TradeParser.parseQuery(response).get(0);
                totalFee = (trade.itemSnapshot.promoPrice.floatValue() - trade.itemSnapshot.expectable.reduction.floatValue()) * trade.quantity;
                init();
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void init() {

        priceTV = (TextView) findViewById(R.id.s11_paynum);
        submit = (Button) findViewById(R.id.s11_submit_button);
        dialog = LayoutInflater.from(this).inflate(R.layout.dialog_trade_success, null);

        receiptFragment = (S17ReceiptFragment) getSupportFragmentManager().findFragmentById(R.id.s11_receipt);
        paymentFragment = (S17PaymentFragment) getSupportFragmentManager().findFragmentById(R.id.s11_payment);

        dialog.findViewById(R.id.s11_dialog_continue).setOnClickListener(this);
        dialog.findViewById(R.id.s11_dialog_list).setOnClickListener(this);
        submit.setOnClickListener(this);
        priceTV.setText(StringUtil.FormatPrice(totalFee) + "");

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        QSApplication.instance().getWxApi().handleIntent(intent, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.s11_back_image_button:
                finish();
                break;
            case R.id.s11_submit_button:
                if(null == trade){
                    getTradeFromNet();
                    return;
                }
                submitTrade();
                break;
            default:
                break;
        }

    }

    public void onEventMainThread(WXPayEvent event) {
        BaseResp baseResp = event.baseResp;
        submit.setEnabled(true);
        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX && baseResp.errCode == BaseResp.ErrCode.ERR_OK) {
            if (null != trade) {
                TradeRefreshCommand.refresh(trade._id, new Callback() {
                    @Override
                    public void onComplete(int result) {
                        showPayStatus();
                    }

                    @Override
                    public void onError(int errorCode) {
                        ErrorHandler.handle(U14PayActivity.this, errorCode);
                    }
                });
            }
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void submitTrade() {
        receiver = receiptFragment.getReceiver();
        boolean hasEmptyAds = false;
        Map<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(receiver.name)) params.put("name", receiver.name);
        else hasEmptyAds = true;

        if (!TextUtils.isEmpty(receiver.phone)) params.put("phone", receiver.phone);
        else hasEmptyAds = true;

        if (!TextUtils.isEmpty(receiver.province)) params.put("province", receiver.province);
        else hasEmptyAds = true;

        if (!TextUtils.isEmpty(receiver.address)) params.put("address", receiver.address);
        else hasEmptyAds = true;

        if (hasEmptyAds) {
            ToastUtil.showShortToast(getApplicationContext(), "请将联系方式填写完整");
            return;
        }

        if (TextUtils.isEmpty(paymentFragment.getPaymentMode())) {
            ToastUtil.showShortToast(getApplicationContext(), "请选择支付方式");
            return;
        }

        submit.setEnabled(false);

        UserReceiverCommand.saveReceiver(params, new Callback() {
            @Override
            public void onComplete(JSONObject response) {
                super.onComplete(response);
                try {
                    selectedPeopleReceiverUuid = response.getJSONObject("data").getString("receiverUuid").toString();
                } catch (Exception ex) {

                }
                createTrade();
            }

            @Override
            public void onError(int errorCode) {
                ErrorHandler.handle(U14PayActivity.this, errorCode);
            }
        });
    }

    private void createTrade() {
        if (null == paymentFragment.getPaymentMode()) {
            return;
        }
        params = new HashMap();
        Log.d(U14PayActivity.class.getSimpleName(), "totalFee:" + totalFee);
        params.put("totalFee", totalFee);
        try {
            if (paymentFragment.getPaymentMode().equals(getResources().getString(R.string.weixin))) {
                JSONObject jsonObject = new JSONObject();
                JSONObject weixin = new JSONObject();
                weixin.put("partnerId", "");
                jsonObject.put("weixin", weixin);
                params.put("pay", jsonObject);
            }
            if (paymentFragment.getPaymentMode().equals(getResources().getString(R.string.alipay))) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("alipay", new JSONObject());
                params.put("pay", jsonObject);
            }
            params.put("selectedPeopleReceiverUuid", selectedPeopleReceiverUuid);
            params.put("_id",trade._id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST,
                QSAppWebAPI.getPayApi(), new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(U14PayActivity.class.getSimpleName(), "response:" + response);
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(U14PayActivity.this, MetadataParser.getError(response));
                    return;
                }
                trade = TradeParser.parse(response);
                pay(trade);
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }


    private void pay(final MongoTrade trade) {
        String paymentMode = paymentFragment.getPaymentMode();

        if (paymentMode.equals(getResources().getString(R.string.alipay))) {
            PayCommand.alipay(trade, U14PayActivity.this, new Callback() {
                @Override
                public void onComplete() {
                    submit.setEnabled(true);
                    TradeRefreshCommand.refresh(trade._id, new Callback() {
                        @Override
                        public void onComplete(int result) {
                            showPayStatus();
                        }

                        @Override
                        public void onError(int errorCode) {

                        }
                    });
                }

                @Override
                public void onError() {
                    submit.setEnabled(true);
                }
            });
        }

        if (paymentMode.equals(getResources().getString(R.string.weixin))) {
            PayCommand.weixin(trade);
        }
    }

    private void showPayStatus() {
        ConfirmDialog dialog = new ConfirmDialog(U14PayActivity.this);
        dialog.setTitle("支付成功");
        dialog.setCancel("继续逛逛", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(ValueUtil.PAY_FINISHED);
                U14PayActivity.this.finish();
            }
        });
        dialog.setConfirm("查看订单", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(U14PayActivity.this, U09TradeListActivity.class);
                intent.putExtra(U09TradeListActivity.FROM_WHERE, U14PayActivity.class.getSimpleName());
                startActivity(intent);
                U14PayActivity.this.finish();
            }
        });
        dialog.show();
        submit.setEnabled(true);

    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}

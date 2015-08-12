package com.focosee.qingshow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.focosee.qingshow.constants.code.StatusCode;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.TradeParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.util.StringUtil;
import com.focosee.qingshow.widget.ConfirmDialog;
import com.focosee.qingshow.wxapi.WXPayEvent;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/3/11.
 */
public class S17PayActivity extends BaseActivity implements View.OnClickListener, IWXAPIEventHandler{

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


    @Override
    public void reconn() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_s17_trade);
        EventBus.getDefault().register(this);

        trade = (MongoTrade) getIntent().getSerializableExtra(INPUT_ITEM_ENTITY);
        init();

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
        priceTV.setText(StringUtil.FormatPrice(String.valueOf(trade.actualPrice)));

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
                submitTrade();
                break;
            default:
                break;
        }

    }

    public void onEventMainThread(WXPayEvent event) {
        BaseResp baseResp = event.baseResp;
        submit.setEnabled(true);
        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (null != trade) {
                TradeRefreshCommand.refresh(trade._id, new Callback() {
                    @Override
                    public void onComplete(int result) {
                        showPayStatus(result);
                    }

                    @Override
                    public void onError(int errorCode) {
                        ErrorHandler.handle(S17PayActivity.this, errorCode);
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
            Toast.makeText(getApplicationContext(), "请将联系方式填写完整", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(paymentFragment.getPaymentMode())) {
            Toast.makeText(getApplicationContext(), "请选择支付方式", Toast.LENGTH_SHORT).show();
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
                ErrorHandler.handle(S17PayActivity.this, errorCode);
            }
        });
    }

    private void createTrade() {
        if (null == paymentFragment.getPaymentMode()) {
            return;
        }
        params = new HashMap();
        params.put("totalFee", trade.actualPrice);
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
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(S17PayActivity.this, MetadataParser.getError(response));
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
            PayCommand.alipay(trade, S17PayActivity.this, new Callback() {
                @Override
                public void onComplete() {
                    TradeRefreshCommand.refresh(trade._id, new Callback() {
                        @Override
                        public void onComplete(int result) {
                            showPayStatus(result);
                        }

                        @Override
                        public void onError(int errorCode) {
                            ErrorHandler.handle(S17PayActivity.this, errorCode);
                        }
                    });
                }

                @Override
                public void onError() {

                }
            });
        }

        if (paymentMode.equals(getResources().getString(R.string.weixin))) {
            PayCommand.weixin(trade);
        }
    }

    private void showPayStatus(int status) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setTitle(StatusCode.getStatusText(status));
        dialog.setCancel("继续逛逛", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dialog.setConfirm("查看订单", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(S17PayActivity.this, U09TradeListActivity.class);
                intent.putExtra(U09TradeListActivity.FROM_WHERE, S17PayActivity.class.getSimpleName());
                startActivity(intent);
                finish();
            }
        });
        dialog.show(getSupportFragmentManager());
        submit.setEnabled(true);

    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {

    }
}

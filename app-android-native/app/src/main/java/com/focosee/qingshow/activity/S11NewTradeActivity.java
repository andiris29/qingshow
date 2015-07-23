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
import com.focosee.qingshow.activity.fragment.S11DetailsEvent;
import com.focosee.qingshow.activity.fragment.S11DetailsFragment;
import com.focosee.qingshow.activity.fragment.S11PaymentFragment;
import com.focosee.qingshow.activity.fragment.S11ReceiptFragment;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.PayCommand;
import com.focosee.qingshow.command.TradeRefreshCommand;
import com.focosee.qingshow.command.UserReceiverCommand;
import com.focosee.qingshow.constants.code.StatusCode;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.TradeParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.vo.mongo.MongoOrder;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.util.StringUtil;
import com.focosee.qingshow.widget.ConfirmDialog;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/3/11.
 */
public class S11NewTradeActivity extends BaseActivity implements View.OnClickListener, IWXAPIEventHandler {

    public static final String INPUT_ITEM_ENTITY = "INPUT_ITEM_ENTITY";

    private Button submit;
    private TextView priceTV;
    private S11DetailsFragment detailsFragment;
    private S11PaymentFragment paymentFragment;
    private S11ReceiptFragment receiptFragment;
    private View dialog;

    private MongoPeople.Receiver receiver;

    private Map params;
    private MongoOrder order;
    private MongoTrade trade;
    private MongoPeople.MeasureInfo measureInfo;

    private String selectedPeopleReceiverUuid;


    @Override
    public void reconn() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_s11_trade);
        EventBus.getDefault().register(this);

        init();
        initOrder();

    }

    private void init() {

        priceTV = (TextView) findViewById(R.id.s11_paynum);
        submit = (Button) findViewById(R.id.s11_submit_button);
        dialog = LayoutInflater.from(this).inflate(R.layout.dialog_trade_success, null);

        detailsFragment = (S11DetailsFragment) getSupportFragmentManager().findFragmentById(R.id.s11_details);
        receiptFragment = (S11ReceiptFragment) getSupportFragmentManager().findFragmentById(R.id.s11_receipt);
        paymentFragment = (S11PaymentFragment) getSupportFragmentManager().findFragmentById(R.id.s11_payment);

        dialog.findViewById(R.id.s11_dialog_continue).setOnClickListener(this);
        dialog.findViewById(R.id.s11_dialog_list).setOnClickListener(this);
        submit.setOnClickListener(this);

        QSApplication.instance().getWxApi().handleIntent(getIntent(), this);
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

    private void initOrder() {
        setAllow(detailsFragment.getOrder() != null,
                detailsFragment.getOrder());
    }

    public void onEventMainThread(S11DetailsEvent event) {
        measureInfo = event.getMeasureInfo();
        setAllow(event.isExists(), event.getOrder());
    }

    private void setAllow(boolean allow, MongoOrder order) {
        if (allow) {
            submit.setBackgroundResource(R.drawable.submit_button);
            submit.setClickable(true);
            this.order = order;
            priceTV.setText(StringUtil.FormatPrice(String.valueOf(order.price * order.quantity)));
        } else {
            submit.setBackgroundColor(getResources().getColor(R.color.hint_text_color));
            submit.setClickable(false);
            priceTV.setText("商品暂无");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void submitTrade() {
        submit.setEnabled(false);

        JSONObject jsonObject = null;
        try {
             jsonObject = new JSONObject(QSGsonFactory.create().toJson(measureInfo));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.UPDATE_SERVICE_URL, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);

        receiver = receiptFragment.getReceiver();

        Map<String, String> params = new HashMap<String, String>();
        if (!TextUtils.isEmpty(receiver.name)) params.put("name", receiver.name);
        if (!TextUtils.isEmpty(receiver.phone)) params.put("phone", receiver.phone);
        if (!TextUtils.isEmpty(receiver.province)) params.put("province", receiver.province);
        if (!TextUtils.isEmpty(receiver.address)) params.put("address", receiver.address);
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
                ErrorHandler.handle(S11NewTradeActivity.this, errorCode);
            }
        });
    }

    private void createTrade() {
        if (null == paymentFragment.getPaymentMode()) {
            return;
        }

        params = new HashMap();
        params.put("totalFee", order.price);
        order.selectedPeopleReceiverUuid = selectedPeopleReceiverUuid;
        List<MongoOrder> orders = new ArrayList<MongoOrder>();
        orders.add(order);
        try {
            if (paymentFragment.getPaymentMode().equals(getResources().getString(R.string.weixin))) {
                JSONObject jsonObject = new JSONObject();
                JSONObject weixin = new JSONObject();
                weixin.put("partnerId","");
                jsonObject.put("weixin", weixin);
                params.put("pay",jsonObject);
            }
            if (paymentFragment.getPaymentMode().equals(getResources().getString(R.string.alipay))){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("alipay", "");
                params.put("pay",jsonObject);
            }

            JSONArray array = new JSONArray(QSGsonFactory.create().toJson(orders));
            params.put("orders", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST,
                QSAppWebAPI.getTradeCreateApi(), new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    Log.i("tag",MetadataParser.getError(response) + "");
                    ErrorHandler.handle(S11NewTradeActivity.this, MetadataParser.getError(response));
                    return;
                }
                trade = TradeParser.parse(response);
                trade.totalFee = 0.01;
                pay(trade);
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }


    private void pay(final MongoTrade trade) {
        String paymentMode = paymentFragment.getPaymentMode();

        if (paymentMode.equals(getResources().getString(R.string.alipay))) {
            PayCommand.alipay(trade, S11NewTradeActivity.this, new Callback() {
                @Override
                public void onComplete() {
                    TradeRefreshCommand.refresh(trade._id, new Callback() {
                        @Override
                        public void onComplete(int result) {
                            showPayStatus(result);
                        }

                        @Override
                        public void onError(int errorCode) {
                            Log.i("tag", errorCode + "");
                            ErrorHandler.handle(S11NewTradeActivity.this,errorCode);
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

    private void showPayStatus(int status){
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setTitle(StatusCode.statusArrays[status]);
        dialog.setCancel("继续逛逛", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(S11NewTradeActivity.this, S02ShowClassify.class);
//                intent.putExtra(S02ShowClassify.INPUT_CATEGORY, 0);
//                startActivity(intent);
//                finish();
            }
        });
        dialog.setConfirm("查看订单",new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(S11NewTradeActivity.this, U09TradeListActivity.class));
                finish();
            }
        });
        dialog.show(getSupportFragmentManager());
        submit.setEnabled(true);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        QSApplication.instance().getWxApi().handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }


    @Override
    public void onResp(BaseResp resp) {
        Log.i("tag",resp.toString());
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (null != trade) {
                TradeRefreshCommand.refresh(trade._id, new Callback() {
                    @Override
                    public void onComplete(int result) {
                        showPayStatus(result);
                    }

                    @Override
                    public void onError(int errorCode) {
                        ErrorHandler.handle(S11NewTradeActivity.this, errorCode);
                    }
                });
            }
        }
    }
}

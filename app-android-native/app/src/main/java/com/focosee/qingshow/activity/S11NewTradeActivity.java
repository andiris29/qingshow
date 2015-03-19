package com.focosee.qingshow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.focosee.qingshow.Fragment.S11DetailsEvent;
import com.focosee.qingshow.Fragment.S11DetailsFragment;
import com.focosee.qingshow.Fragment.S11PaymentFragment;
import com.focosee.qingshow.Fragment.S11ReceiptFragment;
import com.focosee.qingshow.R;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.UserReceiverCommand;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.error.ErrorCode;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.model.vo.mongo.MongoOrder;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by Administrator on 2015/3/11.
 */
public class S11NewTradeActivity extends BaseActivity implements View.OnClickListener {

    public static final String INPUT_ITEM_ENTITY = "INPUT_ITEM_ENTITY";
    public static final String INPUT_RECEIVER_ENTITY = "INPUT_RECEIVER_ENTITY";

    private ImageView submit;
    private TextView priceTV;
    private View dialog;
    private S11DetailsFragment detailsFragment;
    private S11PaymentFragment paymentFragment;
    private S11ReceiptFragment receiptFragment;

    private MongoPeople.Receiver receiver;

    private Map params;
    private MongoOrder order;

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
        submit = (ImageView) findViewById(R.id.s11_submit_button);
        dialog = LayoutInflater.from(S11NewTradeActivity.this).inflate(R.layout.dialog_trade_success, null);

        detailsFragment = (S11DetailsFragment) getSupportFragmentManager().findFragmentById(R.id.s11_details);
        receiptFragment = (S11ReceiptFragment) getSupportFragmentManager().findFragmentById(R.id.s11_receipt);
        paymentFragment = (S11PaymentFragment) getSupportFragmentManager().findFragmentById(R.id.s11_payment);

        submit.setOnClickListener(this);
        dialog.findViewById(R.id.s11_dialog_continue).setOnClickListener(this);
        dialog.findViewById(R.id.s11_dialog_list).setOnClickListener(this);
    }

    private void initOrder() {
        if (detailsFragment.getOrder() == null) {
            submit.setBackgroundResource(R.drawable.s11_submit_off);
            submit.setClickable(false);
            return;
        }
        order = detailsFragment.getOrder();
        priceTV.setText(StringUtil.FormatPrice(order.price));
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
            case R.id.s11_dialog_continue:
                Intent intent = new Intent(this, S02ShowClassify.class);
                intent.putExtra(S02ShowClassify.INPUT_CATEGORY, 0);
                startActivity(intent);
                finish();
                break;
            case R.id.s11_dialog_list:
                startActivity(new Intent(this, U09TradeListActivity.class));
                finish();
                break;
            default:
                break;
        }

    }


    public void onEventMainThread(S11DetailsEvent event) {
        if (!event.isExists()) {
            submit.setBackgroundResource(R.drawable.s11_submit_off);
            submit.setClickable(false);
            return;
        }
        order = event.getOrder();
        priceTV.setText(StringUtil.FormatPrice(order.price));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void submitTrade() {
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
                    Log.i("tag", selectedPeopleReceiverUuid);
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

        params = new HashMap();
        params.put("totalFee", order.price);

        order.selectedPeopleReceiverUuid = selectedPeopleReceiverUuid;
        List<MongoOrder> orders = new ArrayList<MongoOrder>();
        orders.add(order);
        try {
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
                    return;
                }
                new MaterialDialog(S11NewTradeActivity.this).setView(dialog).show();
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

}

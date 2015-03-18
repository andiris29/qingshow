package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.focosee.qingshow.Fragment.S11DetailsEvent;
import com.focosee.qingshow.Fragment.S11DetailsFragment;
import com.focosee.qingshow.Fragment.S11PaymentFragment;
import com.focosee.qingshow.Fragment.S11ReceiptFragment;
import com.focosee.qingshow.R;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.UserReceiverCommand;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/3/11.
 */
public class S11NewTradeActivity extends BaseActivity implements View.OnClickListener{

    public static final String INPUT_ITEM_ENTITY = "INPUT_ITEM_ENTITY";
    private MongoItem itemEntity;

    private ImageView submit;
    private S11PaymentFragment paymentFragment;
    private S11ReceiptFragment receiptFragment;

    private MongoPeople.Receiver receiver;

    private MongoTrade trade;

    private String totalFee;
    private String receiverUuid;


    @Override
    public void reconn() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemEntity = (MongoItem) getIntent().getExtras().getSerializable(S11NewTradeActivity.INPUT_ITEM_ENTITY);
        setContentView(R.layout.activity_s11_trade);
        EventBus.getDefault().register(this);
        init();
    }

    private void init() {

        submit = (ImageView) findViewById(R.id.s11_submit_button);

        receiptFragment = (S11ReceiptFragment) getFragmentManager().findFragmentById(R.id.s11_receipt);
        paymentFragment = (S11PaymentFragment) getFragmentManager().findFragmentById(R.id.s11_payment);

        findViewById(R.id.s11_back_image_button).setOnClickListener(this);
        submit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.s11_back_image_button:
                finish();
                break;
            case R.id.s11_submit_button:
                submitTrade();
            default:
                break;
        }

    }

    public void onEventMainThread(S11DetailsEvent event){
        if(!event.isExists()){
            //TODO change img
            submit.setImageResource(R.drawable.ic_launcher);
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void submitTrade() {
        receiver = receiptFragment.getReceiver();

        Map<String, String> params = new HashMap<String, String>();
        if(!TextUtils.isEmpty(receiver.name)) params.put("name",receiver.name);
        if(!TextUtils.isEmpty(receiver.phone)) params.put("phone",receiver.phone);
        if(!TextUtils.isEmpty(receiver.province)) params.put("province",receiver.province);
        if(!TextUtils.isEmpty(receiver.address)) params.put("address",receiver.address);
        UserReceiverCommand.saveReceiver(params,new Callback(){
            @Override
            public void onComplete(JSONObject response) {
                super.onComplete(response);
                try {
                  receiverUuid = response.getJSONObject("data").getString("receiverUuid").toString();
                    Log.i("tag",receiverUuid);
                }catch (Exception ex){

                }
            }
        });
    }

}

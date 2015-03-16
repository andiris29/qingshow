package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.focosee.qingshow.Fragment.S11DetailsFragment;
import com.focosee.qingshow.Fragment.S11PaymentFragment;
import com.focosee.qingshow.Fragment.S11ReceiptFragment;
import com.focosee.qingshow.R;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/3/11.
 */
public class S11NewTradeActivity extends BaseActivity implements View.OnClickListener{

    public static final String INPUT_ITEM_ENTITY = "INPUT_ITEM_ENTITY";

    private ImageView submit;
    private S11DetailsFragment detailsFragment;
    private S11PaymentFragment paymentFragment;
    private S11ReceiptFragment receiptFragment;

    private MongoPeople.Receiver receiver;

    @Override
    public void reconn() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s11_trade);
        init();
    }

    private void init() {

        submit = (ImageView) findViewById(R.id.s11_submit_button);

        detailsFragment = (S11DetailsFragment) getFragmentManager().findFragmentById(R.id.s11_details);
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

    private void submitTrade() {
        receiver = receiptFragment.getReceiver();
    }

}

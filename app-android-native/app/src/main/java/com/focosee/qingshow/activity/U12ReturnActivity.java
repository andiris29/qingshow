package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import com.android.volley.Response;
import com.focosee.qingshow.R;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.util.ToastUtil;
import com.focosee.qingshow.widget.LoadingDialogs;
import com.focosee.qingshow.widget.QSButton;
import com.focosee.qingshow.widget.QSEditText;
import com.focosee.qingshow.widget.QSTextView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/3/16.
 */
public class U12ReturnActivity extends BaseActivity {
    public static final String TRADE_ENTITY = "return_logistic_entity";
    @InjectView(R.id.address_return_activity)
    QSTextView addressReturnActivity;
    @InjectView(R.id.receiver_return_activity)
    QSTextView receiverReturnActivity;
    @InjectView(R.id.phone_return_activity)
    QSTextView phoneReturnActivity;
    @InjectView(R.id.company_return_activity)
    QSEditText company;
    @InjectView(R.id.returnNumber__return_activity)
    QSEditText returnNo;
    @InjectView(R.id.matters__return_activity)
    QSEditText common;
    @InjectView(R.id.apply_return_btn_return_activity)
    QSButton applyReturnBtn;

    private MongoTrade trade;
    private boolean isSuccessed = false;
    private LoadingDialogs loadingDialog;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_return);
        ButterKnife.inject(this);

        findViewById(R.id.return_back_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadingDialog = new LoadingDialogs(this, R.style.dialog);
        trade = (MongoTrade) getIntent().getSerializableExtra(TRADE_ENTITY);
        position = getIntent().getIntExtra("position", 0);
        if (null != trade.itemSnapshot) {
            if (null != trade.itemSnapshot.returnInfo) {
                addressReturnActivity.setText(trade.itemSnapshot.returnInfo.address);
                receiverReturnActivity.setText(trade.itemSnapshot.returnInfo.name);
                phoneReturnActivity.setText(trade.itemSnapshot.returnInfo.phone);
            }
        }

        applyReturnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitForm();
            }
        });
    }

    public void commitForm() {

        if (TextUtils.isEmpty(company.getText().toString())) {
            ToastUtil.showShortToast(getApplicationContext(), "请填写货运公司");
            return;
        }

        if (TextUtils.isEmpty(returnNo.getText().toString())) {
            ToastUtil.showShortToast(getApplicationContext(), "请填写货运单号");
            return;
        }

        loadingDialog.show();
        Map params = new HashMap();
        Map returnLogistic = new HashMap();

        params.put("_id", trade._id);
        params.put("status", 7);
        params.put("comment", common.getText().toString());


        returnLogistic.put("company", company.getText().toString());
        returnLogistic.put("trackingID", returnNo.getText().toString());
        params.put("returnLogistic", returnLogistic);

        QSJsonObjectRequest jor2 = new QSJsonObjectRequest(QSAppWebAPI.getTradeStatustoApi(), new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingDialog.dismiss();
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(U12ReturnActivity.this, MetadataParser.getError(response));
                    return;
                }
                EventBus.getDefault().post(new U12ReturnEvent(position));
                ToastUtil.showShortToast(getApplicationContext(), getResources().getString(R.string.toast_activity_return));
                finish();
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jor2);
    }

    @Override
    public void reconn() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("U12ReturnActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("U12ReturnActivity");
        MobclickAgent.onPause(this);
    }
}

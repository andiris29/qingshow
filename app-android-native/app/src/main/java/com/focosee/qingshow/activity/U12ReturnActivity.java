package com.focosee.qingshow.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;
import com.focosee.qingshow.R;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.TradeParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.ReturnInformationModel;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.util.FileUtil;
import com.focosee.qingshow.widget.LoadingDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.timessquare.CalendarPickerView;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/3/16.
 */
public class U12ReturnActivity extends BaseActivity{
    public static final String TRADE_ENTITY = "return_logistic_entity";
    //一周的毫秒数
    private long weekTime = 7*24*3600*1000;

    private ReturnInformationModel returnInformationModel;
    private EditText company;
    private EditText returnNo;
    private LinearLayout returnDateLayout;
    private TextView applyReturnBtn;
    private MongoTrade trade;
    private boolean isSuccessed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_return);

        findViewById(R.id.return_back_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        trade = (MongoTrade) getIntent().getSerializableExtra(TRADE_ENTITY);

        String returnInformation = FileUtil.readAssets(this, "returnInformation.json");
        JSONObject jsonObject = new JSONObject();
        Gson gson = new Gson();
        returnInformationModel = gson.fromJson(returnInformation, new TypeToken<ReturnInformationModel>(){}.getType());

        ((TextView)findViewById(R.id.address_return_activity)).setText(returnInformationModel.address);
        ((TextView)findViewById(R.id.receiver_return_activity)).setText(returnInformationModel.receiver);
        ((TextView)findViewById(R.id.phone_return_activity)).setText(returnInformationModel.phone);
        ((TextView)findViewById(R.id.matters_return_activity)).setText(returnInformationModel.matters);

        company = (EditText) findViewById(R.id.company_return_activity);
        returnNo = (EditText) findViewById(R.id.returnNumber__return_activity);

        applyReturnBtn = (TextView) findViewById(R.id.apply_return_btn_return_activity);

        applyReturnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitForm();
            }
        });

    }

    public void commitForm(){

        if(TextUtils.isEmpty(company.getText().toString())){
            Toast.makeText(U12ReturnActivity.this, "请填写货运公司", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(returnNo.getText().toString())){
            Toast.makeText(U12ReturnActivity.this, "请填写货运单号", Toast.LENGTH_SHORT).show();
            return;
        }

        final LoadingDialog loadingDialog = new LoadingDialog(getSupportFragmentManager());
        loadingDialog.show(U12ReturnActivity.class.getSimpleName());
        Map params = new HashMap();
        Map returnLogistic = new HashMap();

        params.put("_id", trade._id);
        params.put("status", 7);



        returnLogistic.put("company", company.getText().toString());
        returnLogistic.put("trackingID", returnNo.getText().toString());
        params.put("returnLogistic", returnLogistic);

        QSJsonObjectRequest jor2 = new QSJsonObjectRequest(QSAppWebAPI.getTradeStatustoApi(), new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("response:" + response);
                loadingDialog.dismiss();
                if(MetadataParser.hasError(response)){
                    ErrorHandler.handle(U12ReturnActivity.this, MetadataParser.getError(response));
                    isSuccessed = false;
                    return;
                }
                if(isSuccessed){
                    EventBus.getDefault().post(TradeParser.parseQuery(response));
                    Toast.makeText(U12ReturnActivity.this, getResources().getString(R.string.toast_activity_return), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jor2);
    }

    @Override
    public void reconn() {

    }
}

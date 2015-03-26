package com.focosee.qingshow.activity;

import android.os.Bundle;
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
    private TextView returnDate;
    private LinearLayout returnDateLayout;
    private TextView applyReturnBtn;
    private CalendarPickerView calendarPickerView;
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
        returnDate = (TextView) findViewById(R.id.returnDate__return_activity);
        calendarPickerView = (CalendarPickerView) findViewById(R.id.calendar_view_return_activity);
        findViewById(R.id.returnDate_layout_return_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarPickerView.setVisibility(View.VISIBLE);
                Calendar nextYear = Calendar.getInstance();
                nextYear.add(Calendar.YEAR, 1);
                CalendarPickerView calendar = (CalendarPickerView) findViewById(R.id.calendar_view_return_activity);
                Date today = new Date();
                Date minDay = new Date();
                minDay.setTime(today.getTime() - 4 * weekTime);
                calendar.init(minDay, nextYear.getTime())
                        .withSelectedDate(today);
                calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(Date date) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        returnDate.setText(dateFormat.format(date));
                        calendarPickerView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onDateUnselected(Date date) {

                    }
                });
            }
        });

        applyReturnBtn = (TextView) findViewById(R.id.apply_return_btn_return_activity);

        applyReturnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitForm();
            }
        });

    }

    public void commitForm(){

        if(company.getText().length() == 0 || null == company.getText()){
            Toast.makeText(U12ReturnActivity.this, "请填写货运公司", Toast.LENGTH_SHORT).show();
            return;
        }

        if(company.getText().length() == 0 || null == company.getText()){
            Toast.makeText(U12ReturnActivity.this, "请填写货运单号", Toast.LENGTH_SHORT).show();
            return;
        }

        Map params1 = new HashMap();

        params1.put("_id", trade._id);
        params1.put("status", 6);

        QSJsonObjectRequest jor1 = new QSJsonObjectRequest(QSAppWebAPI.getTradeStatustoApi(), new JSONObject(params1), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(MetadataParser.hasError(response)){
                    ErrorHandler.handle(U12ReturnActivity.this, MetadataParser.getError(response));
                    isSuccessed = false;
                    return;
                }
                isSuccessed = true;
            }
        });

        Map params2 = new HashMap();

        params2.put("_id", trade._id);
        params2.put("status", 7);
        params2.put("returnLogistic.company", company.getText().toString());
        params2.put("returnLogistic.trackingID", returnNo.getText().toString());

        QSJsonObjectRequest jor2 = new QSJsonObjectRequest(QSAppWebAPI.getTradeStatustoApi(), new JSONObject(params2), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(MetadataParser.hasError(response)){
                    System.out.println(response);
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

        RequestQueueManager.INSTANCE.getQueue().add(jor1);
        RequestQueueManager.INSTANCE.getQueue().add(jor2);

    }

    @Override
    public void reconn() {

    }
}

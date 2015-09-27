package com.focosee.qingshow.util;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.focosee.qingshow.R;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.widget.QSButton;
import com.squareup.okhttp.Call;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.validation.ValidatorHandler;

/**
 * Created by Administrator on 2015/9/11.
 */
public class VerificationHelper {

    private QSButton veri_btn;
    private Context context;
    private int color = Integer.MAX_VALUE;

    public static void validateMobile(Map params, final Callback callback){

        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getValidateMobileApi()
                , new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(ValidatorHandler.class.getSimpleName(), "validate_response:" + response);
                callback.onComplete(response);
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    public void getVerification(String mobileNumber, QSButton veri_btn, final Context context){
        veri_btn.setEnabled(false);
        if(color == Integer.MAX_VALUE)
            color = veri_btn.getCurrentTextColor();
        veri_btn.setTextColor(context.getResources().getColor(R.color.gary));
        this.veri_btn = veri_btn;
        this.context = context;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new VerificationTime(), 0, 1000);

        Map<String, String> params = new HashMap<>();
        params.put("mobile", mobileNumber);

        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getRequestVerificationCodeApi()
                , new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(VerificationHelper.class.getSimpleName(), "response:" + response);

                if(MetadataParser.hasError(response)){
                    ErrorHandler.handle(context, MetadataParser.getError(response));
                    return;
                }
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 0) {
                veri_btn.setText(context.getText(R.string.send_verification_activity_register));
                veri_btn.setTextColor(color);
                veri_btn.setEnabled(true);
                return;
            }
            veri_btn.setText(msg.arg1 + "秒后可重发");
        }
    };

    class VerificationTime extends TimerTask {

        private int time = 59;

        @Override
        public void run() {
            if (time == -1) {
                cancel();
                return;
            }
            Message msg = new Message();
            msg.arg1 = time;
            handler.sendMessage(msg);
            time--;
        }
    }

}

package com.focosee.qingshow.util;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.focosee.qingshow.R;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.widget.QSButton;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2015/9/11.
 */
public class VerificationHelper {

    private QSButton veri_btn;
    private Context context;
    private int color;

    public void getVerification(String mobileNumber, QSButton veri_btn, Context context){
        veri_btn.setEnabled(false);
        veri_btn.setTextColor(context.getResources().getColor(R.color.gary));
        color = veri_btn.getCurrentTextColor();
        this.veri_btn = veri_btn;
        this.context = context;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new VerificationTime(), 0, 1000);

        Map<String, String> params = new HashMap<>();
        params.put("mobileNumber", mobileNumber);

        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getRequestVerificationCodeApi()
                , new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(VerificationHelper.class.getSimpleName(), "response:" + response);
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Integer.MAX_VALUE, 0, 1f));
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
            veri_btn.setText(msg.arg1 + "S后重新发送");
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

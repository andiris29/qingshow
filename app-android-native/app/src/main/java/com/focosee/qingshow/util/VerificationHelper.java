package com.focosee.qingshow.util;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
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

    private QSButton veriBtn;
    private Context context;
    private int color = Integer.MAX_VALUE;

    public void getVerification(String mobileNumber, final QSButton veri_btn, final Context cont){


        Map<String, String> params = new HashMap<>();
        params.put("mobile", mobileNumber);

        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getRequestVerificationCodeApi()
                , new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(VerificationHelper.class.getSimpleName(), "response:" + response);

                if(MetadataParser.hasError(response)){
                    ErrorHandler.handle(QSApplication.instance(), MetadataParser.getError(response));
                    return;
                }

                veri_btn.setEnabled(false);
                if(color == Integer.MAX_VALUE)
                    color = veri_btn.getCurrentTextColor();
                veri_btn.setTextColor(cont.getResources().getColor(R.color.gary));
                veriBtn = veri_btn;
                context = cont;
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new VerificationTime(), 0, 1000);
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 0) {
                veriBtn.setText(context.getText(R.string.send_verification_activity_register));
                veriBtn.setTextColor(color);
                veriBtn.setEnabled(true);
                return;
            }
            veriBtn.setText(msg.arg1 + "秒后可重发");
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

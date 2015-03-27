package com.focosee.qingshow.command;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.android.volley.Request;
import com.android.volley.Response;
import com.focosee.qingshow.constants.config.PaymentConfig;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.util.payment.Alipay.AlipayUtil;
import com.focosee.qingshow.util.payment.Alipay.SignUtils;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Administrator on 2015/3/25.
 */
public class PayCommand {


    public static void alipay(final MongoTrade trade, final Context context, final Callback callback) {

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (TextUtils.isEmpty(msg.obj.toString())) callback.onError();
                callback.onComplete();
            }
        };

        String orderInfo = AlipayUtil.getOrderInfo(trade.orders.get(0).itemSnapshot.name,
                trade.orders.get(0).itemSnapshot.source, "0.01", trade._id);

        String sign = SignUtils.sign(orderInfo, PaymentConfig.RSA_PRIVATE);
        try {
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
                + "sign_type=\"RSA\"";

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask((Activity) context);
                String result = alipay.pay(payInfo);
                Message msg = new Message();
                msg.obj = result;
                handler.sendMessage(msg);
            }
        };
        Thread payThread = new Thread(payRunnable);
        payThread.start();

    }
}

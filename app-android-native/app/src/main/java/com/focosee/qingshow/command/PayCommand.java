package com.focosee.qingshow.command;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alipay.sdk.app.PayTask;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.constants.config.PaymentConfig;
import com.focosee.qingshow.constants.config.ShareConfig;
import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.util.MD5;
import com.focosee.qingshow.util.payment.Alipay.AlipayUtil;
import com.focosee.qingshow.util.payment.Alipay.PayResult;
import com.focosee.qingshow.util.payment.Alipay.SignUtils;
import com.tencent.mm.sdk.modelpay.PayReq;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PayCommand {


    public static void alipay(final MongoTrade trade, final Context context, final Callback callback) {

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.d(PayCommand.class.getSimpleName(), "msg:" + msg.obj.toString());
                if (!Boolean.parseBoolean(msg.obj.toString())) {
                    callback.onError();
                    return;
                }
                callback.onComplete();
            }
        };

        String orderInfo = AlipayUtil.getOrderInfo(trade.itemSnapshot.name,
                trade.itemSnapshot.source, String.valueOf(trade.totalFee), trade._id);

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
                PayResult payResult = new PayResult(result);
                Message msg = new Message();
                msg.obj = payResult.isSuccessed();
                Log.d(PayCommand.class.getSimpleName(), "resultStatus:" + payResult.getResultStatus());
                handler.sendMessage(msg);
            }
        };
        Thread payThread = new Thread(payRunnable);
        payThread.start();

    }

    public static void weixin(final MongoTrade trade){
        String nonceStr = genOutTradNo();
        PayReq request = new PayReq();
        request.appId = ShareConfig.APP_ID;
        request.partnerId = PaymentConfig.WEIXIN_PARTNER;
        request.prepayId = trade.pay.weixin.prepayid;
        request.nonceStr = nonceStr;
        request.timeStamp =String.valueOf(System.currentTimeMillis()/1000);
        request.packageValue = "Sign=WXpay";

        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put("appid",request.appId);
        map.put("partnerid",request.partnerId);
        map.put("prepayid",request.prepayId);
        map.put("noncestr",request.nonceStr);
        map.put("timestamp",request.timeStamp);
        map.put("package",request.packageValue);

        request.sign = getSign(map);
        QSApplication.instance().getWxApi().sendReq(request);
    }

    public static String getSign(Map<String,Object> map){
        ArrayList<String> list = new ArrayList<String>();
        for(Map.Entry<String,Object> entry:map.entrySet()){
            if(entry.getValue()!=""){
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }
        int size = list.size();
        String [] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < size; i ++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        result += "key=" + "1qaz2wsx3edc4rfv1234qwerasdfzxcv";
        result = MD5.MD5Encode(result).toUpperCase();
        return result;
    }

    public static String genOutTradNo() {
        Random random = new Random();
        return MD5.MD5Encode(String.valueOf(random.nextInt(10000)));
    }

}

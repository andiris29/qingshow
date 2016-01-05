package com.focosee.qingshow.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.constants.config.ShareConfig;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.SharedObjectParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoSharedObjects;
import com.focosee.qingshow.persist.SinaAccessTokenKeeper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/7/29.
 */
public class ShareUtil {

    private final static int TYPE_SHOW = 0;
    private final static int TYPE_TRADE = 1;
    private final static int TYPE_BONUS = 2;

    public static void shareShowToWX(final String showId, final String transaction, final Context context, final boolean isTimelineCb) {
        getShareObject(TYPE_SHOW, showId, context, new Callback() {
            @Override
            public void onComplete(MongoSharedObjects sharedObjects) {
                shareToWX(sharedObjects.url + "?_id=" + sharedObjects._id, transaction, context, isTimelineCb
                        , ShareConfig.IMG, sharedObjects.title, sharedObjects.description);
            }
        });
    }

    public static void shareTradeToWX(final String tradeId, final String transaction, final Context context, final boolean isTimelineCb) {
        getShareObject(TYPE_TRADE, tradeId, context, new Callback() {
            @Override
            public void onComplete(MongoSharedObjects sharedObjects) {
                shareToWX(sharedObjects.url + "?_id=" + sharedObjects._id, transaction, context, isTimelineCb
                        , ShareConfig.IMG, sharedObjects.title, sharedObjects.description);
            }
        });
    }

    public static void shareBonusToWX(final Context context) {
        getShareObject(TYPE_BONUS, QSModel.INSTANCE.getUserId(), context, new Callback() {
            @Override
            public void onComplete(MongoSharedObjects sharedObjects) {
                Log.e("test_i","sharedObjects ---> "+sharedObjects.toString());
                shareToWX(sharedObjects.url + "?_id=" + sharedObjects._id, ValueUtil.SHARE_BONUS, context, true
                        , ShareConfig.IMG, sharedObjects.title, sharedObjects.description);
            }
        });
    }

    private static void getShareObject(int type, String _id, final Context context, final Callback callback) {
                Log.e("test_i","-------------------");
        String url;

        switch (type){
            case TYPE_SHOW:
                url = QSAppWebAPI.getShareCreateShowApi();
                break;
            case TYPE_TRADE:
                url = QSAppWebAPI.getShareCreateTradeApi();
                break;
            case TYPE_BONUS:
                url = QSAppWebAPI.getShareCreateBonusApi();
                break;
            default:
                url = QSAppWebAPI.getShareCreateShowApi();
                break;
        }

        Map<String, String> params = new HashMap<>();
        params.put("_id", _id);

        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e(ShareUtil.class.getSimpleName(), "getShareObject-response:" + response.toString());
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(context, MetadataParser.getError(response));
                    return;
                }
                MongoSharedObjects objects = SharedObjectParser.parseSharedObject(response);
                callback.onComplete(objects);
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    public static void shareToWX(String url, String transaction, Context context, boolean isTimelineCb, int img, String title, String description) {
        WXWebpageObject webpage = new WXWebpageObject();
        WXMediaMessage msg;
        webpage.webpageUrl = url;
        msg = new WXMediaMessage();
        msg.mediaObject = webpage;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), img);
        msg.thumbData = BitMapUtil.bmpToByteArray(bitmap, false, Bitmap.CompressFormat.PNG);
        msg.setThumbImage(bitmap);
        msg.title = title;
        msg.description = description;
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = transaction;
        req.message = msg;
        req.scene = isTimelineCb ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        UmengCountUtil.countShareShow(context, "weixin");
        QSApplication.instance().getWxApi().sendReq(req);
    }
}

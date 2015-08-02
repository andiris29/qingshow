package com.focosee.qingshow.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.constants.config.ShareConfig;
import com.focosee.qingshow.persist.SinaAccessTokenKeeper;
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

/**
 * Created by Administrator on 2015/7/29.
 */
public class ShareUtil {

    public static void shareShowToWX(String showId, String transaction, Context context, boolean isTimelineCb){
        shareToWX(ShareConfig.SHARE_SHOW_URL + showId, transaction, context, isTimelineCb);
    }

    public static void shareTradeToWX(String showId, String peopleId, String transaction, Context context, boolean isTimelineCb){
        shareToWX(ShareConfig.getShareTradeUrl(showId, peopleId), transaction, context, isTimelineCb);
    }

    public static void shareToWX(String url, String transaction, Context context, boolean isTimelineCb){
        WXWebpageObject webpage = new WXWebpageObject();
        WXMediaMessage msg;
        webpage.webpageUrl = url;

        msg = new WXMediaMessage();
        msg.mediaObject = webpage;
        Bitmap thumb = BitmapFactory.decodeResource(context.getResources(), ShareConfig.IMG);
        msg.thumbData = BitMapUtil.bmpToByteArray(thumb, false, Bitmap.CompressFormat.PNG);
        msg.setThumbImage(thumb);
        msg.title = ShareConfig.SHARE_TITLE;
        msg.description = ShareConfig.SHARE_DESCRIPTION;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = transaction;
        req.message = msg;
        req.scene = isTimelineCb ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        UmengCountUtil.countShareShow(context, "weixin");
        QSApplication.instance().getWxApi().sendReq(req);
    }

    public static void shareShowToSina(String showId, final Context context, IWeiboShareAPI weiboShareAPI) {

        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = ShareConfig.SHARE_TITLE;
        mediaObject.description = ShareConfig.SHARE_DESCRIPTION;
        mediaObject.setThumbImage(BitmapFactory.decodeResource(context.getResources(), ShareConfig.IMG));
        mediaObject.actionUrl = ShareConfig.SHARE_SHOW_URL + showId;
        mediaObject.defaultText = ShareConfig.SHARE_DESCRIPTION;

        weiboMessage.mediaObject = mediaObject;

        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        AuthInfo authInfo = new AuthInfo(context, ShareConfig.SINA_APP_KEY, ShareConfig.SINA_REDIRECT_URL, ShareConfig.SCOPE);
        Oauth2AccessToken accessToken = SinaAccessTokenKeeper.readAccessToken(context);
        String token = "";
        if (accessToken != null) {
            token = accessToken.getToken();
        }
        weiboShareAPI.sendRequest((Activity)context, request, authInfo, token, new WeiboAuthListener() {

            @Override
            public void onWeiboException(WeiboException arg0) {
            }

            @Override
            public void onComplete(Bundle bundle) {
                Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                SinaAccessTokenKeeper.writeAccessToken(context, newToken);
            }

            @Override
            public void onCancel() {
            }
        });

    }



}

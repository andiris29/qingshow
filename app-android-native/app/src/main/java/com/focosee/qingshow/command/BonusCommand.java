package com.focosee.qingshow.command;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.focosee.qingshow.R;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.util.ToastUtil;
import com.focosee.qingshow.widget.ConfirmDialog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/2.
 */
public class BonusCommand {

    public static void bonusWithDraw(String alipayId, final Context context, final Callback callback){

        Map<String, String> params = new HashMap<>();
        params.put("alipayId", alipayId);


        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST
                , QSAppWebAPI.getUserBonusWithdrawApi(), new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(BonusCommand.class.getSimpleName(), "response:" + response);

                callback.onComplete();
                if (MetadataParser.hasError(response)) {
                    ToastUtil.showShortToast(context.getApplicationContext(), "提现失败，请重试");
                    return;
                }
                UserCommand.refresh();
                final ConfirmDialog dialog = new ConfirmDialog(context);
                dialog.setTitle(context.getString(R.string.bonus_share_successed_hint));
                dialog.setConfirm(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        ((Activity)context).finish();
                    }
                });
                dialog.show();
                dialog.hideCancel();
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);

    }

}

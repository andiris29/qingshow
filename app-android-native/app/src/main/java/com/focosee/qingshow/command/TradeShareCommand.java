package com.focosee.qingshow.command;

import com.android.volley.Request;
import com.android.volley.Response;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/9/21.
 */
public class TradeShareCommand {

    public static void share(String _id, final Callback callback){

        Map<String, String> params = new HashMap<>();
        params.put("_id", _id);
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getTradeShareApi(), new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (MetadataParser.hasError(response)) {
                    callback.onError(MetadataParser.getError(response));
                    return;
                }
                callback.onComplete();

            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }
}

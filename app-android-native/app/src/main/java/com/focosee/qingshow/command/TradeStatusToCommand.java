package com.focosee.qingshow.command;

import com.android.volley.Request;
import com.android.volley.Response;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;

import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Administrator on 2015/9/21.
 */
public class TradeStatusToCommand {
    public static void statusTo(MongoTrade trade, int status, String actualPrice, final Callback callback){
        Map<String, Object> prarms = new TreeMap<>();
        LinkedList<MongoTrade.StatusLog> statusLogs = trade.statusLogs;
        prarms.put("_id", trade._id);
        prarms.put("status", status);
        prarms.put("comment", statusLogs.get(statusLogs.size() - 1));
        prarms.put("actualPrice", actualPrice);
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getTradeStatustoApi(), new JSONObject(prarms), new Response.Listener<JSONObject>() {
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

package com.focosee.qingshow.command;

import com.android.volley.Request;
import com.android.volley.Response;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/9/21.
 */
public class TradeStatusToCommand {

    public static void statusTo(MongoTrade trade, int status, final Callback callback){

        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getTradeStatustoApi(), getStatusJSONObjcet(trade, status), new Response.Listener<JSONObject>() {
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

    private static JSONObject getStatusJSONObjcet(MongoTrade trade, int status) {

        Map params = new HashMap();
        Map taobaoInfo = new HashMap();
        Map logistic = new HashMap();
        params.put("_id", trade._id);
        params.put("status", status);
        params.put("comment", (trade.statusLogs.get(trade.statusLogs.size() - 1)).comment);

        switch (status) {
            case 1:
                taobaoInfo.put("actualPrice", trade.totalFee);
                break;
            case 3:
                logistic.put("company", trade.logistic.company);
                logistic.put("trackingID", trade.logistic.trackingId);
                break;
            case 7:
                logistic.put("company", trade.returnlogistic.company);
                logistic.put("trackingID", trade.returnlogistic.trackingID);
                break;
        }

        return new JSONObject(params);

    }
}

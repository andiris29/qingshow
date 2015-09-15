package com.focosee.qingshow.command;

import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.TradeParser;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import org.json.JSONObject;
import java.util.HashMap;

/**
 * Created by Administrator on 2015/3/25.
 */
public class TradeRefreshCommand {
    public static void refresh(String id, final Callback callback) {
        HashMap params = new HashMap();
        params.put("_id", id);
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getTradeRefreshApi()
                , new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TradeRefreshCommand.class.getSimpleName(), "response:" + response);
                if (MetadataParser.hasError(response)) {
                    callback.onError(MetadataParser.getError(response));
                }
                MongoTrade trade = TradeParser.parse(response);
                if (null != trade) {
                    callback.onComplete(trade.status);
                }
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }
}

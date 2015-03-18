package com.focosee.qingshow.command;

import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Administrator on 2015/3/18.
 */
public class UserReceiverCommand {

    public static void saveReceiver(Map params){
        saveReceiver(params,new Callback());
    }

    public static void saveReceiver(Map params,final Callback callback){
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getUserSaveReceiverApi(), new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(MetadataParser.hasError(response)){
                    callback.onError();
                    return;
                }
                callback.onComplete(response);
                UserCommand.refresh();
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }
}

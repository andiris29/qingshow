package com.focosee.qingshow.command;


import android.content.Intent;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSStringRequest;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.error.ErrorCode;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.U01Model;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.dataparser.UserParser;
import com.focosee.qingshow.model.QSModel;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by i068020 on 2/24/15.
 */
public class UserCommand {
    public static void refresh() {
        refresh(new Callback());
    }

    public static void refresh(final Callback callback) {
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.GET, QSAppWebAPI.getUserApi(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MongoPeople user = UserParser.parseGet(response);
                QSModel.INSTANCE.setUser(user);

                callback.onComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError();
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    public static void update(Map params,final Callback callback){
        QSStringRequest qxStringRequest = new QSStringRequest(params, Request.Method.POST, QSAppWebAPI.UPDATE_SERVICE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MongoPeople user = UserParser.parseUpdate(response);
                if (user == null) {
                    callback.onError(MetadataParser.getError(response));
                } else {
                    QSModel.INSTANCE.setUser(user);
                    callback.onComplete();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(ErrorCode.NoNetWork);
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(qxStringRequest);
    }

    public static void likeOrFollow(String url, String _id, final Callback callback){
        Map<String, String> likeData = new HashMap<>();
        likeData.put("_id", _id);
        JSONObject jsonObject = new JSONObject(likeData);

        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (!MetadataParser.hasError(response)) {
                    callback.onComplete(response);
                    UserCommand.refresh();
                } else {
                    callback.onError(MetadataParser.getError(response));
                }
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }
}

package com.focosee.qingshow.command;


import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.activity.UserUpdatedEvent;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.error.ErrorCode;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.dataparser.UserParser;
import com.focosee.qingshow.model.QSModel;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import de.greenrobot.event.EventBus;

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
                Log.d(UserCommand.class.getSimpleName(), "refresh-response:" + response);
                callback.onComplete();
                MongoPeople user = UserParser.parseGet(response);
                if(null != user)
                    QSModel.INSTANCE.setUser(user);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(UserCommand.class.getSimpleName(), "error");
                callback.onError();
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    public static void update(Map params,final Callback callback){
        update(new JSONObject(params), callback);
    }

    public static void update(JSONObject jsonObject,final Callback callback){
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getUpdateServiceUrl(), jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MongoPeople user = UserParser._parsePeople(response);
                if (user == null) {
                    callback.onError(MetadataParser.getError(response));
                } else {
                    QSModel.INSTANCE.setUser(user);
                    EventBus.getDefault().post(new UserUpdatedEvent(user));
                    callback.onComplete();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(ErrorCode.NoNetWork);
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
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

    public static void getPeople(final Callback callback, final Context context, String _id){

        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getPeopleQueryApi(_id), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (!MetadataParser.hasError(response)) {
                    callback.onComplete(response);
                } else {
                    ErrorHandler.handle(context, MetadataParser.getError(response));
                }
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);

    }

    public static void readExpectableTrade(String _id, final Context context, final Callback callback){

        Map<String, String> params = new HashMap<>();
        params.put("_id", _id);
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getReadExpectableTradeApi()
                , new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(MetadataParser.hasError(response)){
                    ErrorHandler.handle(context, MetadataParser.getError(response));
                    return;
                }
                UserCommand.refresh();
                callback.onComplete();
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }
}

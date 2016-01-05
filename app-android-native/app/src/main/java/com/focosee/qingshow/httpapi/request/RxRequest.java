package com.focosee.qingshow.httpapi.request;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.error.ErrorCode;

import org.json.JSONObject;

import java.util.HashMap;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Administrator on 2015/11/17.
 */
public class RxRequest {

    public static Observable<JSONObject> createJsonRequest(final int method, final String url, final JSONObject jsonRequest){
        Log.e("test_i","------>  "+url);
        if (null != jsonRequest){
            Log.e("test_i","jsonRequest---> "+jsonRequest.toString());
        }
        return Observable.create(new Observable.OnSubscribe<JSONObject>() {
            @Override
            public void call(final Subscriber<? super JSONObject> subscriber) {
                QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(method, url, jsonRequest, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (MetadataParser.hasError(response)){
                            //errorCode became the msg
                            Throwable errorCode = new Throwable(String.valueOf(MetadataParser.getError(response)));
                            subscriber.onError(errorCode);
                            subscriber.onCompleted();
                        }
                        Log.e("test_i" ,"response.toString() --> "+ response.toString());
                        subscriber.onNext(response);
                        subscriber.onCompleted();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Throwable errorCode = new Throwable("8888", error);
                        subscriber.onError(errorCode);
                        subscriber.onCompleted();
                    }
                });
                RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
            }
        });
    }

    public static Observable<JSONObject> createIdRequest(final String url, final String _id){
        HashMap<String, String> map = new HashMap<>();
        map.put("_id", _id);
        return createJsonRequest(Request.Method.POST, url, new JSONObject(map));
    }
}

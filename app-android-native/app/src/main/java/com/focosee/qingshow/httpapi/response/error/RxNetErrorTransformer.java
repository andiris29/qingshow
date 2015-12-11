package com.focosee.qingshow.httpapi.response.error;

import org.json.JSONObject;

import rx.Observable;

/**
 * Created by Administrator on 2015/11/17.
 */
public class RxNetErrorTransformer implements Observable.Transformer<JSONObject, JSONObject> {
    @Override
    public Observable<JSONObject> call(Observable<JSONObject> jsonObjectObservable) {
        return null;
    }
}

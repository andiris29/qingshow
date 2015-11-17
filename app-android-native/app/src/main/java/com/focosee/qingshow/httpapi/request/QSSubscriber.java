package com.focosee.qingshow.httpapi.request;

import com.focosee.qingshow.httpapi.response.MetadataParser;

import org.json.JSONObject;

import rx.Subscriber;

/**
 * Created by Administrator on 2015/11/17.
 */
public abstract class QSSubscriber<T> extends Subscriber<T> {
    @Override
    public void onCompleted(){}

    @Override
    public void onError(Throwable e) {
        onNetError(Integer.parseInt(e.getMessage()));
    }

    @Override
    public void onNext(T t){}

    abstract public void onNetError(int message);
}

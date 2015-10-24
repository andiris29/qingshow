package com.focosee.qingshow.command;

import com.focosee.qingshow.model.vo.mongo.MongoSharedObjects;

import org.json.JSONObject;

/**
 * Created by i068020 on 2/24/15.
 */
public class Callback {
    public void onComplete() {

    }

    public void onComplete(JSONObject response) {

    }

    public void onComplete(String result) {

    }

    public void onComplete(int result) {

    }

    public void onComplete(MongoSharedObjects sharedObjects){

    }

    public void onError() {

    }

    public void onError(int errorCode) {

    }
}

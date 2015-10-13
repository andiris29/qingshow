package com.focosee.qingshow.httpapi.response.dataparser;

import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.model.vo.mongo.MongoSharedObjects;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by i068020 on 2/7/15.
 */
public class SharedObjectParser {

    public static MongoSharedObjects parseSharedObject(JSONObject response) {
        try {
            Gson gson = QSGsonFactory.create();

            String sharedObjects = response.getJSONObject("data").getJSONObject("sharedObject").toString();
            return gson.fromJson(sharedObjects, new TypeToken<MongoSharedObjects>() {
            }.getType());
        } catch (JSONException e) {
            return null;
        }
    }
}

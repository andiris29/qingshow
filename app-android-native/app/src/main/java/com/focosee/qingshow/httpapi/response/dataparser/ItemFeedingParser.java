package com.focosee.qingshow.httpapi.response.dataparser;

import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by i068020 on 2/8/15.
 */
public class ItemFeedingParser {
    // Public interface
    public static ArrayList<MongoItem> parse(JSONObject response) {
        try {
            String items = response.getJSONObject("data").getJSONArray("items").toString();
            Type listType = new TypeToken<ArrayList<MongoItem>>() {
            }.getType();
            Gson gson = QSGsonFactory.create();
            return gson.fromJson(items, listType);
        } catch (JSONException e) {
            return null;
        }
    }

    public static MongoItem parseOne(JSONObject response) {
        try {
            String item = response.getJSONObject("data").getJSONObject("item").toString();
            return QSGsonFactory.create().fromJson(item, new TypeToken<MongoItem>() {
            }.getType());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

}

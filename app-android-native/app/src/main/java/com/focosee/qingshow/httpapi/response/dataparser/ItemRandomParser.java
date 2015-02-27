package com.focosee.qingshow.httpapi.response.dataparser;

import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.LinkedList;

/**
 * Created by Administrator on 2015/2/12.
 */
public class ItemRandomParser {

    public static LinkedList<MongoItem> parse(JSONObject response) {
        try {
            String items = response.getJSONObject("data").getJSONArray("items").toString();
            Type listType = new TypeToken<LinkedList<MongoItem>>() {
            }.getType();
            Gson gson = QSGsonFactory.create();
            return gson.fromJson(items, listType);
        } catch (JSONException e) {
            return null;
        }
    }
}

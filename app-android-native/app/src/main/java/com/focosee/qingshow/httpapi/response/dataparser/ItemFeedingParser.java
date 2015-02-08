package com.focosee.qingshow.httpapi.response.dataparser;

import com.focosee.qingshow.entity.mongo.MongoItem;
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
            Gson gson = new Gson();
            return gson.fromJson(items, listType);
        } catch (JSONException e) {
            return null;
        }
    }

}

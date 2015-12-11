package com.focosee.qingshow.httpapi.response.dataparser;

import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.model.vo.mongo.MongoCategories;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/6/23.
 */
public class CategoryParser {
    public static ArrayList<MongoCategories> parseQuery(JSONObject response) {
        try {
            String trades = response.getJSONObject("data").getJSONArray("categories").toString();
            Gson gson = QSGsonFactory.create();
            return gson.fromJson(trades, new TypeToken<ArrayList<MongoCategories>>() {
            }.getType());
        } catch (JSONException e) {
            return null;
        }
    }
}

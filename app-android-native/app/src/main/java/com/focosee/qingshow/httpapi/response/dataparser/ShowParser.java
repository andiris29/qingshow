package com.focosee.qingshow.httpapi.response.dataparser;

import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.httpapi.gson.deserializer.MongoItemIdDeserializer;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by i068020 on 2/8/15.
 */
public class ShowParser {
    public static LinkedList<MongoShow> parseQuery(JSONObject response) {
        try {
            String shows = response.getJSONObject("data").getJSONArray("shows").toString();
            Gson gson = QSGsonFactory.create();
            return gson.fromJson(shows, new TypeToken<LinkedList<MongoShow>>() {
            }.getType());
        } catch (JSONException e) {
            return null;
        }
    }

    public static MongoShow parse(JSONObject response) {
        try {
            String show = response.getJSONObject("data").getJSONObject("show").toString();
            Gson gson = QSGsonFactory.create();
            return gson.fromJson(show, new TypeToken<MongoShow>() {
            }.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}

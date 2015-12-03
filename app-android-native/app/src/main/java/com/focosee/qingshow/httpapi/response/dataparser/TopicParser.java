package com.focosee.qingshow.httpapi.response.dataparser;

import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;


/**
 * Created by Administrator on 2015/3/31.
 */
public class TopicParser {
    public static LinkedList<MongoShow> parseQuery(JSONObject response) {
        try {
            String shows = response.getJSONObject("data").getJSONArray("topics").toString();
            Gson gson = QSGsonFactory.create();
            return gson.fromJson(shows, new TypeToken<LinkedList<MongoShow>>() {
            }.getType());
        } catch (JSONException e) {
            return null;
        }
    }
}

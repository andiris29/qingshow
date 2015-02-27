package com.focosee.qingshow.httpapi.response.dataparser;

import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.httpapi.gson.deserializer.MongoItemIdDeserializer;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by i068020 on 2/7/15.
 */
public class FeedingParser {

    public static LinkedList<MongoShow> parse(JSONObject response) {
        try {
            GsonBuilder builder = QSGsonFactory.createBuilder();
            builder.registerTypeAdapter(MongoItem.class, new MongoItemIdDeserializer());

            String shows = response.getJSONObject("data").getJSONArray("shows").toString();
            return builder.create().fromJson(shows, new TypeToken<LinkedList<MongoShow>>() {
            }.getType());
        } catch (JSONException e) {
            return null;
        }
    }

}

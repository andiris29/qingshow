package com.focosee.qingshow.httpapi.response.dataparser;

import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.model.vo.mongo.MongoPreview;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.LinkedList;

/**
 * Created by i068020 on 2/8/15.
 */
public class PreviewParser {
    public static LinkedList<MongoPreview> parseFeed(JSONObject response) {
        try {
            String previews = response.getJSONObject("data").getJSONArray("previews").toString();
            Type typeList = new TypeToken<LinkedList<MongoPreview>>() {
            }.getType();
            Gson gson = QSGsonFactory.create();
            return gson.fromJson(previews, typeList);
        } catch (Exception e) {
            return null;
        }
    }
}

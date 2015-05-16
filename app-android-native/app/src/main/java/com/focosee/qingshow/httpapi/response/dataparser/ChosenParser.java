package com.focosee.qingshow.httpapi.response.dataparser;


import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.httpapi.gson.deserializer.ChosenPreviewDeserializer;
import com.focosee.qingshow.httpapi.gson.deserializer.ChosenShowDeserializer;
import com.focosee.qingshow.model.vo.mongo.MongoChosen;
import com.focosee.qingshow.model.vo.mongo.MongoPreview;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by DylanJiang on 15/5/6.
 */
public class ChosenParser {

    public static ArrayList<MongoChosen> parse(JSONObject response){

        GsonBuilder builder = QSGsonFactory.createBuilder();
        builder.registerTypeAdapter(MongoShow.class,new ChosenShowDeserializer());
        builder.registerTypeAdapter(MongoPreview.class,new ChosenPreviewDeserializer());

        String chosen = null;
        try {
            chosen = response.getJSONObject("data").getJSONArray("chosens").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return builder.create().fromJson(chosen, new TypeToken<ArrayList<MongoChosen>>() {
        }.getType());

    }
}


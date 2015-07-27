package com.focosee.qingshow.httpapi.response.dataparser;


import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.model.vo.mongo.IMongoChosen;
import com.focosee.qingshow.model.vo.mongo.MongoChosenPreview;
import com.focosee.qingshow.model.vo.mongo.MongoChosenShow;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by DylanJiang on 15/5/6.
 */
public class ChosenParser {

    public static LinkedList<IMongoChosen> parse(JSONObject response){

        LinkedList<IMongoChosen> data = new LinkedList<IMongoChosen>();

        GsonBuilder builder = QSGsonFactory.createBuilder();

        JSONArray chosen = null;
        try {
            chosen = response.getJSONObject("data").getJSONArray("chosens");
            for (int i = 0; i < chosen.length(); i++) {
                JSONObject item = chosen.getJSONObject(i);
                String type = item.getString("refCollection");
                if (type.equals("shows")){
                    data.add((IMongoChosen) builder.create().fromJson(item.toString(),new TypeToken<MongoChosenShow>(){}.getType()));
                }
                if (type.equals("previews")){
                    data.add((IMongoChosen) builder.create().fromJson(item.toString(),new TypeToken<MongoChosenPreview>(){}.getType()));
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;

    }
}


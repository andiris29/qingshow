package com.focosee.qingshow.httpapi.response.dataparser;

import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.model.vo.mongo.MongoBonus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/12/2.
 */
public class BonusParser {
    public static ArrayList<MongoBonus> parseQuery(JSONObject response) {
        try {
            String bonuses = response.getJSONObject("data").getJSONArray("bonuses").toString();
            Gson gson = QSGsonFactory.create();
            return gson.fromJson(bonuses, new TypeToken<ArrayList<MongoBonus>>() {
            }.getType());
        } catch (JSONException e) {
            return null;
        }
    }
}

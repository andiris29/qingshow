package com.focosee.qingshow.httpapi.response.dataparser;

import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by i068020 on 2/7/15.
 */
public class PeopleParser {
    // Public interface
    public static ArrayList<MongoPeople> parseQuery(JSONObject response) {
        return _parsePeoples(response);
    }

    public static ArrayList<MongoPeople> parseQueryFollowers(JSONObject response) {
        return _parsePeoples(response);
    }

    private static ArrayList<MongoPeople> _parsePeoples(JSONObject response) {
        try {
            Gson gson = QSGsonFactory.create();

            String peoples = response.getJSONObject("data").getJSONArray("peoples").toString();
            return gson.fromJson(peoples, new TypeToken<ArrayList<MongoPeople>>() {
            }.getType());
        } catch (JSONException e) {
            return null;
        }
    }
}

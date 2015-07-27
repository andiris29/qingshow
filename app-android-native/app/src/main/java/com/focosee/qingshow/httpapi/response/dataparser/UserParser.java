package com.focosee.qingshow.httpapi.response.dataparser;

import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.LinkedList;

/**
 * Created by i068020 on 2/8/15.
 */
public class UserParser {

    public static MongoPeople parseGet(JSONObject response) {
        return _parsePeople(response);
    }

    public static MongoPeople parseLogin(String response) {
        try {
            return _parsePeople(new JSONObject(response));
        } catch (JSONException e) {
        }
        return null;
    }

    //    public static MongoPeople parseLogout(JSONObject response) {
//        // TODO
//    }

    public static MongoPeople parseRegister(String response) {
        try {
            return _parsePeople(new JSONObject(response));
        } catch (JSONException e) {
        }
        return null;
    }

    public static MongoPeople parseUpdate(String response) {
        try {
            return _parsePeople(new JSONObject(response));
        } catch (JSONException e) {
        }
        return null;
    }

    public static MongoPeople _parsePeople(JSONObject response) {
        try {
            Gson gson = QSGsonFactory.create();

            String people = response.getJSONObject("data").getJSONObject("people").toString();
            Type type = new TypeToken<MongoPeople>() {
            }.getType();

            return gson.fromJson(people, type);

        } catch (JSONException e) {
            return null;
        }
    }

    public static LinkedList<MongoPeople> _parsePeoples(JSONObject response) {
        try {
            Gson gson = QSGsonFactory.create();

            String people = response.getJSONObject("data").getJSONArray("peoples").toString();
            Type type = new TypeToken<LinkedList<MongoPeople>>() {
            }.getType();

            return gson.fromJson(people, type);

        } catch (JSONException e) {
            return null;
        }
    }

}

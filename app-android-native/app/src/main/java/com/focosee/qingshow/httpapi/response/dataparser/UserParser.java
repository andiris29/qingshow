package com.focosee.qingshow.httpapi.response.dataparser;

import com.focosee.qingshow.entity.mongo.MongoPeople;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

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

    private static MongoPeople _parsePeople(JSONObject response) {
        try {
            String people = response.getJSONObject("data").getJSONObject("people").toString();
            Type type = new TypeToken<MongoPeople>() {
            }.getType();
            Gson gson = new Gson();
            return gson.fromJson(people, type);

        } catch (JSONException e) {
            return null;
        }
    }

}

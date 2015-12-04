package com.focosee.qingshow.httpapi.response.dataparser;

import android.text.TextUtils;

import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.util.StringUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/12/4.
 */
public class ParserGenerator {
    public static <T> T parse(JSONObject response, TypeToken<T> typeToken, String element){
        try {
            String json = TextUtils.isEmpty(element) ? response.getJSONObject("data").toString()
             : response.getJSONObject("data").getJSONObject(element).toString();
            Type type = typeToken.getType();
            Gson gson = QSGsonFactory.create();
            return gson.fromJson(json, type);
        } catch (JSONException e) {
            return null;
        }
    }

    public static <T> T parseQuery(JSONObject response, TypeToken<T> typeToken, String element){
        try {
            String json = TextUtils.isEmpty(element) ? response.getJSONObject("data").toString()
                    : response.getJSONObject("data").getJSONArray(element).toString();
            Type listType = typeToken.getType();
            Gson gson = QSGsonFactory.create();
            return gson.fromJson(json, listType);
        } catch (JSONException e) {
            return null;
        }
    }
}

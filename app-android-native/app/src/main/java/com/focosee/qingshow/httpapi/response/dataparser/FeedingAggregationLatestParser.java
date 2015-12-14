package com.focosee.qingshow.httpapi.response.dataparser;

import android.support.v4.util.ArrayMap;

import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.model.vo.aggregation.FeedingAggregation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/25.
 */
public class FeedingAggregationLatestParser {

    public static FeedingAggregation parse(JSONObject response){
        Gson gson = QSGsonFactory.create();
        try {
            String data = response.getJSONObject("data").toString();
            return gson.fromJson(data, new TypeToken<FeedingAggregation>() {
            }.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<FeedingAggregation> parseQuery(JSONObject response){
        Gson gson = QSGsonFactory.create();
        List<FeedingAggregation> list = new ArrayList<>();
        try {
            JSONObject jsonData = response.getJSONObject("data");
            Iterator<String> keys = jsonData.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String data = jsonData.getJSONObject(key).toString();
                FeedingAggregation aggregation = gson.fromJson(data, new TypeToken<FeedingAggregation>() {
                }.getType());
                aggregation.key = key;
                list.add(aggregation);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}

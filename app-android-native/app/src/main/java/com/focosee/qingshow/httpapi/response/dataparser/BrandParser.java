package com.focosee.qingshow.httpapi.response.dataparser;

import com.focosee.qingshow.entity.mongo.MongoBrand;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by i068020 on 2/8/15.
 */
public class BrandParser {
    // Public interface
    public static ArrayList<MongoBrand> parseQueryBrands(JSONObject response) {
        try {
            String brands = response.getJSONObject("data").getJSONArray("brands").toString();
            return new Gson().fromJson(brands, new TypeToken<ArrayList<MongoBrand>>() {
            }.getType());
        } catch (Exception e) {
            return null;
        }
    }
}

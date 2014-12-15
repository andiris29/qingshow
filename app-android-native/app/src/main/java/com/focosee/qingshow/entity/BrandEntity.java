package com.focosee.qingshow.entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;

public class BrandEntity extends AbsEntity {
    public static final String DEBUG_TAG = "BrandEntity";

    // Public interface
    public static ArrayList<BrandEntity> getBrandListFromResponse(JSONObject response) {
        try {
            String responseString = response.getJSONObject("data").getJSONArray("brands").toString();
            return new Gson().fromJson(responseString, new TypeToken<ArrayList<BrandEntity>>(){}.getType());
        }catch(Exception e) {
            log(e.toString());
            return null;
        }
    }

    public String getBrandName() {
        return name;
    }
    public String getBrandLogo() {
        return logo;
    }
    public String getBrandSlogan() {
        return slogan;
    }
    public String getBrandDescription() {
        return name;
    }

    // Inner data
    public String name;
    public String logo;
    public String slogan;
}

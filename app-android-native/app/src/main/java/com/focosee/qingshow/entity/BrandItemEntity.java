package com.focosee.qingshow.entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class BrandItemEntity extends AbsEntity{

    // Public interface
    public static ArrayList<BrandItemEntity> getBrandItemEntities(JSONObject response) {
        try {
            String tempString = response.getJSONObject("data").getJSONArray("shows").toString();
            Type listType = new TypeToken<ArrayList<BrandItemEntity>>(){}.getType();
            Gson gson = new Gson();
            return gson.fromJson(tempString, listType);
        } catch (JSONException e) {
            log(e.toString());
            return null;
        }
    }

    public String get_id() {
        return _id;
    }

    public String getImage() {
        return image;
    }

    public String getDiscount() {
        return discount;
    }


    public String _id;
    public String image;
    public String discount;
}

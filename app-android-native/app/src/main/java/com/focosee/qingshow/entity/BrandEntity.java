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

    public String get_id() {
        return _id;
    }
    public String getBrandName() {
        return name;
    }
    public String getBrandLogo() {
        return logo;
    }
    public String getBrandCover() {
        return cover;
    }
    public String getBrandDescription() {
        return name;
    }

    public int getCoverWidth() {
        return 320;
    }

    public int getCoverHeight() {
        return 320;
    }

    public String getNewestNumber() {
        return String.valueOf((null != __context) ? __context.numShows : 0);
    }

    public String getDiscountNumber() {
        return String.valueOf((null != __context) ? __context.numShows : 0);
    }

    public String getFansNumber() {
        return String.valueOf((null != __context) ? __context.numFollowers : 0);
    }

    public boolean getModelIsFollowedByCurrentUser() {
        if (null != __context)
            return __context.followedByCurrentUser;
        return false;
    }

    public void setModelIsFollowedByCurrentUser(boolean followedByCurrentUser) {
        __context.followedByCurrentUser = followedByCurrentUser;
    }


    // Inner data
    public String _id;
    public String type;
    public BrandContext __context;
    public String name;
    public String logo;
    public String background;
    public String cover;
    public String create;

    public class BrandContext extends AbsEntity {
        private int numFollowers;
        private int numShows;
        public  boolean followedByCurrentUser = false;
    }

    public class MetaData{
        public String url;
        public int width;
        public int height;
    }
}

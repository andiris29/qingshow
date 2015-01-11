package com.focosee.qingshow.entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class FollowPeopleEntity extends AbsEntity {

    // Public interface (used in HomeWaterAdapter.java)
    public static ArrayList<FollowPeopleEntity> getFollowPeopleList(JSONObject response) {
        try {
            String tempString = response.getJSONObject("data").getJSONArray("peoples").toString();
            Type listType = new TypeToken<ArrayList<FollowPeopleEntity>>(){}.getType();
            Gson gson = new Gson();
            return gson.fromJson(tempString, listType);
        } catch (JSONException e) {
            log(e.toString());
            return null;
        }
    }

    public String getPeopleName() {
        return name;
    }

    public String getPeoplePortrait() {
        return portrait;
    }

    public String getShowNumberString() {
        return String.valueOf(__context.numShows);
    }

    public String getLikeNumberString() {
        return String.valueOf(__context.numFollowers);
    }

    private FollowPeopleContext __context;
//    private String _id;
//    private int __v;
//    private String update;
//    private String create;
//    private String[] hairTypes;
//    private String[] roles;
//
//    private String _hughUpdate;
//    private String height;
//    private String weight;
//    private String name;
//    private String background;
//    private String portrait;

    private String _id;
    private String _hughUpdate;
    private String height;
    private String weight;
    private String name;
    private String background;
    private String update;
    private String portrait;
    private String create;
    private String[] hairTypes;
    private int[] roles;

    class FollowPeopleContext {
        private int numFollowers;
        private int numShows;
        private boolean followedByCurrentUser = false;
    }
}

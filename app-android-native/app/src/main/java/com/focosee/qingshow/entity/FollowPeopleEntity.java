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
        return _id;
    }

    public String getPeoplePortrait() {
        return "";
    }

    public String getShowNumberString() {
        return (null != __context) ? __context.numShows : "0";
    }

    public String getLikeNumberString() {
        return (null != __context) ? __context.numFollowers : "0";
    }

    private FollowPeopleContext __context;
    private String _id;
    private int __v;
    private String update;
    private String create;
    private String[] hairTypes;
    private String[] roles;

    class FollowPeopleContext {
        public String numFollowers;
        private String numShows;
    }
}

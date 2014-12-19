package com.focosee.qingshow.entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ModelEntity extends AbsEntity {

    // Cover super property
    public static final String DEBUG_TAG = "ModelEntity";


    // Public interface
    public static ArrayList<ModelEntity> getModelEntityListFromResponse(JSONObject response) {
        try {
            String tempString = response.getJSONObject("data").getJSONArray("peoples").toString();
            Gson gson = new Gson();
            return gson.fromJson(tempString, new TypeToken<ArrayList<ModelEntity>>(){}.getType());
        } catch (JSONException e) {
            log(e.toString());
            return null;
        }
    }

    public ModelContext get__context() {
        return __context;
    }

    public String get_id() {
        return _id;
    }

    public String get_hughUpdate() {
        return _hughUpdate;
    }

    public String getHeight() {
        return height;
    }

    public String getWeight() {
        return weight;
    }

    public String getName() {
        return name;
    }

    public String getBackground() {
        return background;
    }

    public String getUpdate() {
        return update;
    }

    public String getPortrait() {
        return portrait;
    }

    public String getCreate() {
        return create;
    }

    public String[] getHairTypes() {
        return hairTypes;
    }

    public int[] getRoles() {
        return roles;
    }

    public int getNumberShows() {
        if (null != __context)
            return __context.numShows;
        return 0;
    }

    public int getNumberFollowers() {
        if (null != __context)
            return __context.numFollowers;
        return 0;
    }

    public boolean getModelIsFollowedByCurrentUser() {
        if (null != __context)
            return __context.followedByCurrentUser;
        return false;
    }

    public void setModelIsFollowedByCurrentUser(boolean followedByCurrentUser) {
        __context.followedByCurrentUser = followedByCurrentUser;
    }

    private ModelContext __context;
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

    public class ModelContext {
        private int numFollowers;
        private int numShows;
        private boolean followedByCurrentUser = false;
    }
}

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
        if(null == height){
            return "";
        }
        return height + "cm/";
    }

    public String getWeight() {
        if(null == weight){
            return "";
        }
        return weight + "kg";
    }

    public String getName() {
        return name;
    }

    public String getJob() {
        return "模特";
    }

    public String getHeightWeight() {
        if(height != "" && weight != "" && null != height && null != weight) {
            return String.valueOf(height) + "cm/" + String.valueOf(weight) + "kg";
        }
        return "";
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
    public String _id;
    public String _hughUpdate;
    public String height;
    public String weight;
    public String name;
    public String background;
    public String update;
    public String portrait;
    public String create;
    public String[] hairTypes;
    public int[] roles;

    public class ModelContext extends AbsEntity {
        public int numFollowers;
        public int numShows;
        public boolean followedByCurrentUser = false;
    }
}

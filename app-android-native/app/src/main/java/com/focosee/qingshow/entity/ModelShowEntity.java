package com.focosee.qingshow.entity;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ModelShowEntity extends AbsEntity {
    private final static String DEBUG_TAG = "ModelShowEntity";

    // Public interface (used in HomeWaterAdapter.java)
    public static ArrayList<ModelShowEntity> getModelShowEntities(JSONObject response) {
        try {
            String tempString = response.getJSONObject("data").getJSONArray("shows").toString();
            Type listType = new TypeToken<ArrayList<ModelShowEntity>>(){}.getType();
            Gson gson = new Gson();
//            return gson.fromJson(tempString, listType);
            ArrayList<ModelShowEntity> result;
            result = gson.fromJson(tempString, listType);
            return result;
        } catch (JSONException e) {
            log(e.toString());
            return null;
        }
    }

    public static void log(String info) {
        Log.i(DEBUG_TAG, info);
    }

    public String get_id() {
        return _id;
    }

    public String getCover() {
        return (null != coverMetadata) ? coverMetadata.url : null;
    }

    public int getCoverWidth() {
        return (null != coverMetadata) ? coverMetadata.width : 0;
    }

    public int getCoverHeight() {
        return (null != coverMetadata) ? coverMetadata.height : 0;
    }


    private ModelDetailContext __context;
    private String _id;
    private String _hughUpdate;
    private String name;
    private String cover;
    private String video;
    private int numLike;
    private ModelDetailModelRef modelRef;
    private String create;
    private String[] itemRefs;
    private String[] posters;
    private ModelDetailCoverMetaData coverMetadata;

    class ModelDetailContext {
        private String likedByCurrentUser;
        private int numLike;
        private int numComments;
    }

    class ModelDetailModelRef {
        private ModelDetailModelRefContext __context;
        private String _id;
        private String _hughUpdate;
        private float height;
        private String name;
        private float weight;
        private String background;
        private String portrait;
        private String update;
        private String create;
        private int[] hairTypes;
        private int[] roles;

        class ModelDetailModelRefContext {
            private String followedByCurrentUser;
        }
    }

    class ModelDetailCoverMetaData {
        private String url;
        private int width;
        private int height;
    }
}

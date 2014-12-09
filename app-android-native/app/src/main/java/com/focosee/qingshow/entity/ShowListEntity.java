package com.focosee.qingshow.entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.LinkedList;

public class ShowListEntity extends AbsEntity {


    // Cover super property
    public static final String DEBUG_TAG = "ShowEntity";


    // Public interface (used in HomeWaterAdapter.java)
    public static LinkedList<ShowListEntity> getShowListFromResponse(JSONObject response) {
        try {
            String tempString = response.getJSONObject("data").getJSONArray("shows").toString();
            Type listType = new TypeToken<LinkedList<ShowListEntity>>(){}.getType();
            Gson gson = new Gson();
            return gson.fromJson(tempString, listType);
        } catch (JSONException e) {
            log(e.toString());
            return null;
        }
    }

    public String getShowCover() {
        if (null != coverMetadata && null != coverMetadata.cover)
            return coverMetadata.cover;
        return null;
    }

    public int getCoverHeight() {
        if (null != coverMetadata && null != coverMetadata.cover)
            return coverMetadata.height;
        return 0;
    }

    public int getCoverWidth() {
        if (null != coverMetadata && null != coverMetadata.cover)
            return coverMetadata.width;
        return 0;
    }

    public String getShowNumLike() {
        return numLike;
    }

    public String getModelPhoto() {
        return modelRef.portrait;
    }

    public String getModelName() {
        return modelRef.name;
    }

    public String getModelHeight() {
        if (null != modelRef)
            return String.valueOf(modelRef.height);
        return "0";
    }

    public String getModelWeight() {
        if (null != modelRef)
            return String.valueOf(modelRef.weight);
        return "0";
    }

    // TODO check job field
    public String getModelJob() {
        if (null != modelRef)
            return String.valueOf(modelRef.roles);
        return "";
    }

    public String getModelTag() {
        return modelRef.modelInfo.status;
    }

    public String getModelStatus() {
        return modelRef.modelInfo.status;
    }



    // Inner data
    public String _id;                      // "5439f64013bf528b45f00f9a"
    public String name;                     // "秀"
    public String cover;                    // "url for image source"
    public String video;                    // "/10.mp4.mp4"
    public String numLike;                  // "7777"
    public RefModel modelRef;               // "Model Object"
    public String create;                   // "2014-11-21T15:52:27.740Z"
    public String[] itemRefs;          // "Item Object List"
    public String[] styles;
    public String[] posters;            // "Poster(str) List"
    public MetaDataCover coverMetadata;     // "Cover Object"
    public ShowContext __context;


    // Item object in show
    public static class RefModel extends AbsEntity {
        public String _id;
        public String name;
        public String portrait;
        public String birthtime;
        public float height;
        public float weight;
        public String[] followerRefs;
        public String __v;
        public String update;
        public String create;
        public InfoModel modelInfo;
        public String[] hairTypes;
        public int[] roles;
        public ModelContext __context;

        public static class InfoModel extends AbsEntity {
            public String status;
            public int numLikes;
        }

        public static class ModelContext {
            public Boolean followedByCurrentUser;
        }
    }

    public static class MetaDataCover extends AbsEntity {
        public String cover;
        public int width;
        public int height;
    }

    public static class ShowContext {
        public int numComments;
        public int numLike;
        public Boolean likedByCurrentUser;
    }

}

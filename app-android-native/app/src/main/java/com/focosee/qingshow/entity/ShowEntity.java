package com.focosee.qingshow.entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class ShowEntity extends AbsEntity {

    //--- Public interface
    public String getShowId(){
        return _id;
    }

    public String getShowName(){
        return name;
    }

    // the image of the show
    public String getShowCover() {
        if (null != coverMetaData && null != coverMetaData.cover)
            return coverMetaData.cover;
        return null;
    }

    public String getCoverHeight() {
        if (null != coverMetaData && null != coverMetaData.cover)
            return coverMetaData.height;
        return null;
    }

    public String getCoverWidth() {
        return coverMetaData.width;
    }

    // the image of the show
    public String getShowVideo() {
        return video;
    }

    // the image of the show
    public String getShowNumLike() {
        return numLike;
    }

    // the image of the show
    public String getShowCreateTime() {
        return create;
    }

    // model image
    public String getModelImgSrc() {
        return modelRef.portrait;
    }

    // model name
    public String getModelName() {
        return modelRef.name;
    }

    // model height
    public String getModelHeight() {
        return "10";
    }

    // model weight
    public String getModelWeight() {
        return "10";
    }

    // model status
    public String getModelStatus() {
        return modelRef.modelInfo.status;
    }

    public String getModelJob() {
        return modelRef.roles.get(0);
    }

    // item id
    public String getItemId(int itemIndex) {
        return (itemIndex >=0 && itemIndex < itemRefs.size()) ? itemRefs.get(itemIndex)._id : null;
    }

    public static LinkedList<ShowEntity> getLinkedListFromString(String inputStr) {
        Type listType = new TypeToken<LinkedList<ShowEntity>>(){}.getType();
        Gson gson = new Gson();
        return gson.fromJson(inputStr, listType);

    }


    //--- Inner data
    public String _id;                      // "5439f64013bf528b45f00f9a"
    public String name;                     // "秀"
    public String cover;                    // "url for image source"
    public String video;                    // "/10.mp4.mp4"
    public String numLike;                  // "7777"
    public RefModel modelRef;               // "Model Object"
    public String create;                   // "2014-11-21T15:52:27.740Z"
    public List<RefItem> itemRefs;          // "Item Object List"
    public List<String> tags;               // "Tag(str) List"
    public List<String> posters;            // "Poster(str) List"
    public MetaDataCover coverMetaData;     // "Cover Object"

    //--- Item object in show
    public static class RefModel {
        public String _id;
        public String name;
        public String portrait;
        public String birthtime;
        public String __v;
        public String update;
        public String create;
        public InfoModel modelInfo;
        public List<String> roles;

        public static class InfoModel {
            public String status;
            public String numLikes;
        }
    }

    public static class RefItem {
        //--- Inner data
        public String _id;
        public String category;
        public String name;
        public String cover;
        public String source;
        public String brandRef;
    }

    public static class MetaDataCover {
        public String cover;
        public String width;
        public String height;
    }

    //--- Generated setMethod (User do not need to know, only for json conversion)
}

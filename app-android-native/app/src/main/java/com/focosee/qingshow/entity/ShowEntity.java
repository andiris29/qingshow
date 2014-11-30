package com.focosee.qingshow.entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
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

    // the image of the show
    public String getShowVideo() {
        return (null != video) ? video : null;
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

    // model job
    public String getModelJob() {
        return modelRef.roles[0];
    }

    // item
    public RefItem getItem(int index) {
        return (index < itemRefs.length) ? itemRefs[index] : null;
    }

    // item list
    public ArrayList<RefItem> getItemsList() {
        ArrayList<RefItem> _itemsData = new ArrayList<RefItem>();
        for (RefItem item : itemRefs)
            _itemsData.add(item);
        return _itemsData;
    }

    // tag
    public String[] getTag() {
        return tags;
    }

    // All item description
    public String getAllItemDescription() {
        String description = "";
        for (RefItem item : itemRefs)
            description += item.name;
        return description;
    }

    public String getAge() {
        return create;
    }

    // Posters
    public String[] getPosters() {
        return posters;
    }

    // Item url list
    public List<String> getItemUrlList() {
        ArrayList<String> itemUrlList = new ArrayList<String>();
        for (RefItem item : itemRefs) {
            itemUrlList.add(item.cover);
        }
        return itemUrlList;
    }

    // Item description list
    public List<String> getItemDescriptionList() {
        ArrayList<String> itemDescriptionList = new ArrayList<String>();
        for (RefItem item : itemRefs) {
            itemDescriptionList.add(item.name);
        }
        return itemDescriptionList;
    }

    // Item brand list
    public List<String> getItemBrandList() {
        ArrayList<String> itemBrandList = new ArrayList<String>();
        for (RefItem item : itemRefs) {
            itemBrandList.add(item.brandRef);
        }
        return itemBrandList;
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
    public RefItem[] itemRefs;          // "Item Object List"
    public String[] tags;               // "Tag(str) List"
    public String[] posters;            // "Poster(str) List"
    public MetaDataCover coverMetadata;     // "Cover Object"

    //--- Item object in show
    public static class RefModel extends AbsEntity {
        public String _id;
        public String name;
        public String portrait;
        public String birthtime;
        public String __v;
        public String update;
        public String create;
        public InfoModel modelInfo;
        public String[] roles;

        public static class InfoModel extends AbsEntity {
            public String status;
            public String numLikes;
        }
    }

    public static class RefItem extends AbsEntity {
        //--- Inner data
        public String _id;
        public String category;
        public String name;
        public String cover;
        public String source;
        public String brandRef;
    }

    public static class MetaDataCover extends AbsEntity {
        public String cover;
        public int width;
        public int height;
    }

    //--- Generated setMethod (User do not need to know, only for json conversion)
}

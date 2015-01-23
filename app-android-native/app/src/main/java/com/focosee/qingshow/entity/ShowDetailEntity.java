package com.focosee.qingshow.entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ShowDetailEntity extends AbsEntity {

    // Cover super property
    public static final String DEBUG_TAG = "ShowDetailEntity";


    // Public interface (used in S03ShowActivity.java)
    public static ShowDetailEntity getShowDetailFromResponse(JSONObject response) {
        try {
            String tempString = response.getJSONObject("data").getJSONArray("shows").get(0).toString();
            Gson gson = new Gson();
            return gson.fromJson(tempString, ShowDetailEntity.class);
        } catch (JSONException e) {
            log(e.toString());
            return null;
        }
    }

    public String get_id() {
        return _id;
    }

    public String getShowVideo() {
        return (null != video) ? video : null;
    }

    public String getModelPhoto() {
        if (null != modelRef && null != modelRef.portrait)
            return modelRef.portrait;
        return null;
    }
    public String getModelName() {
        return (null != modelRef && null != modelRef.name) ? modelRef.name : "";
    }
    public String getModelJob() {
        return (null != modelRef && null != modelRef.roles) ? modelRef.roles.toString() : "";
    }
    public String getModelWeightHeight() {
        return ((null != modelRef) ? modelRef.height : "") + "cm/" + ((null != modelRef) ? modelRef.weight : "") + "kg";
    }

    // TODO change age calculate method
    public String getModelAgeHeight() {
        return (null != modelRef) ? modelRef.birthtime + "岁 " + modelRef.height + "cm" : "岁 cm";
    }

    public String getModelStatus() {
        if (null != modelRef && null != modelRef.modelInfo && null != modelRef.modelInfo.status)
            return modelRef.modelInfo.status;
        return "";
    }

    public ArrayList<RefItem> getItemsList() {
        ArrayList<RefItem> result = new ArrayList<RefItem>();
        for (RefItem item : itemRefs) {
            result.add(item);
        }
        return result;
    }
    public RefItem getItem(int index) {
        if (null == itemRefs || index >= itemRefs.length || index < 0) return null;
        return itemRefs[index];
    }
    public String getAllItemDescription() {
        String description = "";
        for (RefItem item : itemRefs)
            description += item.name;
        return description;
    }
    public List<String> getItemDescriptionList() {
        ArrayList<String> itemDescriptionList = new ArrayList<String>();
        for (RefItem item : itemRefs) {
            itemDescriptionList.add(item.name);
        }
        return itemDescriptionList;
    }

    public String[] getPosters() {
        return (null != posters) ? posters : new String[0];
    }

    public String getShowCommentNumber() {
        return (null != __context) ? String.valueOf(__context.numComments) : "0";
    }

    public String getShowLikeNumber() {
        return (null != __context) ? String.valueOf(__context.numLike) : "0";
    }

    public Boolean likedByCurrentUser() {
        return __context.likedByCurrentUser;
    }

    public void setLikedByCurrentUser(Boolean likedByCurrentUser) {
        __context.likedByCurrentUser = likedByCurrentUser;
        if (likedByCurrentUser)
            __context.numLike++;
        else if (__context.numLike>0)
            __context.numLike--;
    }

    public String getCover() {
        return cover;
    }

    public String getItemsCount() {
        return String.valueOf((null != itemRefs) ? itemRefs.length : 0);
    }

    public String getBrandNameText() {
        return name == null ? "" : name;
    }

//    public RefBrand getBrandEntity() {
//        return brandRef;
//    }

    // Inner data
    public String _id;                      // "5439f64013bf528b45f00f9a"
    public String name;
    public String cover;                    // "url for image source"
    public String video;                    // "/10.mp4.mp4"
    public int numLike;                  // "7777"
    public RefModel modelRef;               // "Model Object"
    //public RefBrand brandRef;
    //public BrandEntity brandRef;
    public String create;                   // "2014-11-21T15:52:27.740Z"
    public RefItem[] itemRefs;          // "Item Object List"
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
        public int __v;
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

    public static class RefItem extends AbsEntity {

        // Public interface
        public static ArrayList<RefItem> getItemEntities(JSONObject response) {
            try {
                String tempString = response.getJSONObject("data").getJSONArray("items").toString();
                Type listType = new TypeToken<ArrayList<RefItem>>(){}.getType();
                Gson gson = new Gson();
                return gson.fromJson(tempString, listType);
            } catch (JSONException e) {
                log(e.toString());
                return null;
            }
        }

        public static MetaData getMetaData(JSONObject response){
            try {
                String metadataString = response.getJSONObject("metadata").toString();
                Type metadataType = new TypeToken<MetaData>(){}.getType();
                Gson gson = new Gson();
                return gson.fromJson(metadataString, metadataType);
            } catch (JSONException e) {
                log(e.toString());
                return null;
            }
        }

        public String getItemName() {
            return name;
        }

        public String getItemCategory() {
            String categoryName;
            switch (category) {
                case 0:
                    categoryName = "上装";
                    break;
                case 1:
                    categoryName = "下装";
                    break;
                case 2:
                    categoryName = "鞋子";
                    break;
                case 3:
                    categoryName = "配饰";
                    break;
                default:
                    categoryName = "未定义";
                    break;
            }
            return categoryName;
        }

        public String getCover() {
            return source;
        }

        public String getBrandPortrait() {
            return (null != imageMetadata) ? imageMetadata.url : null;
        }

        public String getSource() {
            return source;
        }

        public String getOriginPrice() {
            return price;
        }

        public String getPrice() {
            return "￥ " + price;
        }

//        public RefItem getBrandEntity() {
//            return this;
//        }
        public BrandEntity getBrandRef(){

            if(null == brandRef)return null;

            LinkedHashMap brandMap = new LinkedHashMap((LinkedHashMap)brandRef);
            LinkedHashMap context = (LinkedHashMap)brandMap.get("__context");
            LinkedHashMap coverMetadata = (LinkedHashMap)brandMap.get("coverMetadata");
            brandMap.remove("__context");
            brandMap.remove("coverMetadata");
            JSONObject jsonObject = new JSONObject(brandMap);

            try {
                jsonObject.put("__context", new JSONObject(context));
                jsonObject.put("coverMetadata", new JSONObject(coverMetadata));
                String temp = jsonObject.toString();

                return new Gson().fromJson(temp, BrandEntity.class);
            } catch (Exception e) {
                log(e.toString());
                return null;
            }
        }

        public String getBrandId(){
            if(null == brandRef) return "";
            return String.valueOf(brandRef);
        }
        public ArrayList<ImageInfo> getImages(){ return images; }

        public int getOrder(){
            return (brandNewInfo == null) ? 0:brandNewInfo.order;
        }

        public int getNumTotal(){ return (null == metadata) ? 0:metadata.numTotal; }

        public String _id;
        public String _kellyupdate;
        public int category;
        public String name;
        //public String cover;
        public String source;
        public Object brandRef;
        public String create;
        public String price;
        public BrandNewInfo brandNewInfo;
        public ArrayList<ImageInfo> images;
        public MetaDataCover imageMetadata;
        public MetaData metadata;
        //public String priceAfterDiscount;
    }

    public static class ImageInfo extends AbsEntity {
        public String url;
        public String description;
    }

    public static class MetaDataCover extends AbsEntity {
        public String url;
        public int width;
        public int height;
    }

    public class MetaData extends AbsEntity{
        public int numTotal;
        public int numPages;
    }

    public static class ShowContext {
        public int numComments;
        public int numLike;
        public Boolean likedByCurrentUser = false;
    }

    public static class BrandNewInfo extends AbsEntity {

        public int order;

    }

    public static class RefBrand extends AbsEntity {
        public String _id;
        public String type;
        public String name;
        public String logo;
        public String background;
        public String shopInfo;
        public String address;
        public String phone;
        public String create;
    }

}

package com.focosee.qingshow.entity;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import org.json.JSONObject;
import com.google.gson.Gson;
import java.util.LinkedList;
import java.util.List;

public class TrendEntity extends AbsEntity {
    // Cover super property
    public static final String DEBUG_TAG = "TrendEntity";

    public String get_id() {
        return _id;
    }

    public static LinkedList<TrendEntity> getTrendListFromResponse(JSONObject response){
        try {

            String tempString = response.getJSONObject("data").getJSONArray("previews").toString();
            Type typeList = new TypeToken<LinkedList<TrendEntity>>(){}.getType();
            Gson gson = new Gson();
            return gson.fromJson(tempString, typeList);
        }catch(Exception e){
            log(e.toString());
            return null;
        }
    }

    public int getNumComments(){
        if(null != __context){
            return __context.numComments;
        }
        return 0;
    }

    public boolean getIsLikeByCurrentUser(){
        if(null != __context){
            return __context.likedByCurrentUser;
        }
        return false;
    }

    public String getCover(int index){   if(null == images.get(index)){return "";}   return images.get(index).url;}
    public String getBrandDescription(int index){   if(null == images || null == images.get(index)){return "";}   return images.get(index).brandDescription;}
    public String getNameDescription(int index){   if(null == images || null == images.get(index)){return "";}   return images.get(index).description;}
    public String getPriceDescription(int index){   if(null == images || null == images.get(index)){return "";}   return images.get(index).priceDescription;}

    public int getWidth(){
        if(null != imageMetadata){
            return imageMetadata.width;
        }
        return 0;
    }

    public int getHeight(){
        if(null != imageMetadata){
            return imageMetadata.height;
        }
        return 0;
    }

    //Inner data
    public TrendContext __context;
    public String _id;
    public String create;
    public int numLike;

    public MetaDataCover imageMetadata;
    public LinkedList<ImageInfo> images;

    public class MetaDataCover extends AbsEntity{

        public String url;
        public int width;
        public int height;

    }

    public class TrendContext{
        public boolean likedByCurrentUser;
        public int numComments;
    }

    public class ImageInfo{
        public String url;
        public String description;
        public String priceDescription;
        public String brandDescription;

    }

}

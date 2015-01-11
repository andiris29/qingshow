package com.focosee.qingshow.entity;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import org.json.JSONObject;
import com.google.gson.Gson;
import java.util.LinkedList;

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

    public String getCover(){   if(null == cover){return "";}   return cover;}
    public String getBrandDescription(){   if(null == brandDescription){return "";}   return brandDescription;}
    public String getNameDescription(){   if(null == nameDescription){return "";}   return nameDescription;}
    public String getPriceDescription(){   if(null == priceDescription){return "";}   return priceDescription;}

    public int getWidth(){
        if(null != coverMetadata){
            return coverMetadata.width;
        }
        return 0;
    }

    public int getHeight(){
        if(null != coverMetadata){
            return coverMetadata.height;
        }
        return 0;
    }

    //Inner data
    public TrendContext __context;
    public String _id;
    public String cover;
    public String brandDescription;
    public String nameDescription;
    public String priceDescription;
    public String create;
    public int numLike;
    public MetaDataCover coverMetadata;

    public static class MetaDataCover extends AbsEntity{

        public String url;
        public int width;
        public int height;

    }

    public class TrendContext{
        public int numComments;
    }

}

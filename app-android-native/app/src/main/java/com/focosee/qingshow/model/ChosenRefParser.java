package com.focosee.qingshow.model;

import com.focosee.qingshow.model.vo.mongo.MongoPreview;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by 华榕 on 2015/5/17.
 */
public class ChosenRefParser {


    public static MongoPreview previewParser(Object obj){


        Gson gson = new Gson();
        String temp = gson.toJson(obj);

        return gson.fromJson(temp, MongoPreview.class);

//        MongoPreview preview = new MongoPreview();
//
//        if (null == obj) return preview;
//        try {
//
//            Class cls = preview.getClass();
//
//            Set set = obj.entrySet();
//            for (Object key : set) {
//                Field field = cls.getDeclaredField(String.valueOf(key));
//                field.setAccessible(true);
//                field.set(preview, obj.get(key));
//            }
//
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return preview;
    }





}

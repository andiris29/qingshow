package com.focosee.qingshow.httpapi.gson.deserializer;

import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.model.vo.mongo.MongoStickyShow;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by Administrator on 2016/1/4.
 */
public class MongoStickyShowDeserializer implements JsonDeserializer<MongoStickyShow> {
    @Override
    public MongoStickyShow deserialize(JsonElement jElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(jElement.isJsonObject()){
            return QSGsonFactory.create(MongoStickyShow.class).fromJson(jElement.getAsJsonObject(), MongoStickyShow.class);
        }else{
            MongoStickyShow item = new MongoStickyShow();
            item._id = jElement.getAsString();
            return item;
        }
    }
}

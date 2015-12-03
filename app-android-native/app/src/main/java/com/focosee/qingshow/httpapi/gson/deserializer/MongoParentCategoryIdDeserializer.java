package com.focosee.qingshow.httpapi.gson.deserializer;

import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.model.vo.mongo.MongoParentCategories;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by i068020 on 2/8/15.
 */
public class MongoParentCategoryIdDeserializer implements JsonDeserializer<MongoParentCategories> {

    @Override
    public MongoParentCategories deserialize
            (JsonElement jElement, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if(jElement.isJsonObject()){
            return QSGsonFactory.create(MongoParentCategories.class).fromJson(jElement.getAsJsonObject(), MongoParentCategories.class);
        }
        MongoParentCategories categorie = new MongoParentCategories();
        categorie._id = jElement.getAsString();
        return categorie;
    }
}

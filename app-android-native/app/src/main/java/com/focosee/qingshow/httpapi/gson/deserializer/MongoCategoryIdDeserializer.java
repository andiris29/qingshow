package com.focosee.qingshow.httpapi.gson.deserializer;

import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.model.vo.mongo.MongoCategories;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by i068020 on 2/8/15.
 */
public class MongoCategoryIdDeserializer implements JsonDeserializer<MongoCategories> {
    @Override
    public MongoCategories deserialize
            (JsonElement jElement, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        MongoCategories categorie = new MongoCategories();
        if (jElement.isJsonObject())
            return QSGsonFactory.create(MongoCategories.class).fromJson(jElement.getAsJsonObject(), MongoCategories.class);
        else
            categorie._id = jElement.getAsString();
        return categorie;
    }
}

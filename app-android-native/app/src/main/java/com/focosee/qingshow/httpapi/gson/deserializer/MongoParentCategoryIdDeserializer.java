package com.focosee.qingshow.httpapi.gson.deserializer;

import com.focosee.qingshow.model.vo.mongo.MongoParentCategories;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

/**
 * Created by i068020 on 2/8/15.
 */
public class MongoParentCategoryIdDeserializer implements JsonDeserializer<MongoParentCategories> {

    @Override
    public MongoParentCategories deserialize
            (JsonElement jElement, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        MongoParentCategories categorie = new MongoParentCategories();
        categorie._id = jElement.getAsString();
        return categorie;
    }
}

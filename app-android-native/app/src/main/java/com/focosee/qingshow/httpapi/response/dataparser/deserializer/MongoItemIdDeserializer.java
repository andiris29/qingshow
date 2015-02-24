package com.focosee.qingshow.httpapi.response.dataparser.deserializer;

import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by i068020 on 2/8/15.
 */
public class MongoItemIdDeserializer implements JsonDeserializer<MongoItem> {
    @Override
    public MongoItem deserialize
            (JsonElement jElement, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        MongoItem item = new MongoItem();
        item._id = jElement.getAsString();
        return item;
    }
}

package com.focosee.qingshow.httpapi.gson.deserializer;

import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.model.vo.mongo.MongoCategories;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by i068020 on 2/8/15.
 */
public class MongoPeopleDeserializer implements JsonDeserializer<MongoPeople> {
    @Override
    public MongoPeople deserialize
            (JsonElement jElement, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if(!jElement.isJsonObject()) {
            MongoPeople people = new MongoPeople();
            people._id = jElement.getAsString();
            return people;
        }else{
            return QSGsonFactory.create(MongoPeople.class).fromJson(jElement.toString(), MongoPeople.class);
        }
    }
}

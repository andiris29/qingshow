package com.focosee.qingshow.httpapi.gson.deserializer;

import com.focosee.qingshow.model.vo.mongo.MongoPreview;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by DylanJiang on 15/5/16.
 */
public class ChosenPreviewDeserializer implements JsonDeserializer<MongoPreview> {
    @Override
    public MongoPreview deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(json.getAsString().equals("previews")){
            return new MongoPreview();
        }
        return null;
    }
}

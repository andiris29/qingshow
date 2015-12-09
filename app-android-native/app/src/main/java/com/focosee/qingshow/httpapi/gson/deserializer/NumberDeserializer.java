package com.focosee.qingshow.httpapi.gson.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

/**
 * Created by Administrator on 2015/10/6.
 */
public class NumberDeserializer implements JsonDeserializer<Number> {

    @Override
    public Number deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Float.parseFloat(json.getAsString());
    }
}

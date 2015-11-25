package com.focosee.qingshow.httpapi.gson.deserializer;

import com.focosee.qingshow.model.vo.aggregation.FeedingAggregation;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by Administrator on 2015/11/25.
 */
public class AggregationDeserializer implements JsonDeserializer<FeedingAggregation> {
    @Override
    public FeedingAggregation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return null;
    }
}

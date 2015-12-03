package com.focosee.qingshow.httpapi.gson;

import com.focosee.qingshow.httpapi.gson.deserializer.AggregationDeserializer;
import com.focosee.qingshow.httpapi.gson.deserializer.MongoCategoryIdDeserializer;
import com.focosee.qingshow.httpapi.gson.deserializer.MongoItemIdDeserializer;
import com.focosee.qingshow.httpapi.gson.deserializer.MongoPeopleDeserializer;
import com.focosee.qingshow.httpapi.gson.deserializer.NumberDeserializer;
import com.focosee.qingshow.httpapi.gson.deserializer.QSRectDeserializer;
import com.focosee.qingshow.httpapi.gson.deserializer.UTCDeserializer;
import com.focosee.qingshow.model.vo.aggregation.FeedingAggregation;
import com.focosee.qingshow.model.vo.context.QSRect;
import com.focosee.qingshow.model.vo.mongo.MongoCategories;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.GregorianCalendar;

/**
 * Created by i068020 on 2/28/15.
 */
public class QSGsonFactory {

    public static Gson create() {
        return createBuilder().create();
    }

    public static Gson create(Class omit){
        return createDeserializerBuilder(TypeToken.get(omit)).create();
    }

    public static GsonBuilder createBuilder(){
        GsonBuilder builder = new GsonBuilder();
        return builder.registerTypeAdapter(GregorianCalendar.class, new UTCDeserializer())
                .registerTypeAdapter(MongoPeople.class, new MongoPeopleDeserializer())
                .registerTypeAdapter(QSRect.class, new QSRectDeserializer())
                .registerTypeAdapter(Number.class, new NumberDeserializer())
                .registerTypeAdapter(MongoCategories.class, new MongoCategoryIdDeserializer())
                .registerTypeAdapter(FeedingAggregation.class, new AggregationDeserializer())
                .registerTypeAdapter(MongoItem.class, new MongoItemIdDeserializer());
    }

    private static GsonBuilder createDeserializerBuilder(TypeToken omit) {
        GsonBuilder builder = new GsonBuilder();
        return builder.registerTypeAdapterFactory(new OmitTypeAdapterFactory(omit, createBuilder()));
    }
}

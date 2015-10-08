package com.focosee.qingshow.httpapi.gson;

import com.focosee.qingshow.httpapi.gson.deserializer.MongoCategoryIdDeserializer;
import com.focosee.qingshow.httpapi.gson.deserializer.MongoItemIdDeserializer;
import com.focosee.qingshow.httpapi.gson.deserializer.MongoParentCategoryIdDeserializer;
import com.focosee.qingshow.httpapi.gson.deserializer.MongoPeopleDeserializer;
import com.focosee.qingshow.httpapi.gson.deserializer.PriceDeserializer;
import com.focosee.qingshow.httpapi.gson.deserializer.UTCDeserializer;
import com.focosee.qingshow.model.vo.mongo.MongoCategories;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.model.vo.mongo.MongoParentCategories;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.GregorianCalendar;
import java.util.LinkedList;

/**
 * Created by i068020 on 2/28/15.
 */
public class QSGsonFactory {
    public static Gson create() {
        return createBuilder().create();
    }

    public static GsonBuilder createBuilder() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(GregorianCalendar.class, new UTCDeserializer());
        return builder;
    }

    public static GsonBuilder itemBuilder() {
        GsonBuilder builder = createBuilder();
        builder.registerTypeAdapter(Number.class, new PriceDeserializer());
        builder.registerTypeAdapter(MongoItem.class, new MongoItemIdDeserializer());
        return builder;
    }

    public static GsonBuilder cateGoryBuilder() {
        GsonBuilder builder = createBuilder();
        builder.registerTypeAdapter(Number.class, new PriceDeserializer());
        builder.registerTypeAdapter(MongoCategories.class, new MongoCategoryIdDeserializer());
        return builder;
    }

    public static GsonBuilder parentCateGoryBuilder() {
        GsonBuilder builder = createBuilder();
        builder.registerTypeAdapter(MongoParentCategories.class, new MongoParentCategoryIdDeserializer());
        return builder;
    }

    public static GsonBuilder peopleBuilder() {
        GsonBuilder builder = createBuilder();
        builder.registerTypeAdapter(MongoPeople.class, new MongoPeopleDeserializer());
        return builder;
    }

    public static GsonBuilder peopleAndItemBuilder() {
        GsonBuilder builder = createBuilder();
        builder.registerTypeAdapter(Number.class, new PriceDeserializer());
        builder.registerTypeAdapter(MongoPeople.class, new MongoPeopleDeserializer()).registerTypeAdapter(MongoItem.class, new MongoItemIdDeserializer());
        return builder;
    }

    public static GsonBuilder tradeBudiler(){
        GsonBuilder builder = createBuilder();
        builder.registerTypeAdapter(Number.class, new PriceDeserializer());
        builder.registerTypeAdapter(MongoCategories.class, new MongoCategoryIdDeserializer());
        builder.registerTypeAdapter(MongoItem.class, new MongoItemIdDeserializer());
        return builder;
    }
}

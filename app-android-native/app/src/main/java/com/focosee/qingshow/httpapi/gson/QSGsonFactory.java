package com.focosee.qingshow.httpapi.gson;

import com.focosee.qingshow.httpapi.gson.deserializer.BonusAmountDeserializer;
import com.focosee.qingshow.httpapi.gson.deserializer.MongoCategoryIdDeserializer;
import com.focosee.qingshow.httpapi.gson.deserializer.MongoItemIdDeserializer;
import com.focosee.qingshow.httpapi.gson.deserializer.MongoParentCategoryIdDeserializer;
import com.focosee.qingshow.httpapi.gson.deserializer.MongoPeopleDeserializer;
import com.focosee.qingshow.httpapi.gson.deserializer.MongoStickyShowDeserializer;
import com.focosee.qingshow.httpapi.gson.deserializer.NumberDeserializer;
import com.focosee.qingshow.httpapi.gson.deserializer.QSRectDeserializer;
import com.focosee.qingshow.httpapi.gson.deserializer.UTCDeserializer;
import com.focosee.qingshow.model.vo.aggregation.BonusAmount;
import com.focosee.qingshow.model.vo.mongo.MongoStickyShow;
import com.focosee.qingshow.model.vo.remix.QSRect;
import com.focosee.qingshow.model.vo.mongo.MongoCategories;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.model.vo.mongo.MongoParentCategories;
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
                .registerTypeAdapter(MongoParentCategories.class, new MongoParentCategoryIdDeserializer())
                .registerTypeAdapter(MongoItem.class, new MongoItemIdDeserializer())
                .registerTypeAdapter(BonusAmount.class, new BonusAmountDeserializer());

    }

    private static GsonBuilder createDeserializerBuilder(TypeToken omit) {
        GsonBuilder builder = new GsonBuilder();
        return builder.registerTypeAdapterFactory(new OmitTypeAdpaterFactory(omit, createBuilder()));
    }
}

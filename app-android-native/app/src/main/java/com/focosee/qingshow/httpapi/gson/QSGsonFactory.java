package com.focosee.qingshow.httpapi.gson;

import com.focosee.qingshow.httpapi.gson.deserializer.UTCDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.GregorianCalendar;

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
}

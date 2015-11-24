package com.focosee.qingshow.httpapi.gson.deserializer;

import android.graphics.Rect;

import com.focosee.qingshow.model.vo.context.QSRect;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;


public class QSRectDeserializer implements JsonDeserializer<QSRect> {
    @Override
    public QSRect deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray arr = json.getAsJsonArray();
        int left = Integer.parseInt(arr.get(0).toString());
        int top = Integer.parseInt(arr.get(1).toString());
        int width = Integer.parseInt(arr.get(2).toString());
        int height =Integer.parseInt( arr.get(3).toString());
        return new QSRect(left, top, width, height);
    }
}
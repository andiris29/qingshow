package com.focosee.qingshow.httpapi.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

/**
 * Created by Administrator on 2015/12/3.
 */
public class OmitTypeAdpaterFactory implements TypeAdapterFactory {

    TypeToken omit;
    GsonBuilder builder;

    public OmitTypeAdpaterFactory(TypeToken omit, GsonBuilder builder) {
        this.omit = omit;
        this.builder = builder;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if(omit.equals(type)){
            return null;
        }else {
            return builder.create().getAdapter(type);
        }
    }
}

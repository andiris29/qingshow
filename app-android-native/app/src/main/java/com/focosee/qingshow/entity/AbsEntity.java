package com.focosee.qingshow.entity;

import android.util.Log;

import java.io.Serializable;

public abstract class AbsEntity implements Serializable {

    public static final String DEBUG_TAG = "AbsEntity";

    public static void log(String info) {
        Log.i(DEBUG_TAG, info);
    }
}

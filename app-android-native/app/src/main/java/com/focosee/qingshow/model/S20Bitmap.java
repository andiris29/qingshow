package com.focosee.qingshow.model;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2015/7/9.
 */
public enum S20Bitmap {
    INSTANCE;
    private Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }



}

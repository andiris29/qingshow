package com.focosee.qingshow.util;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import java.util.Hashtable;

/**
 * Created by DylanJiang on 15/5/25.
 */
public class FontsUtil {

    private static Hashtable<String, Typeface> fontCache = new Hashtable<>();

    public static Typeface get(Context context,String name) {
        Typeface tf = fontCache.get(name);
        if(tf == null) {
            try {
                tf = Typeface.createFromAsset(context.getAssets(), name);
            }
            catch (Exception e) {
                return null;
            }
            fontCache.put(name, tf);
        }
        return tf;
    }

    public static void changeFont(Context context,TextView view,String path){
        view.setTypeface(get(context,path));
    }
}

package com.focosee.qingshow.util;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Created by DylanJiang on 15/5/25.
 */
public class FontsUtil {
    public static void changeFont(Context context,TextView view,String path){
        view.setTypeface(Typeface.createFromAsset(context.getAssets(), path));
    }
}

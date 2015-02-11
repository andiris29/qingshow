package com.focosee.qingshow.util;

import android.util.Log;

/**
 * Created by Administrator on 2015/2/11.
 */
public class ImgUtil {

    public static String imgTo2x(String url){
        String str = "";
        if(null != url)
        str = url.substring(0,url.length() - 4) + "@2x.jpg";
        Log.i("tag",url);
        Log.i("tag",str);
        return str;
    }
}

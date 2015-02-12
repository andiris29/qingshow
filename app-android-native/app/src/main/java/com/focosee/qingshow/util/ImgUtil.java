package com.focosee.qingshow.util;

public class ImgUtil {

    public static String imgTo2x(String url){
        String str = "";
        if(null != url)
        str = url.substring(0,url.length() - 4) + "@2x.jpg";
        return str;
    }
}

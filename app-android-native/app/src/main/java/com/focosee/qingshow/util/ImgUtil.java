package com.focosee.qingshow.util;

import android.net.Uri;

public class ImgUtil {

    public static String imgTo2x(String url) {
        String str = "";
        if (null != url)
            str = url.substring(0, url.length() - 4) + "@2x.jpg";
        return str;
    }

    public static String getImgSrc(String url, int scale) {
        return getImgSrc(url, scale, null);
    }

    public static String getImgSrc(String url, int scale, String type) {
        String result = "";
        if (null == type || "".equals(type))
            type = "jpg";
        switch (scale) {
            case 1:
                break;
            case 0:
                result = "_s";
            case -1:
                result = "_xs";
                break;
            case -2:
                result = "_xxs";
                break;
            case -3:
                result = "_xxxs";
                break;
        }

        return url + result + "." + type;
    }


    public static String getImgSrc_portrait(String url, int scale) {

        if (scale == 0) return url;
        url = url.substring(0, url.lastIndexOf("."));
        System.out.println("url:" + url);
        return url + "_" + scale + ".png";
    }

    public static Uri changeImgUri(String uri, CategoryImgType t) {

        String[] array = uri.split("\\.");
        String type = "." + array[array.length - 1];
        String result = "";
        switch (t) {
            case NORMAL:
                result = uri.replace(type, "_normal" + type);
                break;
            case SELECTED:
                result = uri.replace(type, "_selected" + type);
                break;
            case DISABLED:
                result = uri.replace(type, "_disabled" + type);
                break;
        }
        return Uri.parse(result);
    }

    public enum CategoryImgType {
        NORMAL,
        SELECTED,
        DISABLED
    }
}

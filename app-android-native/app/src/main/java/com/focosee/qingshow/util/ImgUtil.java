package com.focosee.qingshow.util;

public class ImgUtil {

    public static String imgTo2x(String url) {
        String str = "";
        if (null != url)
            str = url.substring(0, url.length() - 4) + "@2x.jpg";
        return str;
    }

    public static String getImgSrc(String url, int scale) {
        String[] array = url.split("\\.");
        String type = "." + array[array.length - 1];

        String result = "";
        switch (scale) {
            case 1:
                break;
            case 0:
                result = url.replace(type, "_s" + type);
                break;
            case -1:
                result = url.replace(type, "_xs" + type);
                break;
            case -2:
                result = url.replace(type, "_xxs" + type);
                break;
            case -3:
                result = url.replace(type, "_xxxs" + type);
                break;
        }

        return result;
    }
}

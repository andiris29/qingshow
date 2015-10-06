package com.focosee.qingshow.util;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.focosee.qingshow.constants.config.QSAppWebAPI;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class ImgUtil {

    public static String LARGE = "s";
    public static String Meduim = "xs";
    public static String Normal = "xxs";
    public static String Small = "xxxs";

    public static String PORTRAIT_LARGE = "100";

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
            type = "png";
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


    public static String getImgSrc(String url, String scale) {
        if(TextUtils.isEmpty(url))return getUserDefaultPortrait();
        if (TextUtils.isEmpty(scale)) return url;
        String type = FilenameUtils.getExtension(url);
        url = url.substring(0, url.length() - type.length());
        if(TextUtils.isEmpty(type)) {
            return url + "_" + scale;
        }
        if(url.substring(url.length() - 1).equals("."))
            url = url.substring(0, url.length() - 1);
        Log.d(ImgUtil.class.getSimpleName(), "url:" + url);
        return url + "_" + scale + "." + type;
    }

    public static String getUserDefaultPortrait(){
        return QSAppWebAPI.USER_DEFAULT_PORTRAIT;
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

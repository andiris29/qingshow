package com.focosee.qingshow.config;

/**
 * Created by jackyu on 11/22/14.
 */
public class QSAppWebAPI {
    private static final String SITE_BASE = "http://121.41.162.102:30001";
        private static final String SHOW_LIST_API = SITE_BASE + "/services/feeding/chosen?pageNo=";
//    private static final String SHOW_LIST_API = SITE_BASE + "/services/feeding/hot?pageNo=";


    public static String getAbsoluteApi(String url) {
        if (!url.startsWith(SITE_BASE)) {
            url = (url.startsWith("/")) ? SITE_BASE + url : SITE_BASE + "/" + url;
        }
        return url;
    }


    public static String getShowListApi(int pageIndex, int pageSize) {
        return SHOW_LIST_API + String.valueOf(pageIndex) + "&pageSize=" + String.valueOf(pageSize);
    }
}

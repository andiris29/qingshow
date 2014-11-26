package com.focosee.qingshow.config;

/**
 * Created by jackyu on 11/22/14.
 */
public class QSAppWebAPI {
    private static final String SITE_BASE = "http://chingshow.com:30001";
        private static final String SHOW_LIST_API = SITE_BASE + "/services/feeding/chosen?pageNo=";
//    private static final String SHOW_LIST_API = SITE_BASE + "/services/feeding/hot?pageNo=";


    public static String getShowListApi(int pageIndex, int pageSize) {
        return SHOW_LIST_API + String.valueOf(pageIndex) + "&pageSize=" + String.valueOf(pageSize);
    }
}

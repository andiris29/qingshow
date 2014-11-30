package com.focosee.qingshow.config;

/**
 * Created by jackyu on 11/22/14.
 */
public class QSAppWebAPI {
    public static final String LOGIN_SERVICE_URL = "http://121.41.162.102:30001/services/user/login";
    public static final String REGISTER_SERVICE_URL = "http://121.41.162.102:30001/services/user/register";
    public static final String UPDATE_SERVICE_URL = "http://121.41.162.102:30001/services/user/update";

    private static final String SITE_BASE = "http://chingshow.com:30001";
    private static final String SHOW_LIST_API = SITE_BASE + "/services/feeding/chosen?pageNo=";
//  private static final String SHOW_LIST_API = SITE_BASE + "/services/feeding/hot?pageNo=";


    public static String getShowListApi(int pageIndex, int pageSize) {
        return SHOW_LIST_API + String.valueOf(pageIndex) + "&pageSize=" + String.valueOf(pageSize);
    }
}

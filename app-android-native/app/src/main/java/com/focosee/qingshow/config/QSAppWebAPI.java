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
    private static final String[] SHOW_LIST_CATEGORY_API = {"/services/feeding/chosen?pageNo=", "/services/feeding/hot?pageNo=", "/services/feeding/chosen?pageNo="};
    private static final String SHOW_COMMENTS_LIST_API = SITE_BASE + "/services/show/queryComments";


    public static String getShowListApi(int pageIndex, int pageSize) {
        return SHOW_LIST_API + String.valueOf(pageIndex) + "&pageSize=" + String.valueOf(pageSize);
    }

    public static String getShowCategoryListApi(int category, int pageIndex, int pageSize) {
        if (SHOW_LIST_CATEGORY_API.length <= category)
            return null;
        return SITE_BASE + SHOW_LIST_CATEGORY_API[category] + String.valueOf(pageIndex) + "&pageSize=" + String.valueOf(pageSize);
    }

    public static String getShowCommentsListApi(String showId, int pageIndex, int pageSize) {
        return SHOW_COMMENTS_LIST_API + "?_id=" + showId + "&pageNo" + String.valueOf(pageIndex) + "pageSize" + String.valueOf(pageSize);
    }
}

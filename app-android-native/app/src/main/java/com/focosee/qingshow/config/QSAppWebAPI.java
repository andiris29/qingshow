package com.focosee.qingshow.config;

/**
 * Created by jackyu on 11/22/14.
 */
public class QSAppWebAPI {
    public static final String LOGIN_SERVICE_URL = "http://121.41.162.102:30001/services/user/login";
    public static final String REGISTER_SERVICE_URL = "http://121.41.162.102:30001/services/user/register";
    public static final String UPDATE_SERVICE_URL = "http://121.41.162.102:30001/services/user/update";

    private static final String HOST_ADDRESS = "http://chingshow.com:30001";
    private static final String HOST_NAME = HOST_ADDRESS + "/services";
    private static final String kImageUrlBase = HOST_ADDRESS + "/images";
    private static final String kVideoUrlBase = HOST_ADDRESS + "/videos";


    private static final String SHOW_LIST_API = HOST_NAME + "/feeding/chosen";
    private static final String SHOW_DETAIL_API = HOST_NAME + "/show/query";
    private static final String[] SHOW_LIST_CATEGORY_API = {"/feeding/chosen?pageNo=", "/feeding/hot?pageNo=", "/feeding/chosen?pageNo="};
    private static final String SHOW_COMMENTS_LIST_API = HOST_NAME + "/show/queryComments";
    private static final String PEOPLE_QUERY_MODELS_API = HOST_NAME + "/people/queryModels";


    public static String getShowListApi(int pageIndex, int pageSize) {
        return SHOW_LIST_API + "?pageNo=" + String.valueOf(pageIndex) + "&pageSize=" + String.valueOf(pageSize);
    }

    public static String getShowDetailApi(String showId) {
        return SHOW_DETAIL_API + "?_ids=" + showId;
    }

    public static String getShowCategoryListApi(int category, int pageIndex, int pageSize) {
        if (SHOW_LIST_CATEGORY_API.length <= category)
            return null;
        return HOST_NAME + SHOW_LIST_CATEGORY_API[category] + String.valueOf(pageIndex) + "&pageSize=" + String.valueOf(pageSize);
    }

    public static String getShowCommentsListApi(String showId, int pageIndex, int pageSize) {
        return SHOW_COMMENTS_LIST_API + "?_id=" + showId + "&pageNo" + String.valueOf(pageIndex) + "pageSize" + String.valueOf(pageSize);
    }

    public static String getModelListApi(String pageNo, String pageSize) {
        return PEOPLE_QUERY_MODELS_API + "?pageNo=" + pageNo + "&pageSize=" + pageSize;
    }
}

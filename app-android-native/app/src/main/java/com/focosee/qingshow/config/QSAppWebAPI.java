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
    private static final String[] SHOW_LIST_CATEGORY_API = {"/feeding/chosen?pageNo=", "/feeding/hot?pageNo=", "/feeding/studio?pageNo="};

    private static final String SHOW_COMMENTS_LIST_API = HOST_NAME + "/show/queryComments";
    private static final String COMMENT_POST_API = HOST_NAME + "/show/comment";
    private static final String COMMENT_DELETE_API = HOST_NAME + "/show/deleteComment";

    private static final String PEOPLE_QUERY_MODELS_API = HOST_NAME + "/people/queryModels";
    private static final String MODEL_DETAIL_API = HOST_NAME + "/feeding/byModel";
    private static final String BRAND_LIST_API = HOST_NAME + "/brand/queryBrands";

    private static final String PEOPLE_FOLLOW_API = HOST_NAME + "/people/follow";
    private static final String PEOPLE_UNFOLLOW_API = HOST_NAME + "/people/unfollow";

    private static final String SHOW_LIKE_API = HOST_NAME + "/show/like";
    private static final String SHOW_UNLIKE_API = HOST_NAME + "/show/unlike";

    private static final String BRAND_NEWEST_API = HOST_NAME + "/feeding/byBrandNew";
    private static final String BRAND_DISCOUNT_API = HOST_NAME + "/feeding/byBrandDiscount";

    private static final String QUERY_PEOPLE_FOLLOWER_API = HOST_NAME + "/people/queryFollowers";
    private static final String QUERY_PEOPLE_FOLLOWED_API = HOST_NAME + "/people/queryFollowed";

    private static final String PREVIEW_TREND_LIST_API = HOST_NAME + "/preview/feed";

    //author:Chenhr
    public static String getPreviewTrendListApi(int pageIndex, int pageSize){
        return PREVIEW_TREND_LIST_API + "?pageNo=" + String.valueOf(pageIndex) + "&pageSize=" + String.valueOf(pageSize);
    }

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

    public static String getCommentPostApi() {
        return COMMENT_POST_API;
    }

    public static String getCommentDeleteApi() {
        return COMMENT_DELETE_API;
    }

    public static String getModelListApi(String pageNo, String pageSize) {
        return PEOPLE_QUERY_MODELS_API + "?pageNo=" + pageNo + "&pageSize=" + pageSize;
    }

    public static String getBrandListApi(String type, String page) {
        return BRAND_LIST_API + "?type=" + type + "&page=" + page;
    }

    public static String getPeopleFollowApi() {
        return PEOPLE_FOLLOW_API;
    }

    public static String getPeopleUnfollowApi() {
        return PEOPLE_UNFOLLOW_API;
    }

    public static String getShowLikeApi() {
        return SHOW_LIKE_API;
    }

    public static String getShowUnlikeApi() {
        return SHOW_UNLIKE_API;
    }

    public static String getModelShowsApi(String modelId, String pageNo) {
        return MODEL_DETAIL_API + "?_id=" + modelId + "&pageNo=" + pageNo + "&pageSize=10";
    }

    public static String getQueryPeopleFollowerApi(String modelId, String pageNo) {
        return QUERY_PEOPLE_FOLLOWER_API + "?_id=" + modelId + "&pageNo=" + pageNo + "&paegSize=10";

    }

    public static String getQueryPeopleFollowedApi(String modelId, String pageNo) {
        return QUERY_PEOPLE_FOLLOWED_API + "?_id=" + modelId + "&pageNo=" + pageNo + "&paegSize=10";
    }

    public static String getBrandNewestApi(String brandId, String pageNo) {
        return BRAND_NEWEST_API + "?_id=" + brandId + "&pageNo=" + pageNo + "&paegSize=10";
    }

    public static String getBrandDiscountApi(String brandId, String pageNo) {
        return BRAND_DISCOUNT_API + "?_id=" + brandId + "&pageNo=" + pageNo + "&paegSize=10";
    }

}

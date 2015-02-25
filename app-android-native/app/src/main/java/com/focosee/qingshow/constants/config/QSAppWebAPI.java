package com.focosee.qingshow.constants.config;

/**
 * Created by jackyu on 11/22/14.
 */
public class QSAppWebAPI {
    private static final String HOST_ADDRESS = "http://chingshow.com:30001";
    private static final String HOST_NAME = HOST_ADDRESS + "/services";

    public static final String LOGIN_SERVICE_URL = HOST_NAME + "/user/login";
    public static final String REGISTER_SERVICE_URL = HOST_NAME + "/user/register";
    public static final String UPDATE_SERVICE_URL = HOST_NAME + "/user/update";
    public static final String LOGOUT_SERVICE_URL = HOST_NAME + "/user/logout";
    public static final String GET_SERVICE_URL = HOST_NAME + "/user/get";

    public static final String People_Query_Followed = HOST_NAME + "/people/queryFollowed";

    private static final String kImageUrlBase = HOST_ADDRESS + "/images";
    private static final String kVideoUrlBase = HOST_ADDRESS + "/videos";

    private static final String FEEDING_LIKE = HOST_NAME + "/feeding/like";
    private static final String FEEDING_RECOMMENDATION = HOST_NAME + "/feeding/recommendation";

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

    private static final String BRAND_FOLLOWED_API = HOST_NAME + "/brand/queryFollowed";
    private static final String BRAND_FOLLOW_API = HOST_NAME + "/brand/follow";
    private static final String BRAND_UNFOLLOW_API = HOST_NAME + "/brand/unfollow";

//    private static final String BRAND_NEWEST_API = HOST_NAME + "/feeding/byBrandNew";
//    private static final String BRAND_DISCOUNT_API = HOST_NAME + "/feeding/byBrandDiscount";

    private static final String BRAND_MATCH_API = HOST_NAME + "/itemFeeding/byBrandNew";
    private static final String BRAND_DISCOUNT_API = HOST_NAME + "/itemFeeding/byBrandDiscount";
    private static final String BRAND_SHOW_API = HOST_NAME + "/feeding/byBrand";
    private static final String BRAND_QUERY_API = HOST_NAME + "/brand/query";

    private static final String QUERY_PEOPLE_FOLLOWER_API = HOST_NAME + "/people/queryFollowers";
    private static final String QUERY_PEOPLE_FOLLOWED_API = HOST_NAME + "/people/queryFollowed";

    private static final String PREVIEW_TREND_LIST_API = HOST_NAME + "/preview/feed";
    private static final String PREVIEW_TREND_LIKE_API = HOST_NAME + "/preview/like";
    private static final String PREVIEW_TREND_UNLIKE_API = HOST_NAME + "/preview/unlike";

    private static final String USER_UPDATEPORTRAIT = HOST_NAME + "/user/updatePortrait";
    private static final String USER_UPDATEBACKGROUND = HOST_NAME + "/user/updateBackground";
    private static final String ITEM_RANDOM = HOST_NAME +"/itemFeeding/random";

    public static String getBrandFollowApi(){
        return BRAND_FOLLOW_API;
    }

    public static String getBrandUnfollowApi(){
        return BRAND_UNFOLLOW_API;
    }

    public static String getBrandQueryApi(String _ids){
        return BRAND_QUERY_API + "?_ids=" + _ids;
    }

    public static String getBrandFollowedApi(String _id, int pageNo, int pageSize) {
        return BRAND_FOLLOWED_API + "?_id=" + _id + "&pageNo=" + pageNo + "&pageSize=" + pageSize;
    }

    public static String getUserApi() {
        return GET_SERVICE_URL;
    }

    public static String getUserApi(String _id) {
        return GET_SERVICE_URL + "?id=" + _id;
    }

    public static String getPreviewTrendLikeApi(int type) {
        if (0 == type) return PREVIEW_TREND_UNLIKE_API;
        return PREVIEW_TREND_LIKE_API;
    }

    public static String getPreviewTrendListApi(int pageIndex, int pageSize) {
        return PREVIEW_TREND_LIST_API + "?pageNo=" + String.valueOf(pageIndex) + "&pageSize=" + String.valueOf(pageSize);
    }

    public static String getShowListApi(int pageIndex, int pageSize) {
        return SHOW_LIST_API + "?pageNo=" + String.valueOf(pageIndex) + "&pageSize=" + String.valueOf(pageSize);
    }

    public static String getShowHotApi(int pageIndex, int pageSize){
        return HOST_NAME + SHOW_LIST_CATEGORY_API[1] + String.valueOf(pageIndex) + "&pageSize=" + String.valueOf(pageSize);
    }

    public static String getFeedingLikeApi(String _id, int pageIndex, int pageSize) {
        return FEEDING_LIKE + "?_id=" + _id + "&pageNo=" + pageIndex + "&pageSize=" + pageSize;
    }

    public static String getFeedingRecommendationApi(String _id, int pageIndex, int pageSize) {
        return FEEDING_RECOMMENDATION + "?_id=" + _id + "&pageNo=" + String.valueOf(pageIndex) + "&pageSize=" + String.valueOf(pageSize);
    }

    public static String getPeopleQueryFollowedApi(String _id, int pageIndex, int pageSize) {
        return People_Query_Followed + "?_id=" + _id + "&pageNo=" + String.valueOf(pageIndex) + "&pageSize=" + String.valueOf(pageSize);
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
        return SHOW_COMMENTS_LIST_API + "?_id=" + showId + "&pageNo=" + pageIndex + "pageSize=" + pageSize;
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

    public static String getBrandListApi(String type, String pageNo) {
        return BRAND_LIST_API + "?type=" + type + "&pageNo=" + pageNo + "&pageSize=10";
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

//    public static String getBrandNewestApi(String brandId, String pageNo) {
//        return BRAND_NEWEST_API + "?_id=" + brandId + "&pageNo=" + pageNo + "&paegSize=10";
//    }
//
//    public static String getBrandDiscountApi(String brandId, String pageNo) {
//        return BRAND_DISCOUNT_API + "?_id=" + brandId + "&pageNo=" + pageNo + "&paegSize=10";
//    }

    public static String getBrandMatchApi(String brandId, String pageNo) {
        return BRAND_MATCH_API + "?_id=" + brandId + "&pageNo=" + pageNo + "&paegSize=10";
    }

    public static String getBrandDiscountApi(String brandId, String pageNo) {
        return BRAND_DISCOUNT_API + "?_id=" + brandId + "&pageNo=" + pageNo + "&paegSize=10";
    }

    public static String getBrandShowApi(String brandId, String pageNo) {
        return BRAND_SHOW_API + "?_id=" + brandId + "&pageNo=" + pageNo + "&paegSize=10";
    }

    public static String getitemRandomApi(int pageIndex, int pageSize){

        return ITEM_RANDOM + "?pageNo=" +String.valueOf(pageIndex) + "&pageSize=" + String.valueOf(pageSize);
    }

    public static String getUserUpdatebackground(){
        return USER_UPDATEBACKGROUND;
    }

    public static String getUserUpdateportrait(){
        return USER_UPDATEPORTRAIT;
    }
}

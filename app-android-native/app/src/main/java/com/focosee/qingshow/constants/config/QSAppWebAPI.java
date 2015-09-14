package com.focosee.qingshow.constants.config;

/**
 * Created by jackyu on 11/22/14.
 */
public class QSAppWebAPI {
    public static final String USER_DEFAULT_PORTRAIT = "http://trial01.focosee.com/img//user/portrait/1.png";//识别头像是否是用倾秀上传
    public static String HOST_ADDRESS_PAYMENT = "";
    public static String HOST_ADDRESS_APPWEB = "";

    public static String HOST_NAME = "";

    private static String LOGIN_SERVICE_URL = "/user/login";
    private static String REGISTER_SERVICE_URL = "/user/register";
    private static String UPDATE_SERVICE_URL = "/user/update";
    private static String GET_SERVICE_URL = "/user/get";
    private static String USER_SAVE_RECEIVER_API = "/user/saveReceiver";
    private static String USER_REMOVE_RECEIVER_API = "/user/removeReceiver";
    private static String USER_LOGIN_WX_API = "/user/loginViaWeixin";

    private static String USER_LOGOUT = "/user/logout";

    private static String USER_LOGIN_WB_API = "/user/loginViaWeibo";
    private static String FEEDING_LIKE = "/feeding/like";
    private static String SHOW_DETAIL_API = "/show/query";

    private static String SHOW_COMMENTS_LIST_API = "/show/queryComments";
    private static String COMMENT_POST_API = "/show/comment";
    private static String COMMENT_DELETE_API = "/show/deleteComment";

    private static String PEOPLE_FOLLOW_API = "/people/follow";
    private static String PEOPLE_UNFOLLOW_API = "/people/unfollow";

    private static String SHOW_LIKE_API = "/show/like";
    private static String SHOW_UNLIKE_API = "/show/unlike";

    private static String QUERY_PEOPLE_FOLLOWER_API = "/people/queryFollowers";

    private static String PREVIEW_QUERYCOMMENTS_API = "/preview/queryComments";
    private static String PREVIEW_COMMENT_POST_API = "/preview/comment";
    private static String PREVIEW_COMMENT_DEL_API = "/preview/deleteComment";

    private static String USER_UPDATEPORTRAIT = "/user/updatePortrait";
    private static String USER_UPDATEBACKGROUND = "/user/updateBackground";

    private static String TRADE_CREATE_API = "/trade/create";
    private static String TRADE_QUERY_API = "/trade/queryCreatedBy";
    private static String TRADE_QUERY = "/trade/query";
    private static String TRADE_STATUSTO_API = "/trade/statusTo";
    private static String TRADE_REFRESH = "/trade/refreshPaymentStatus";

    private static String TRADE_GET_RETURNRECEIVER = "/trade/getReturnReceiver";
    private static String TOP_API = "/feeding/hot";
    private static String BYDATE_API = "/feeding/byRecommendDate";

    private static String SPREAD_FIRSTLANUCH_API = "/spread/firstLaunch";


    private static String MATCH_HOT_API = "/feeding/matchHot";
    private static String MATCH_NEW_API = "/feeding/matchNew";

    private static String QUERY_CATEGORIES = "/matcher/queryCategories";

    private static String QUERY_ITEMS = "/matcher/queryItems";
    private static String MATCH_SAVE = "/matcher/save";
    private static String UPDATE_COVER = "/matcher/updateCover";
    private static String MATCH_HIDE_API = "/matcher/hide";

    private static String MATCH_CREATEDBY_API = "/feeding/matchCreatedBy";

    private static String PEOPLE_QUERY_FOLLOW_PEOPLES_API = "/people/queryFollowingPeoples";
    private static String PEOPLE_QUERY_API = "/people/query";
    private static String PAY_API = "/trade/prepay";
    private static String TRADE_SHARE_API = "/trade/share";

    private static String FEEDING_RECOMMENDATION_API = "/feeding/recommendation";

    private static String TRADE_QUERYBY_PHASE_API = "/trade/queryByPhase";
    private static String ITEM_SYNC_API = "/item/sync";

    private static String TRADE_QUERY_HIGHLIGHTED_API = "/trade/queryHighlighted";

    private static String USER_BONUS_WITHDRAW_API = "/userBonus/withdraw";

    private static String ITEM_QUERY_API = "/item/query";

    private static String REQUEST_VERIFICATION_CODE_API = "/user/requestVerificationCode";

    private static String VALIDATE_MOBILE_API = "/user/validateMobile";

    public static String getUserLogout() {
        return USER_LOGOUT;
    }

    public static String getLoginServiceUrl() {
        return HOST_NAME + LOGIN_SERVICE_URL;
    }

    public static String getRegisterServiceUrl() {
        return HOST_NAME + REGISTER_SERVICE_URL;
    }

    public static String getUpdateServiceUrl() {
        return HOST_NAME + UPDATE_SERVICE_URL;
    }

    public static String getRequestVerificationCodeApi() {
        return HOST_NAME + REQUEST_VERIFICATION_CODE_API;
    }

    public static String getValidateMobileApi() {
        return HOST_NAME + VALIDATE_MOBILE_API;
    }

    public static String getItemQueryApi(String _ids) {
        return HOST_NAME + ITEM_QUERY_API + "?_ids=" + _ids;
    }

    public static String getUserBonusWithdrawApi() {
        return HOST_NAME + USER_BONUS_WITHDRAW_API;
    }

    public static String getTradeQueryHighlightedApi(int pageNo, int pageSize) {
        return HOST_NAME + TRADE_QUERY_HIGHLIGHTED_API + "?pageNo=" + pageNo + "&pageSize=" + pageSize;
    }

    public static String getTradeGetReturnreceiver(String _id) {
        return HOST_NAME + TRADE_GET_RETURNRECEIVER + "?_id=" + _id;
    }

    public static String getTradeQuerybyPhaseApi(String phases, int pageNo, int pageSize) {
        return HOST_NAME + TRADE_QUERYBY_PHASE_API + "?phases=" + phases + "&pageNo=" + pageNo + "&pageSize=" + pageSize;
    }

    public static String getItemSyncApi() {
        return HOST_NAME + ITEM_SYNC_API;
    }

    public static String getMatchHideApi() {
        return HOST_NAME + MATCH_HIDE_API;
    }

    public static String getPayApi() {
        return HOST_NAME + PAY_API;
    }

    public static String getTradeShareApi() {
        return HOST_NAME + TRADE_SHARE_API;
    }

    public static String getPeopleQueryApi(String _ids) {
        return HOST_NAME + PEOPLE_QUERY_API + "?_ids=" + _ids;
    }

    public static String getMatchSaveApi() {
        return HOST_NAME + MATCH_SAVE;
    }

    public static String getUpdateMatchCoverApi() {
        return HOST_NAME + UPDATE_COVER;
    }

    public static String getUserLoginWbApi() {
        return HOST_NAME + USER_LOGIN_WB_API;
    }

    public static String getUserLoginWxApi() {
        return HOST_NAME + USER_LOGIN_WX_API;
    }

    public static String getQueryItems(int pageNo, int pageSize, String categoryRef) {
        return HOST_NAME + QUERY_ITEMS + "?pageNo=" + pageNo + "&pageSize=" + pageSize + "&categoryRef=" + categoryRef;
    }

    public static String getPeopleQueryFollowPeoplesApi(String _id, int pageNo, int pageSize) {
        return HOST_NAME + PEOPLE_QUERY_FOLLOW_PEOPLES_API + "?_id=" + _id + "&pageNo=" + pageNo + "&pageSize=" + pageSize;
    }

    public static String getMatchCreatedbyApi(String _id, int pageNo, int pageSize) {
        return HOST_NAME + MATCH_CREATEDBY_API + "?_id=" + _id + "&pageNo=" + pageNo + "&pageSize=" + pageSize;
    }

    public static String getMatchHotApi(int pageNo, int pageSize) {
        return HOST_NAME + MATCH_HOT_API + "?pageNo=" + pageNo + "&pageSize=" + pageSize;
    }

    public static String getMatchNewApi(int pageNo, int pageSize) {
        return HOST_NAME + MATCH_NEW_API + "?pageNo=" + pageNo + "&pageSize=" + pageSize;
    }

    public static String getQueryCategories() {
        return HOST_NAME + QUERY_CATEGORIES;
    }


    public static String getSpreadFirstlanuchApi() {
        return HOST_NAME + SPREAD_FIRSTLANUCH_API;
    }


    public static String getBydateApi(String date) {
        return HOST_NAME + BYDATE_API + "?date=" + date;
    }

    public static String getTopApi() {
        return HOST_NAME + TOP_API;
    }

    public static String getFeedingRecommendationApi() {
        return HOST_NAME + FEEDING_RECOMMENDATION_API;
    }

    public static String getTradeRefreshApi() {
        return HOST_NAME + TRADE_REFRESH;
    }

    public static String getUserRemoveReceiverApi() {
        return HOST_NAME + USER_REMOVE_RECEIVER_API;
    }

    public static String getTradeCreateApi() {
        return HOST_NAME + TRADE_CREATE_API;
    }

    public static String getUserSaveReceiverApi() {
        return HOST_NAME + USER_SAVE_RECEIVER_API;
    }

    public static String getTradeStatustoApi() {
        return HOST_NAME + TRADE_STATUSTO_API;
    }

    public static String getTradeQueryApi(String _id, int pageNo, int pageSize, boolean inProgress) {
        return HOST_NAME + TRADE_QUERY_API + "?_id=" + _id + "&inProgress=" + inProgress + "&pageNo=" + pageNo + "&pageSize=" + pageSize;
    }

    public static String getTradeApi(String _id){
        return HOST_NAME + TRADE_QUERY + "?_ids=" + _id;
    }

    public static String getUserApi() {
        return HOST_NAME + GET_SERVICE_URL;
    }

    public static String getPreviewQuerycommentsApi(String _id, int pageIndex, int pageSize) {
        return HOST_NAME + PREVIEW_QUERYCOMMENTS_API + "?_id=" + _id + "&pageNo=" + pageIndex + "&pageSize=" + pageSize;
    }

    public static String getFeedingLikeApi(String _id, int pageNo, int pageSize) {
        return HOST_NAME + FEEDING_LIKE + "?_id=" + _id + "&pageNo=" + pageNo + "&pageSize=" + pageSize;
    }

    public static String getShowDetailApi(String showId) {
        return HOST_NAME + SHOW_DETAIL_API + "?_ids=" + showId;
    }

    public static String getShowCommentsListApi(String showId, int pageIndex, int pageSize) {
        return HOST_NAME + SHOW_COMMENTS_LIST_API + "?_id=" + showId + "&pageNo=" + pageIndex + "pageSize=" + pageSize;
    }

    public static String getCommentPostApi(int API_TYPE) {
        if (API_TYPE == 1) {//preview
            return HOST_NAME + PREVIEW_COMMENT_POST_API;
        }
        return HOST_NAME + COMMENT_POST_API;
    }

    public static String getCommentDeleteApi(int API_TYPE) {
        if (API_TYPE == 1) {
            return HOST_NAME + PREVIEW_COMMENT_DEL_API;
        }
        return HOST_NAME + COMMENT_DELETE_API;
    }

    public static String getPeopleFollowApi() {
        return HOST_NAME + PEOPLE_FOLLOW_API;
    }

    public static String getPeopleUnfollowApi() {
        return HOST_NAME + PEOPLE_UNFOLLOW_API;
    }

    public static String getShowLikeApi() {
        return HOST_NAME + SHOW_LIKE_API;
    }

    public static String getShowUnlikeApi() {
        return HOST_NAME + SHOW_UNLIKE_API;
    }

    public static String getQueryPeopleFollowerApi(String _id, int pageNo, int pageSize) {
        return HOST_NAME + QUERY_PEOPLE_FOLLOWER_API + "?_id=" + _id + "&pageNo=" + pageNo + "&paegSize=" + pageSize;
    }

    public static String getUserUpdatebackground() {
        return HOST_NAME + USER_UPDATEBACKGROUND;
    }

    public static String getUserUpdateportrait() {
        return HOST_NAME + USER_UPDATEPORTRAIT;
    }
}

package com.focosee.qingshow.constants.config;

import com.focosee.qingshow.QSApplication;

/**
 * Created by jackyu on 11/22/14.
 */
public class QSAppWebAPI {
   // public static final String USER_DEFAULT_PORTRAIT = "http://trial01.focosee.com/img//user/portrait/1.png";//识别头像是否是用倾秀上传
    //http://trial01.focosee.com/img/user/default_portrait_50.png
   public static final String USER_DEFAULT_PORTRAIT = "http://trial01.focosee.com/img/user/default_portrait_50.png";
    public static final String host_name = "HOST_NAEM";
    public static final String host_address_payment = "HOST_ADDRESS_PAYMENT";
    public static final String host_address_appweb = "HOST_ADDRESS_APPWEB";
    public static String HOST_ADDRESS_PAYMENT = "";
    public static String HOST_ADDRESS_APPWEB = "";

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

    private static String TRADE_OWN_API = "/trade/own";
    private static String ITEM_SYNC_API = "/item/sync";

    private static String TRADE_QUERY_HIGHLIGHTED_API = "/trade/queryHighlighted";

    private static String USER_BONUS_WITHDRAW_API = "/userBonus/withdraw";

    private static String ITEM_QUERY_API = "/item/query";

    private static String REQUEST_VERIFICATION_CODE_API = "/user/requestVerificationCode";

    private static String VALIDATE_MOBILE_API = "/user/validateMobile";

    private static String READ_EXPECTABLE_TRADE_API = "/user/readNotification";

    private static String SYSTEM_LOG_API = "/system/log";

    private static String RESET_PASSWORD = "/user/resetPassword";

    private static String FEEDING_FEATURED = "/feeding/featured";

    private static String SHARE_QUERY_API = "/share/query";

    private static String SHARE_CREATE_SHOW_API = "/share/createShow";

    private static String SHARE_CREATE_TRADE_API = "/share/createTrade";

   // private static String SHARE_CREATE_BONUS_API = "/share/createBonus";
   private static String SHARE_CREATE_BONUS_API = "/bonus/withdraw";
  // private static String SHARE_CREATE_BONUS_API = "share/withdraw";
    private static String USER_LOGINASGUEST_API = "/user/loginAsGuest";

    private static String USER_UPDATEREGISTRATIONID_API = "/user/bindJPush";

    private static String FEEDINGAGGREGATION_LATEST = "/feedingAggregation/latest";

    private static String SHOW_VIEW_API = "/show/view";

    private static String REMIX_BY_MODEL = "/matcher/remixByModel";

    private static String REMIX_BY_ITEM = "/matcher/remixByItem";
    private static final String FEEDING_TIME = "/feeding/time";

    private static String QUERY_BUYERS = "/people/queryBuyers";

    private static String GETCONFIG = "/system/getConfig";

    private static String BIND_MOBILE_API = "/bindMobile";

    public static String getBindMobileApi() {
        return BIND_MOBILE_API;
    }

    public static String getQueryBonus(String... _ids) {
        StringBuffer params = new StringBuffer();
        for (String _id : _ids) {
            params.append("_ids=");
            params.append(_id);
            params.append("&");
        }
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + QUERY_BONUS + "?" + params.substring(0, params.length() - 1);
    }

    private static String QUERY_BONUS = "/bonus/query";

    public static String getBonusOwn() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + "/bonus/own";
    }

    public static String getConfig() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + GETCONFIG;
    }

    public static String getQueryBuyers() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + QUERY_BUYERS;
    }


    public static String getFeedingaggregationLatest() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + FEEDINGAGGREGATION_LATEST;
    }

    public static String getShowViewApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "");
    }

    public static String getUserUpdateregistrationidApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + USER_UPDATEREGISTRATIONID_API;
    }

    public static String getShareCreateShowApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + SHARE_CREATE_SHOW_API;
    }

    public static String getShareCreateTradeApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + SHARE_CREATE_TRADE_API;
    }

    public static String getShareCreateBonusApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + SHARE_CREATE_BONUS_API;
    }

    public static String getShareQueryApi(String _id) {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + SHARE_QUERY_API + "?_ids=" + _id;
    }

    public static String getUserLoginasguestApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + USER_LOGINASGUEST_API;
    }

    public static String getFeedingFeatured(int pageNo, int pageSize) {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + FEEDING_FEATURED + "?pageNo=" + pageNo + "&pageSize=" + pageSize;
    }

    public static String getResetPassword() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + RESET_PASSWORD;
    }

    public static String getSystemLogApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + SYSTEM_LOG_API;
    }

    public static String getReadNotificationTradeApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + READ_EXPECTABLE_TRADE_API;
    }

    public static String getUserLogout() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + USER_LOGOUT;
    }

    public static String getLoginServiceUrl() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + LOGIN_SERVICE_URL;
    }

    public static String getRegisterServiceUrl() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + REGISTER_SERVICE_URL;
    }

    public static String getUpdateServiceUrl() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + UPDATE_SERVICE_URL;
    }

    public static String getRequestVerificationCodeApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + REQUEST_VERIFICATION_CODE_API;
    }

    public static String getValidateMobileApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + VALIDATE_MOBILE_API;
    }

    public static String getItemQueryApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + ITEM_QUERY_API;
    }

    public static String getUserBonusWithdrawApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + USER_BONUS_WITHDRAW_API;
    }

    public static String getTradeQueryHighlightedApi(int pageNo, int pageSize) {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + TRADE_QUERY_HIGHLIGHTED_API + "?pageNo=" + pageNo + "&pageSize=" + pageSize;
    }

    public static String getTradeGetReturnreceiver(String _id) {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + TRADE_GET_RETURNRECEIVER + "?_id=" + _id;
    }

    public static String getTradeOwn(int pageNo, int pageSize) {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + TRADE_OWN_API + "?pageNo=" + pageNo + "&pageSize=" + pageSize;
    }

    public static String getItemSyncApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + ITEM_SYNC_API;
    }

    public static String getMatchHideApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + MATCH_HIDE_API;
    }

    public static String getPayApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + PAY_API;
    }

    public static String getTradeShareApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + TRADE_SHARE_API;
    }

    public static String getPeopleQueryApi(String... _ids) {
        StringBuffer params = new StringBuffer();
        for (String _id : _ids) {
            params.append("_ids=");
            params.append(_id);
            params.append("&");
        }
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + PEOPLE_QUERY_API + "?" + params.substring(0, params.length() - 1);
    }

    public static String getMatchSaveApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + MATCH_SAVE;
    }

    public static String getUpdateMatchCoverApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + UPDATE_COVER;
    }

    public static String getUserLoginWbApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + USER_LOGIN_WB_API;
    }

    public static String getUserLoginWxApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + USER_LOGIN_WX_API;
    }

    public static String getQueryItems(int pageNo, int pageSize, String categoryRef) {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + QUERY_ITEMS + "?pageNo=" + pageNo + "&pageSize=" + pageSize + "&categoryRef=" + categoryRef;
    }

    public static String getPeopleQueryFollowPeoplesApi(String _id, int pageNo, int pageSize) {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + PEOPLE_QUERY_FOLLOW_PEOPLES_API + "?_id=" + _id + "&pageNo=" + pageNo + "&pageSize=" + pageSize;
    }

    public static String getMatchCreatedbyApi(String _id, int pageNo, int pageSize) {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + MATCH_CREATEDBY_API + "?_id=" + _id + "&pageNo=" + pageNo + "&pageSize=" + pageSize;
    }

    public static String getMatchHotApi(int pageNo, int pageSize) {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + MATCH_HOT_API + "?pageNo=" + pageNo + "&pageSize=" + pageSize;
    }

    public static String getFeedingTimeApi(int pageNo, int pageSize, String from, String to) {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + FEEDING_TIME + "?pageNo=" + pageNo + "&pageSize=" + pageSize + "&from=" + from + "&to=" + to;
    }

    public static String getQueryCategories() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + QUERY_CATEGORIES;
    }


    public static String getSpreadFirstlanuchApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + SPREAD_FIRSTLANUCH_API;
    }


    public static String getBydateApi(String date) {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + BYDATE_API + "?date=" + date;
    }

    public static String getTopApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + TOP_API;
    }

    public static String getFeedingRecommendationApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + FEEDING_RECOMMENDATION_API;
    }

    public static String getTradeRefreshApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + TRADE_REFRESH;
    }

    public static String getUserRemoveReceiverApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + USER_REMOVE_RECEIVER_API;
    }

    public static String getTradeCreateApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + TRADE_CREATE_API;
    }

    public static String getUserSaveReceiverApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + USER_SAVE_RECEIVER_API;
    }

    public static String getTradeStatustoApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + TRADE_STATUSTO_API;
    }

    public static String getTradeQueryApi(String _id, int pageNo, int pageSize, boolean inProgress) {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + TRADE_QUERY_API + "?_id=" + _id + "&inProgress=" + inProgress + "&pageNo=" + pageNo + "&pageSize=" + pageSize;
    }

    public static String getTradeApi(String _id) {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + TRADE_QUERY + "?_ids=" + _id;
    }

    public static String getUserApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + GET_SERVICE_URL;
    }

    public static String getFeedingLikeApi(String _id, int pageNo, int pageSize) {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + FEEDING_LIKE + "?_id=" + _id + "&pageNo=" + pageNo + "&pageSize=" + pageSize;
    }

    public static String getShowDetailApi(String showId) {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + SHOW_DETAIL_API + "?_ids=" + showId;
    }

    public static String getShowCommentsListApi(String showId, int pageIndex, int pageSize) {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + SHOW_COMMENTS_LIST_API + "?_id=" + showId + "&pageNo=" + pageIndex + "pageSize=" + pageSize;
    }

    public static String getCommentPostApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + COMMENT_POST_API;
    }

    public static String getCommentDeleteApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + COMMENT_DELETE_API;
    }

    public static String getPeopleFollowApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + PEOPLE_FOLLOW_API;
    }

    public static String getPeopleUnfollowApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + PEOPLE_UNFOLLOW_API;
    }

    public static String getShowLikeApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + SHOW_LIKE_API;
    }

    public static String getShowUnlikeApi() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + SHOW_UNLIKE_API;
    }

    public static String getQueryPeopleFollowerApi(String _id, int pageNo, int pageSize) {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + QUERY_PEOPLE_FOLLOWER_API + "?_id=" + _id + "&pageNo=" + pageNo + "&paegSize=" + pageSize;
    }

    public static String getUserUpdatebackground() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + USER_UPDATEBACKGROUND;
    }

    public static String getUserUpdateportrait() {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + USER_UPDATEPORTRAIT;
    }

    public static String getRemixByModel(String modelRef) {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + REMIX_BY_MODEL + "?modelRef=" + modelRef;
    }

    public static String getRemixByItem(String itemRef) {
        return QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "") + REMIX_BY_ITEM + "?itemRef=" + itemRef;
    }
}

package com.focosee.qingshow.util;


/**
 * Created by i068020 on 2/21/15.
 */
public class ValueUtil {

    public static float pre_img_AspectRatio = 9/16;//前景图的宽高比
    public static float match_img_AspectRatio = 0.68f;//搭配图的宽高比
    public static float file_size_limit = 1000000000;
    public static final String phases_apply = "0";
    public static final String phases_finish = "1,2";
    public static final String SHARE_TRADE = "U09_SHARE_TRADE";
    public static final String SHARE_SHOW = "S03_SHARE_SHOW";
    public static final String SHARE_BONUS = "U15_SHARE_BONUS";

    public static final int SHOW_ERROR_TIME = 5000;//显示错误信息的时间
    public static final String CRASH_LOG = "crash_log";//崩溃日志
    //sharePreference
    public static final String NEED_GUIDE = "NEED_GUIDE_PUSH";//降价指引
    public static final String NEED_GUIDE_ID = "NEED_GUIDE_ID";//推送消息携带的ID
    public static final String itemExpectablePriceUpdated_id = "NEED_GUIDE_ID";//推送消息携带的ID
    public static final String tradeInitialized_id = "NEED_GUIDE_ID";//推送消息携带的ID
    public static final String S20_FIRST_INT = "第一次进入到搭配页面";
    public static final String IS_FIRST_OPEN_APP = "第一次打开应用";
    public static final String USER_STATUS = "用户状态";
    public static final String GUEST_ID = "guestId";
    public static final String UPDATE_APP_FORCE = "强制更新";

    //EventBus
    public static final String EVENT_NEED_GUIDE = "event_need_guide";
    public static final String BONUES_COMING = "bonues_coming";//收到佣金推送
    public static final String SUBMIT_TRADE_SUCCESSED = "S11NewTradeFragment页面下单成功";
    public static final String TRADE_REFUND_COMPLETE = "退款成功的通知消息";
    public static final String PAY_FINISHED = "付完款点击继续逛逛";
    public static final String U01_LOADING_FINISH = "u01的所有网络请求完成";
    public static final String UPDATE_APP_EVENT = "需要强制更新APP";
}

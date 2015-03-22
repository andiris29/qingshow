package com.focosee.qingshow.util;

public class ServerError {
    public static final int ERROR_GET_TOKEN_FAIL_CD = 9001;
    public static final int ERROR_GENERATE_PREPAY_PACKAGE_CD = 9002;
    public static final int ERROR_SIGN_PREPAY_PACKAGE_CD = 9003;
    public static final int ERROR_GET_PREPAY_FAIL_CD = 9004;
    public static final int ERROR_QUERY_ORDER_FAIL_CD = 9005;
    
    
    public static final String ERROR_GET_TOKEN_FAIL_MSG = "get access token failure.";
    public static final String ERROR_GENERATE_PREPAY_PACKAGE_MSG = "generate prepay's package failure.";
    public static final String ERROR_SIGN_PREPAY_PACKAGE_MSG = "sign prepay's pay parameter failure by SHA1.";
    public static final String ERROR_GET_PREPAY_FAIL_MSG = "get pre_pay id failure.";
    public static final String ERROR_QUERY_ORDER_FAIL_MSG = "query order status failure.";
}

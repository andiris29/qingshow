package com.focosee.qingshow.httpapi.response.error;

/**
 * Created by zenan on 1/2/15.
 */
public class ErrorCode {

    public static final int VolleyError = 8888;

    public static final int ServerError = 1000;
    public static final int IncorrectMailOrPassword = 1001;
    public static final int SessionExpired = 1002;
    public static final int ShowNotExist = 1003;
    public static final int ItemNotExist = 1004;
    public static final int PeopleNotExist = 1005;
    public static final int BrandNotExist = 1006;
    public static final int InvalidEmail = 1007;
    public static final int NotEnoughParam = 1008;
    public static final int PagingNotExist = 1009;
    public static final int EmailAlreadyExist = 1010;
    public static final int AlreadyLikeShow = 1011;
    public static final int NeedLogin = 1012;
    public static final int AlreadyFollowPeople = 1013;
    public static final int DidNotFollowPeople = 1014;
    public static final int AlreadyFollowBrand = 1015;
    public static final int DidNotFollowBrand = 1016;
    public static final int PItemNotExist = 1017;
    public static final int RequestValidationFail = 1018;
    public static final int AlreadyRelated = 1019;
    public static final int AlreadyUnrelated = 1020;
    public static final int InvalidCurrentPassword = 1021;
    public static final int NoNetWork = 1022;
    public static final int UnSupportVersion = 1027;
    public static final int MobileAlreadyExist = 1029;
    public static final int MobileVerifyFailed = 1030;
    public static final int SMSlimitedSend = 1031;
    public static final int FrequentlyRequest = 1032;
    public static final int NickNameAlredyExist = 1033;
}

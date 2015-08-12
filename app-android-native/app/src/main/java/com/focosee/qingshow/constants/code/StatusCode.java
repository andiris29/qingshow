package com.focosee.qingshow.constants.code;


/**
 * Created by Administrator on 2015/3/17.
 */
public class StatusCode {

    public static int APPLYING = 0;
    public static int APPLY_SUCCESSED = 1;
    public static int REPLAY_GOOD = 2;
    public static int SENDED = 3;
    public static int TRADE_SUCCESSED = 5;
    public static int RETURNING = 7;
    public static int RETURN_SUCCESSED = 9;
    public static int RETURN_FALSE = 10;
    public static int AUTO_RECEIVERED = 15;
    public static int TRADE_SHUTDOWN = 17;
    public static int TRADE_CANCEL = 18;


    public static final String getStatusText(int status){

        switch (status){
            case 0:
                return "折扣申请中...";
            case 1:
                return "折扣申请成功";
            case 2:
                return "备货中...";
            case 3:
                return "已发货";
            case 5:
                return "交易成功";
            case 7:
                return "退货中";
            case 9:
                return "交易关闭";
            case 10:
                return "交易关闭";
            case 15:
                return "交易成功";
            case 17:
                return "交易关闭";
        }
        return "";
    }

}

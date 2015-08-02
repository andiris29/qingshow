package com.focosee.qingshow.constants.code;

import java.util.Map;

/**
 * Created by Administrator on 2015/3/17.
 */
public class StatusCode {

    public static final String getStatusText(int status){

        switch (status){
            case 0:
                return "折扣申请中...";
            case 1:
                return "折扣申请成功";
            case 2:
                return "备货中…";
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

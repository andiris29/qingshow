package com.focosee.qingshow.model.vo.mongo;

import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.LinkedList;

/**
 * Created by Administrator on 2015/3/13.
 */
public class MongoTrade implements Serializable {

    public String _id;
    public Number totalFee;
    public int status;
    public GregorianCalendar create;
    public LinkedList<MongoOrder> orders;
//    public TaobaoInfo taobaoInfo;
    public Logistic logistic;
    public Returnlogistic returnlogistic;
    public LinkedList<StatusLog> statusLogs;

    public class TaobaoInfo{
        public String userNick;
        public String tradeID;
    }

    public class Logistic{
        public String company;
        public String trackingID;
    }

    public class Returnlogistic{
        public String company;
        public String trackingID;
    }

    public class StatusLog {
        public GregorianCalendar update;
        public String _id;
        public String peopleRef;
        public int status;
    }

}

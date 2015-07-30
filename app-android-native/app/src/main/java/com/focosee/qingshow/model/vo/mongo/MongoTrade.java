package com.focosee.qingshow.model.vo.mongo;

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.LinkedList;

/**
 * Created by Administrator on 2015/3/13.
 */
public class MongoTrade implements Serializable {

    public String _id;
//    public TradeContext __context;
    public Number totalFee;
    public int status;
    public GregorianCalendar create;
    public LinkedList<MongoOrder> orders;
    public TaobaoInfo taobaoInfo;
    public Logistic logistic;
    public Returnlogistic returnlogistic;
    public Pay pay;
    public LinkedList<StatusLog> statusLogs;

    public class Pay implements Serializable{

        public Weixin weixin;

        public class Weixin{
            public String prepayid;
        }

    }

    public class TaobaoInfo implements Serializable{
        public String userNick;
        public String tradeID;
    }

    public class Logistic implements Serializable{
        public String company;
        public String trackingID;
    }

    public class Returnlogistic implements Serializable {
        public String company;
        public String trackingID;
    }

    public class StatusLog implements Serializable{
        public GregorianCalendar date;
        public String _id;
        public String peopleRef;
        public int status;
        public String comment;
    }

    public class TradeContext implements Serializable{
        public boolean sharedByCurrentUser;
    }

}

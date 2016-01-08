package com.focosee.qingshow.model.vo.mongo;

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/3/13.
 */
public class MongoTrade implements Serializable {

    public String _id;
    public TradeContext __context;
    public Number totalFee;
    public int status;
    public GregorianCalendar create;
    public GregorianCalendar update;
    public TaobaoInfo taobaoInfo;
    public Logistic logistic;
    public Returnlogistic returnlogistic;
    public Pay pay;
    public LinkedList<StatusLog> statusLogs;
    public MongoPeople peopleSnapshot;
    public String selectedPeopleReceiverUuid;
    public boolean shareToPay;
    public String hint;
    public int quantity;
    public float expectedPrice;
    public MongoItem itemSnapshot;
    public MongoItem itemRef;
   // public String itemRef;
    public List<String> selectedSkuProperties;
    public String statusOrder;
    public  Number __v;
    public String highlight;
   // public String ownerRef;
    public String promoterRef;

    public class Pay implements Serializable {

        public Weixin weixin;
        public ForgeInfo forge;

        public class Weixin implements Serializable {
            public String prepayid;
        }
        public class ForgeInfo implements Serializable{
            public  String date;
        }

    }

    public class TaobaoInfo implements Serializable {
        public String userNick;
        public String tradeID;
    }

    public class Logistic implements Serializable {
        public String company;
        public String trackingId;
    }

    public class Returnlogistic implements Serializable {
        public String company;
        public String trackingID;
    }

    public class StatusLog implements Serializable {
        public GregorianCalendar date;
        public String _id;
        public String peopleRef;
        public int status;
        public String comment;
    }

    public class TradeContext implements Serializable {
        public boolean sharedByCurrentUser;
        public Item item;

        public class Item implements Serializable{
            public String expectablePrice;
            public String delist;
        }
    }
    public class ItemRef implements Serializable{
            public  String minExpectedPrice;
        public String promoPrice;
        public String __v;
    }



}

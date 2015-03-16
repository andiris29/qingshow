package com.focosee.qingshow.model.vo.mongo;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Created by Administrator on 2015/3/13.
 */
public class MongoTrade implements Serializable {

    public String _id;
    public String totalFee;
    public int status;
    public LinkedList<Order> orders;
    public TaobaoInfo taobaoInfo;
    public Logistic logistic;
    public Returnlogistic returnlogistic;

    class Order implements Serializable{
        public String quantity;
        public String price;
        public MongoItem itemSnapshot;
        public String selectedItemSkuId;
        public String selectedPeopleReceiverUuid;


    }

    class TaobaoInfo{
        public String userNick;
        public String tradeID;
    }

    class Logistic{
        public String company;
        public String trackingID;
    }

    class Returnlogistic{
        public String company;
        public String trackingID;
    }

}

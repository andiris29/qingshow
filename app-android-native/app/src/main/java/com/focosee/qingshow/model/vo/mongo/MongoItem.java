package com.focosee.qingshow.model.vo.mongo;

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by i068020 on 2/8/15.
 */
public class MongoItem implements Serializable {

    public String _id;
    public MongoCategories categoryRef;
    public String name;
    public String source;
    public String thumbnail;
    public Number price = 0;
    public Number promoPrice = 0;
    public List<String> skuProperties;
    public HashMap<String, String> skuTable;
    public GregorianCalendar create;
    public GregorianCalendar delist;
    public GregorianCalendar list;
    public boolean syncEnabled;
    public boolean readOnly;
    public GregorianCalendar sync;
    public ReturnInfo returnInfo;
    public Expectable expectable;
    public Number minExpectedPrice;
    public SourceInfo sourceInfo;

    public class ReturnInfo implements Serializable {
        public String name;
        public String phone;
        public String province;
        public String address;
    }

    public class Expectable implements Serializable {
        public boolean expired;
        public String message;
        public Number reduction;
    }

    public class SourceInfo implements Serializable{
        public String  id; //43001925539";
        public String  domain;  //tmall";
        public String  icon;  //http://trial01.focosee.com/img/item/source/tmall.jpg";
    }

//    public  class ShopRef implements  Serializable{
//
//    }

}

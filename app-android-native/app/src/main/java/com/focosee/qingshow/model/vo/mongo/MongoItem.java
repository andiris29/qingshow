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
    public Number price;
    public Number promoPrice;
    public Number minExpectedPrice;
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
        public Number price;
    }

}

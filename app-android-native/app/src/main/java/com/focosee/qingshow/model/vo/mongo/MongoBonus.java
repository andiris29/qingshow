package com.focosee.qingshow.model.vo.mongo;

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Administrator on 2015/12/2.
 */
public class MongoBonus implements Serializable {
    public String _id;
    //public MongoPeople ownerRef;
    public String ownerRef;
    public String[] participants;
    public Number type;
    public Number status;
    public Number amount;
    public String description;
    public GregorianCalendar create;
    public String icon;
    public Trigger trigger;
    public Number __v;

    public class Trigger implements Serializable{
        public MongoTrade tradeRef;
       // public List<MongoShow> showRefs;
        public List<String> showRefs;
    }
}

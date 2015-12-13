package com.focosee.qingshow.model.vo.mongo;

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Administrator on 2015/12/2.
 */
public class MongoBonus implements Serializable {
    public String _id;
    public MongoPeople ownerRef;
    public List<MongoPeople> participants;
    public Number type;
    public Number status;
    public Number amount;
    public String description;
    public String migrate;
    public GregorianCalendar create;
    public WeixinRedPack weixinRedPack;
    public String icon;
    public Trigger trigger;
    public LegacyTrigger legacyTrigger;

    public class LegacyTrigger implements Serializable{
        MongoItem itemRef;
    }

    public class Trigger implements Serializable{
        public List<MongoShow> showRefs;
    }

    public class WeixinRedPack implements Serializable {
        public GregorianCalendar create;
        public String send_listid;
    }
}

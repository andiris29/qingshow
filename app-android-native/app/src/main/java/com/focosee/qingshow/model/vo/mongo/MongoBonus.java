package com.focosee.qingshow.model.vo.mongo;

import java.io.Serializable;
import java.util.GregorianCalendar;

/**
 * Created by Administrator on 2015/12/2.
 */
public class MongoBonus implements Serializable {
    public MongoPeople ownerRef;
    public MongoPeople participants;
    public MongoTrade trigger;
    public Number type;
    public Number status;
    public Number amount;
    public String description;
    public GregorianCalendar create;
    public String weixinRedPackId;
}

package com.focosee.qingshow.model.vo.mongo;

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.Map;

/**
* Created by DylanJiang on 15/5/6.
        */
public class MongoChosen implements Serializable {
    public String _id;
    public IMongoChosen ref;
    public String refCollection;
    public int order;
    public int type;
    public GregorianCalendar date;


}

package com.focosee.qingshow.model.vo.mongo;

import java.io.Serializable;
import java.util.GregorianCalendar;

/**
 * Created by DylanJiang on 15/5/6.
 */
public class MongoChosen implements Serializable {
    public String _id;
    public Object ref;
    public String refCollection;
    public int order;
    public int type;
    public GregorianCalendar date;
}

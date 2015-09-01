package com.focosee.qingshow.model.vo.mongo;

import com.focosee.qingshow.model.vo.context.ShowContext;
import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.List;

public class MongoShow implements Serializable {
    public String _id;
    public ShowContext __context;

    public String cover;
    public boolean ugc;
    public String video;
    public String[] posters;
    public String description;

    public List<MongoItem> itemRefs;
    public MongoPromotion promotionRef;
    public String coverForeground;

    public GregorianCalendar create;
    public int numLike;
    public MongoPeople ownerRef;
    public boolean hideAgainstOwner;

    public Recommend recommend;
    public class Recommend implements Serializable {
        public String group;
        public GregorianCalendar date;
        public String description;
    }

}

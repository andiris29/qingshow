package com.focosee.qingshow.model.vo.mongo;

import java.io.Serializable;
import java.util.GregorianCalendar;

public class MongoShow implements Serializable {
    public String _id;

    public String cover;

    public String video;
    public String[] posters;
    public String description;


    public String[] itemRefs;
    public MongoBrand brandRef;
    public MongoPromotion promotionRef;

    public GregorianCalendar create;
    public int numLike;

    public Recommend recommend;



    public class Recommend implements Serializable {
        public String group;
        public GregorianCalendar date;
        public String description;
    }


}

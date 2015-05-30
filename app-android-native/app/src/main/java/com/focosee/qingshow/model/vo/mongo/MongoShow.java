package com.focosee.qingshow.model.vo.mongo;

import com.focosee.qingshow.model.vo.context.ShowContext;

import java.io.Serializable;
import java.util.GregorianCalendar;

public class MongoShow implements Serializable {
    public String _id;
    public ShowContext __context;

    public String cover;

    public String video;
    public String[] posters;
    public String description;


    public MongoItem[] itemRefs;
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

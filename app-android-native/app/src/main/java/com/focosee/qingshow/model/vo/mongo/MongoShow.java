package com.focosee.qingshow.model.vo.mongo;

import com.focosee.qingshow.model.vo.context.QSRect;
import com.focosee.qingshow.model.vo.context.ShowContext;
import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.List;

public class MongoShow implements Serializable {
    public String _id;
    public ShowContext __context;

    public String cover;
    public String video;

    public List<MongoItem> itemRefs;
    public List<QSRect> itemRects;
    public String coverForeground;

    public GregorianCalendar create;
    public int numLike;
    public MongoPeople ownerRef;
    public boolean hideAgainstOwner;

    public Recommend recommend;
    public class Recommend implements Serializable {
        public GregorianCalendar date;
    }

}

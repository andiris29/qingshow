package com.focosee.qingshow.model.vo.remix;

import com.focosee.qingshow.model.vo.mongo.MongoItem;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2015/12/2.
 */
public class RemixByItem implements Serializable {
    public Master master;
    public List<Slave> slaves;

    public class Master implements Serializable{
        public QSRect rect;
    }

    public class Slave implements Serializable{
       public QSRect rect;
       public MongoItem itemRef;
    }
}

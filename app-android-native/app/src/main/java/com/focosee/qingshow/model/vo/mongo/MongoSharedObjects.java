package com.focosee.qingshow.model.vo.mongo;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/10/13.
 */
public class MongoSharedObjects implements Serializable {
    public String _id;
    public String initiatorRef;
    public Number numLike;
    public Number __v;
    public Number numDislike;
    public Number type;
    public String create;
    public String title;
    public String description;
    public String icon;
    public String url;

 public class TargetInfoItem {
    public ShowItem show;
 }
    public class  ShowItem{
        public String showRef;
    }
}

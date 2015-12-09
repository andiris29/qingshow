package com.focosee.qingshow.model.vo.mongo;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/6/24.
 */
public class MongoCategories implements Serializable{

    public String _id;
    public String name;
    public String icon;
    public String order;
    public MatchInfo matchInfo;
    public MongoParentCategories parentRef;
    public boolean activate = true;
    public Context __context;

    public class MatchInfo implements Serializable{
        public boolean enabled;
        public boolean defaultOnCanvas;
    }

    public MongoCategories() {

    }

    public boolean isActivate() {
        return activate;
    }

    public void setActivate(boolean activate) {
        this.activate = activate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public class Context {
        public float x;
        public float y;
        public int maxWidth;
        public int maxHeight;
    }

}

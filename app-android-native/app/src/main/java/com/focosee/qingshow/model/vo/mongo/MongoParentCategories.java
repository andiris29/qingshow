package com.focosee.qingshow.model.vo.mongo;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/6/24.
 */
public class MongoParentCategories implements Serializable{
    public String _id;
    public String name;
    public String icon;
    public String order;
    public MatchInfo matchInfo;
    public int measureComposition;
    public boolean activate = true;

    public class MatchInfo implements Serializable{
        public boolean enabled;
        public boolean defaultOnCanvas;
        public int row;
        public int column;
    }
    public MongoParentCategories() {

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

}

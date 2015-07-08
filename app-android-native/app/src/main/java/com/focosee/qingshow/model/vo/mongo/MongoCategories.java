package com.focosee.qingshow.model.vo.mongo;

/**
 * Created by Administrator on 2015/6/24.
 */
public class MongoCategories {
    public String _id;
    public String name;
    public String order;
    private String parentRef;
    private String icon;
    private boolean activate = true;
    public MatchInfo matchInfo;

    public class MatchInfo {
        public boolean enabled;
        public boolean defaultOnCavase;
        public int row;
        public int column;
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

    public String getParentRef() {
        return parentRef;
    }

    public void setParentRef(String parentRef) {
        this.parentRef = parentRef;
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

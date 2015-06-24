package com.focosee.qingshow.model.vo.mongo;

/**
 * Created by Administrator on 2015/6/24.
 */
public class MongoCategories {
    private String _id;
    private String name;
    private String parentRef;
    private String icon;
    private boolean activate;

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

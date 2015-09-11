package com.focosee.qingshow.model;

import com.focosee.qingshow.model.vo.mongo.MongoCategories;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/9/11.
 */
public enum CategoriesModel {
    INSTANCE;

    public Map<String, MongoCategories> categories;

    public void setCategories(Map<String, MongoCategories> categories){
        this.categories = categories;
    }

    public Map<String, MongoCategories> getCategories(){
        if(null == this.categories)return null;
        return categories;
    }

}

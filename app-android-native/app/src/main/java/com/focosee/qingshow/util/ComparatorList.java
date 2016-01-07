package com.focosee.qingshow.util;

import com.focosee.qingshow.model.CategoriesModel;
import com.focosee.qingshow.model.vo.mongo.MongoCategories;
import com.focosee.qingshow.model.vo.mongo.MongoItem;

/**
* Created by Administrator on 2015/8/4.
        */
public class ComparatorList {

    public static java.util.Comparator<MongoItem> itemComparator(){
        return new java.util.Comparator<MongoItem>() {
            @Override
            public int compare(MongoItem lhs, MongoItem rhs) {
                return CategoriesModel.INSTANCE.getCategories().get(lhs._id).order.compareTo(CategoriesModel.INSTANCE.getCategories().get(rhs._id).order);
            }
        };
    }

    public static java.util.Comparator<MongoCategories> categoriesComparator(){
        return new java.util.Comparator<MongoCategories>() {
            @Override
            public int compare(MongoCategories lhs, MongoCategories rhs) {
                return lhs.order.compareTo(rhs.order);
            }
        };
    }
}

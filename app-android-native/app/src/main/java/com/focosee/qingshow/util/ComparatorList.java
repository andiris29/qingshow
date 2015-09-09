package com.focosee.qingshow.util;

import com.focosee.qingshow.model.vo.mongo.MongoCategories;
import com.focosee.qingshow.model.vo.mongo.MongoItem;

import java.util.Comparator;

/**
* Created by Administrator on 2015/8/4.
        */
public class ComparatorList {

    public static java.util.Comparator<MongoItem> itemComparator(){
        return new java.util.Comparator<MongoItem>() {
            @Override
            public int compare(MongoItem lhs, MongoItem rhs) {
                return lhs.categoryRef.order.compareTo(rhs.categoryRef.order);
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

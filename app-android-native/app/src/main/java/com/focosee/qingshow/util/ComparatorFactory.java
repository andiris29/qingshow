package com.focosee.qingshow.util;

import com.focosee.qingshow.model.vo.mongo.MongoCategories;
import com.focosee.qingshow.model.vo.mongo.MongoItem;

import java.util.Comparator;

/**
 * Created by Administrator on 2015/8/4.
 */
public class ComparatorFactory {

    public static Comparator<MongoItem> itemComparator(){
        return new Comparator<MongoItem>() {
            @Override
            public int compare(MongoItem lhs, MongoItem rhs) {
                return lhs.categoryRef.order.compareTo(rhs.categoryRef.order);
            }
        };
    }

    public static Comparator<MongoCategories> categoriesComparator(){
        return new Comparator<MongoCategories>() {
            @Override
            public int compare(MongoCategories lhs, MongoCategories rhs) {
                return lhs.order.compareTo(rhs.order);
            }
        };
    }
}

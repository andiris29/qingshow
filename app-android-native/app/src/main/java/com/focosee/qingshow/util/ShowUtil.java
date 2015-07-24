package com.focosee.qingshow.util;

import com.focosee.qingshow.model.vo.mongo.MongoShow;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by DylanJiang on 15/5/28.
 */
public class ShowUtil {
    public static Map<GregorianCalendar, List<MongoShow>> formentShowByData(List<MongoShow> data) {

        Map<GregorianCalendar, List<MongoShow>> result = new HashMap<>();
        for (MongoShow show : data) {
            if (result.containsKey(show.recommend.date)) {
                List<MongoShow> item = result.get(show.recommend.date);
                item.add(show);
                result.put(show.recommend.date,item);
            } else {
                List<MongoShow> item = new ArrayList<>();
                item.add(show);
                result.put(show.recommend.date,item);
            }
        }

        return result;
    }

    public static List<List<MongoShow>> formentShow(List<MongoShow> data){
        List<List<MongoShow>> result = new ArrayList<>();
        for (List<MongoShow> shows : formentShowByData(data).values()) {
            result.add(shows);
        }
        return result;
    }

    public static List<MongoShow> cleanHidedShow(List<MongoShow> data){
        List<MongoShow> result = new ArrayList<>();
        for (MongoShow show : data) {
            if(!show.hideAgainstOwner){
                result.add(show);
            }
        }
        return result;
    }
}

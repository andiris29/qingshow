package com.focosee.qingshow.util.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/8/6.
 */
public class FilterHepler {
    public static <T> void filterList(List<T> list, Filter<T> filter) {
        List<T> result = new ArrayList<>();
        result.addAll(list);
        for (T t : list) {
            if (filter.filtrate(t)) {
                result.remove(t);
            }
        }
        list.clear();
        list.addAll(result);
    }
}

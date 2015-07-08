package com.focosee.qingshow.activity;

import java.util.List;

/**
 * Created by Administrator on 2015/7/8.
 */
public class S21CategoryEvent {
    private List<String> categories;

    public S21CategoryEvent(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getCategories() {
        return categories;
    }
}

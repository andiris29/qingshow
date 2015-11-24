package com.focosee.qingshow.model.vo.context;

import android.graphics.Point;
import android.graphics.Rect;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2015/11/24.
 */
public class ItemContext implements Serializable {
    public Model model;
    public List<Item> items;
    public class Model {
        public QSRect rect;
    }

    public class Item {
        public QSRect rect;
        public String ref;
    }
}

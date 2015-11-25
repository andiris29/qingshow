package com.focosee.qingshow.model.vo.context;

import android.graphics.Point;
import android.graphics.Rect;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2015/11/24.
 */
public class ItemContext implements Serializable {
    public Model master;
    public List<Slave> slaves;
    public class Model {
        public QSRect rect;
    }

    public class Slave {
        public QSRect rect;
        public String categoryRef;
    }
}

package com.focosee.qingshow.model.vo.remix;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2015/11/24.
 */
public class RemixByModel implements Serializable {
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

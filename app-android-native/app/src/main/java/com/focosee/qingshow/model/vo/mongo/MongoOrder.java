package com.focosee.qingshow.model.vo.mongo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2015/3/19.
 */
public class MongoOrder implements Serializable {
    public String _id;
    public int quantity;
    public double actualPrice;
    public double expectedPrice;
    public MongoItem itemSnapshot;
    public List<String> selectedSkuProperties;


}

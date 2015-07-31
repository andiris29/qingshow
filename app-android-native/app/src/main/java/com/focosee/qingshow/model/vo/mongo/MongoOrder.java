package com.focosee.qingshow.model.vo.mongo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2015/3/19.
 */
public class MongoOrder implements Serializable{
    public int quantity;
    public double price;
    public MongoItem itemSnapshot;
    public String selectedItemSkuId;
    public String selectedPeopleReceiverUuid;
    public MongoPeople peopleSnapshot;
    public String _id;
    public List<String> selectedSkuProperties;

}

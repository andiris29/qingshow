package com.focosee.qingshow.model.vo.aggregation;

import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.model.vo.mongo.MongoStickyShow;

import java.util.List;
import java.util.Objects;

/**
 * Created by Administrator on 2015/11/25.
 */
public class FeedingAggregationLatest {
   public List<MongoPeople> topOwners;
   public List<MongoShow> topShows;
   public int numViewOfCurrentUser;
   public int numOwners;
   public String key;
   public Object stickyShow;
}

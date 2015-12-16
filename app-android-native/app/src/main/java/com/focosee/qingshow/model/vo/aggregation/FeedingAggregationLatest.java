package com.focosee.qingshow.model.vo.aggregation;

import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoShow;

import java.util.List;

/**
 * Created by Administrator on 2015/11/25.
 */
public class FeedingAggregationLatest {
   public List<MongoPeople> topOwners;
   public List<MongoShow> topShows;
   public int numViewOfCurrentUser;
   public int numOwners;
   public String key;
}

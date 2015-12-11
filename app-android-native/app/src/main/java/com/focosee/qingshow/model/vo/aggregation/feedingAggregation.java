package com.focosee.qingshow.model.vo.aggregation;

import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoShow;

import java.util.List;

/**
 * Created by Administrator on 2015/11/25.
 */
public class FeedingAggregation {
   public List<MongoPeople> topOwners;
   public List<MongoShow> topShows;
   public int indexOfCurrentUser;
   public int numOwners;
   public String key;
}

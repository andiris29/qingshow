package com.focosee.qingshow.model.vo.context;

import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoPromotion;

import java.io.Serializable;

/**
 * Created by i068020 on 2/7/15.
 */
public class ShowContext implements Serializable {
    public int numComments;
    public MongoPeople createdBy;
//    public int numLike;
    public MongoPromotion promotionRef;
    public Boolean likedByCurrentUser;
    public Boolean sharedByCurrentUser;
}

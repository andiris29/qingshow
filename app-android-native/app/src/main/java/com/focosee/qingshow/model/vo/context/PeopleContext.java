package com.focosee.qingshow.model.vo.context;

import com.focosee.qingshow.model.vo.aggregation.BonusAmount;

import java.io.Serializable;

/**
 * Created by i068020 on 2/7/15.
 */
public class PeopleContext implements Serializable {
    public boolean followedByCurrentUser = false;
    public int numCreateShows;
    public int numLikeToCreateShows;
    public BonusAmount bonusAmountByStatus;
}

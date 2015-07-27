package com.focosee.qingshow.model.vo.context;

import java.io.Serializable;

/**
 * Created by i068020 on 2/7/15.
 */
public class PeopleContext implements Serializable {
    public boolean followedByCurrentUser = false;
    public int numCreateShows;
    public int numLikeToCreateShows;
}

package com.focosee.qingshow.entity.mongo;

import com.focosee.qingshow.entity.context.PeopleContext;

import java.io.Serializable;

public class MongoPeople implements Serializable {
    public static final String DEBUG_TAG = "MongoPeople";

    public String _id;

    public int[] roles;
    public String name;
    public String portrait;
    public String background;
    public String height;
    public String weight;

    public int job;
    public int shoeSize;
    public int clothingSize;
    public int gender;
    public int hairType;
    //    +birthday <date>
    public String favoriteBrand;
    public UserInfo userInfo;
    //    +modelInfo
    //    +modelInfo.order

    private PeopleContext __context;

    public class UserInfo implements Serializable {
        public String id;
        public String password;
        public String encryptedPassword;
    }

    public PeopleContext get__context() {
        return __context;
    }

    public String get_id() {
        return _id;
    }

    public String getHeight() {
        if (null == height) {
            return "";
        }
        return height + "cm/";
    }

    public String getWeight() {
        if (null == weight) {
            return "";
        }
        return weight + "kg";
    }

    public String getName() {
        return name;
    }

    public String getJob() {
        return "模特";
    }

    public String getHeightWeight() {
        if (height != "" && weight != "" && null != height && null != weight) {
            return String.valueOf(height) + "cm/" + String.valueOf(weight) + "kg";
        }
        return "";
    }

    public String getBackground() {
        return background;
    }

    public String getPortrait() {
        return portrait;
    }

    public String getPeopleName() {
        return name;
    }

    public String getPeoplePortrait() {
        return portrait;
    }

    public String getShowNumberString() {
        return String.valueOf(__context.numShows);
    }

    public String getLikeNumberString() {
        return String.valueOf(__context.numFollowers);
    }

    public int[] getRoles() {
        return roles;
    }

    public int getNumberShows() {
        if (null != __context)
            return __context.numShows;
        return 0;
    }

    public int getNumberFollowers() {
        if (null != __context)
            return __context.numFollowers;
        return 0;
    }

    public boolean getModelIsFollowedByCurrentUser() {
        if (null != __context)
            return __context.followedByCurrentUser;
        return false;
    }

    public void setModelIsFollowedByCurrentUser(boolean followedByCurrentUser) {
        __context.followedByCurrentUser = followedByCurrentUser;
    }

}

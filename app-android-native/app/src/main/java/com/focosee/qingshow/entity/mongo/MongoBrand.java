package com.focosee.qingshow.entity.mongo;

import com.focosee.qingshow.entity.context.BrandContext;
import com.focosee.qingshow.entity.metadata.ImageMetadata;

import java.io.Serializable;

public class MongoBrand implements Serializable {
    public static final String DEBUG_TAG = "MongoBrand";

    public String _id;

    public String name;
    public Number type;
    public String logo;
    public String background;
    public String cover;
    public ShopInfo shopInfo; // TODO integrate

    public BrandContext __context;
    public ImageMetadata coverMetadata;

    public String get_id() {
        return _id;
    }

    public String getBrandName() {
        return name;
    }

    public String getBrandLogo() {
        return logo;
    }

    public String getBrandCover() {
        return cover;
    }

    public String getBrandDescription() {
        return name;
    }

    public int getCoverWidth() {
        return 320;
    }

    public int getCoverHeight() {
        return 320;
    }

    public boolean getModelIsFollowedByCurrentUser() {
        if (null != __context)
            return __context.followedByCurrentUser;
        return false;
    }

    public void setModelIsFollowedByCurrentUser(boolean followedByCurrentUser) {
        __context.followedByCurrentUser = followedByCurrentUser;
    }

    public class ShopInfo {
        public String address;
        public String phone;
    }
}

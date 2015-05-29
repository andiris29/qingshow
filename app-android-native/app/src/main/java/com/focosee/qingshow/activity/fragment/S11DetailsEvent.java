package com.focosee.qingshow.activity.fragment;

import com.focosee.qingshow.model.vo.mongo.MongoOrder;

/**
 * Created by Administrator on 2015/3/17.
 */
public class S11DetailsEvent {

    private MongoOrder order;
    private boolean exists = false;
    private int category;
    private float[] nums;

    public S11DetailsEvent(MongoOrder order, boolean exists, int category,float[] nums) {
        this.order = order;
        this.exists = exists;
        this.category = category;
        this.nums = nums;
    }
    public MongoOrder getOrder() {
        return order;
    }

    public boolean isExists() {
        return exists;
    }

    public int getCategory() {
        return category;
    }

    public float[] getNums() {
        return nums;
    }
}

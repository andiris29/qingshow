package com.focosee.qingshow.model.vo.mongo;

import com.focosee.qingshow.model.vo.context.PeopleContext;

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.LinkedList;

public class MongoPeople implements Serializable {
    public static final String DEBUG_TAG = "MongoPeople";

    public String _id;

    public String nickname;
    public int[] roles;
    public String name;
    public String portrait;
    public String background;
    public int height;
    public int weight;
    public int age;
    public int gender;
    public int hairType;
    public int bodyType;
    public int dressStyle;
    public int[] expectations;
    public UserInfo userInfo;
    public LinkedList<Receiver> receivers;
    public MeasureInfo measureInfo;

    //    +modelInfo
    //    +modelInfo.order

    public PeopleContext __context;

    public class Receiver implements Serializable {
        public String uuid;
        public String name;
        public String phone;
        public String province;
        public String address;
        public boolean isDefault;

        public Receiver() {

        }
    }


    public class UserInfo implements Serializable {
        public String id;
        public String password;
        public String encryptedPassword;
    }

    public class MeasureInfo implements Serializable {
        public int shoulder;
        public int bust;
        public int waist;
        public int hips;

    }
}

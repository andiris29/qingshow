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
    public Number height;
    public Number weight;
    public Number age;
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
        public Number shoulder;
        public Number bust;
        public Number waist;
        public Number hips;

    }
}

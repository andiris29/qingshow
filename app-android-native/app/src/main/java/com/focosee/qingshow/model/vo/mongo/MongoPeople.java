package com.focosee.qingshow.model.vo.mongo;

import com.focosee.qingshow.model.vo.context.PeopleContext;
import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

public class MongoPeople implements Serializable {
    public static final String DEBUG_TAG = "MongoPeople";
    public static final int GUEST = 0;
    public static final int USER = 1;
    public static final int FIRST_OPEN_APP = 1;
    public static final int GET_GUEST_USER = 2;
    public static final int MATCH_FINISHED = 3;
    public static final int LOGIN_GUIDE_FINISHED = 4;

    public String _id;

    public String nickname;
    public Number role;
    public String name;
    public String portrait;
    public String background;
    public Number height;
    public Number weight;
    public Number age;
    public String mobile;
    public int bodyType;
    public int dressStyle;
    public int[] expectations;
    public UserInfo userInfo;
    public LinkedList<Receiver> receivers;
    public MeasureInfo measureInfo;
    public boolean bonusWithdrawRequested;
    public List<UnreadNotification> unreadNotifications;

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

    public class Trigger implements Serializable {
        public String forgerRef;
        public String tradeRef;
        public String itemRef;
    }

    public class UnreadNotification implements Serializable {
        public GregorianCalendar create;
        public Extra extra;
    }

    public class Extra implements Serializable{
        public String _id;
        public String command;
        public int index;
    }
}

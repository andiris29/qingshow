package com.focosee.qingshow.entity;

public class People {
    public String _id;
    public UserInfo userinfo;
    public String update;
    public String create;
    public String[] followBrandRefs;
    public String[] followRefs;
    public String[] followerRefs;
    public String[] likingShowRefs;
    public String[] hairTypes;
    public String[] roles;
    public int __v;
    public int gender;
    public String portrait;
    public String height;
    public String name;
    public String weight;
    public String background;

    public class UserInfo {
        public String id;
        public String encryptedPassword;
    }
}

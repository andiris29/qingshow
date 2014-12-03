package com.focosee.qingshow.entity;

/**
 * Created by jackyu on 11/30/14.
 */
public class CommentEntity extends AbsEntity {

    public String _id;
    public String targetRef;
    public String atRef;
    public RefAuthor authorRef;
    public String comment;
    public String __v;
    public String create;

    public class RefAuthor {
        public String _id;
        public String __v;
        public String portrait;
        public String background;
        public String update;
        public String create;
        public String hairTypes;
        public String roles;
        public String name;
    }
}

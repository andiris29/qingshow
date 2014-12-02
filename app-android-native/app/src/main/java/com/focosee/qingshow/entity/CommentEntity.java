package com.focosee.qingshow.entity;

/**
 * Created by jackyu on 11/30/14.
 */
public class CommentEntity extends AbsEntity {
    public Metadata metadata;
    public Data data;

    public class Metadata {
        public int numTotal;
        public int numPages;
    }

    public class Data {
        public ShowComments[] showComments;

        public class ShowComments {
            public String _id;
            public String targetRef;
            public String atRef;
            public String authorRef;
            public String __v;
            public String create;
        }
    }
}

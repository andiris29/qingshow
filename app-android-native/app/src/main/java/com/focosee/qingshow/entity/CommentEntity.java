package com.focosee.qingshow.entity;

public class CommentEntity extends AbsEntity {

    public String getId() {
        return _id;
    }

    public String getUserId() {
        if (null == authorRef || null == authorRef._id)
            return null;
        return authorRef._id;
    }

    public String getAuthorImage() {
        if (null != this.authorRef && null != this.authorRef.portrait)
            return this.authorRef.portrait;
        return "";
    }

    public String getAuthorName() {
        if (null != this.authorRef && null != this.authorRef.name)
            return this.authorRef.name;
        return "未设置名称";
    }

    public String getCommentContent() {
        if (null != this.comment)
            return this.comment;
        return "评论内容为空";
    }

    public String getCommentTime() {
        if (null != this.create)
            return this.create;
        return "未设置时间";
    }

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
        public String[] followRefs;
        public String[] followerRefs;
        public String portrait;
        public String background;
        public String update;
        public String create;
        public int[] hairTypes;
        public String[] roles;
        public String name;
    }
}

package com.focosee.qingshow.model.vo.mongo;

import java.io.Serializable;

// TODO Split to MongoShowComment & MongoPreviewComment... keep same as db design
public class MongoComment implements Serializable {
    public static final String DEBUG_TAG = "MongoComment";

    public String _id;

    // targetRef will not be populated
    public String targetRef;

    public MongoPeople authorRef;
    public MongoPeople atRef;
    public String comment;

    public String create;

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

    public MongoPeople getAuthorRef() {
        return this.authorRef;
    }

    public String getAuthorName() {
        if (null != this.authorRef && null != this.authorRef.name)
            return this.authorRef.name;
        return "未命名";
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
}

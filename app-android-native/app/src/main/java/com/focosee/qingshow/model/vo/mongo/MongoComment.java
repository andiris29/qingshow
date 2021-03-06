package com.focosee.qingshow.model.vo.mongo;

import java.io.Serializable;
import java.util.GregorianCalendar;

// TODO Split to MongoShowComment & MongoPreviewComment... keep same as db design
public class MongoComment implements Serializable {
    public static final String DEBUG_TAG = "MongoComment";

    public String _id;

    // targetRef will not be populated
    public String targetRef;

    public MongoPeople authorRef;
    public MongoPeople atRef;
    public String comment;

    public GregorianCalendar create;

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
        if (null != this.authorRef && null != this.authorRef.nickname && !"".equals(this.authorRef.nickname))
            return this.authorRef.nickname;
        return "倾秀用户";
    }

    public String getCommentContent() {
        if (null != this.comment)
            return this.comment;
        return "评论内容为空";
    }
}

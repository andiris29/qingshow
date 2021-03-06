package com.focosee.qingshow.model.vo.mongo;

import com.focosee.qingshow.model.vo.context.PreviewContext;
import com.focosee.qingshow.model.vo.metadata.ImageMetadata;

import java.io.Serializable;
import java.util.LinkedList;

public class MongoPreview implements Serializable {
    public static final String DEBUG_TAG = "MongoPreview";

    public String _id;

    public LinkedList<Image> images;
    public ImageMetadata imageMetadata;

    public int numLike;

    public PreviewContext __context;

    public String get_id() {
        return _id;
    }

    public int getNumComments() {
        if (null != __context) {
            return __context.numComments;
        }
        return 0;
    }

    public boolean getIsLikeByCurrentUser() {
        if (null != __context) {
            return __context.likedByCurrentUser;
        }
        return false;
    }

    public void setIsLikeByCurrentUser(boolean isLike){
        if(null != __context) {
            __context.likedByCurrentUser = isLike;
        }
    }

    public String getCover(int index) {
        if (null == images.get(index)) {
            return "";
        }
        return images.get(index).url;
    }


    public String getDescription(int index) {
        if (null == images || null == images.get(index)) {
            return "";
        }
        return images.get(index).description;
    }


    public int getWidth() {
        if (null != imageMetadata) {
            return imageMetadata.width;
        }
        return 0;
    }

    public int getHeight() {
        if (null != imageMetadata) {
            return imageMetadata.height;
        }
        return 0;
    }


    public class Image implements Serializable {
        public String url;
        public String description;
    }

}

package com.focosee.qingshow.model.vo.mongo;

import com.focosee.qingshow.model.vo.context.ShowContext;
import com.focosee.qingshow.model.vo.metadata.ImageMetadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;

// TODO Cleanup redundant methods
public class MongoShow implements Serializable {
    public static final String DEBUG_TAG = "MongoShow";

    public String _id;
    public String _hughUpdate;


    public String cover;
    public String horizontalCover;

    public String video;
    public String[] posters;


    public int numLike;


    public String[] itemRefs;
    public GregorianCalendar create;

    public ShowContext __context;
    public ImageMetadata coverMetadata;
    public ImageMetadata horizontalCoverMetadata;

    public Recommend recommend;



    public int getHorizontalCoverHeight() {
        if (null != horizontalCoverMetadata && null != horizontalCoverMetadata.url)
            return horizontalCoverMetadata.height;
        return 0;
    }

    public int getHorizontalCoverWidth() {
        if (null != horizontalCoverMetadata && null != horizontalCoverMetadata.url)
            return horizontalCoverMetadata.width;
        return 0;
    }

    public String getShowCover() {
        if (null != coverMetadata && null != coverMetadata.url)
            return coverMetadata.url;
        return null;
    }

    public int getCoverHeight() {
        if (null != coverMetadata && null != coverMetadata.url)
            return coverMetadata.height;
        return 0;
    }

    public int getCoverWidth() {
        if (null != coverMetadata && null != coverMetadata.url)
            return coverMetadata.width;
        return 0;
    }

    public String getShowNumLike() {
        return numLike + "";
    }

    public String getModelPhoto() {
//        return (null != modelRef) ? modelRef.getPortrait() : "";
        return "";
    }

    public String getModelName() {
//        return (null != modelRef) ? modelRef.getName() : "";
        return "";
    }


    public String getModelHeightAndHeightWithFormat() {
//        return (null != modelRef) ? (String.valueOf(modelRef.getHeight()) + String.valueOf(modelRef.getWeight())) : "0cm/0kg";
        return "";
    }

    public boolean getShowIsFollowedByCurrentUser() {
        if (null != __context) {
            return __context.likedByCurrentUser;
        }
        return false;
    }


//    public MongoPeople getModelRef() {
//        return modelRef;
//    }


    public String get_id() {
        return _id;
    }

    public String getShowVideo() {
        return (null != video) ? video : null;
    }

    public String getModelJob() {
//        return (null != modelRef && null != modelRef.roles) ? modelRef.roles.toString() : "";
        return "";
    }

    public String getModelWeightHeight() {
//        return ((null != modelRef) ? modelRef.height : "") + "cm/" + ((null != modelRef) ? modelRef.weight : "") + "kg";
        return "";
    }

    // TODO change age calculate method
    public String getModelAgeHeight() {
//        return (null != modelRef) ? "x" + "岁 " + modelRef.height + "cm" : "";
        return "";
    }


    public ArrayList<MongoItem> getItemsList() {
        ArrayList<MongoItem> result = new ArrayList<MongoItem>();
//        for (MongoItem item : itemRefs) {
//            result.add(item);
//        }
        return result;
    }

    public MongoItem getItem(int index) {
        if (null == itemRefs || index >= itemRefs.length || index < 0) return null;
        return null;
    }

    public String[] getPosters() {
        return (null != posters) ? posters : new String[0];
    }

    public String getShowCommentNumber() {
        return (null != __context) ? String.valueOf(__context.numComments) : "0";
    }

    public String getShowLikeNumber() {
        return numLike + "";
    }

    public Boolean likedByCurrentUser() {
        return __context.likedByCurrentUser;
    }

    public void setLikedByCurrentUser(Boolean likedByCurrentUser) {
        __context.likedByCurrentUser = likedByCurrentUser;
        if (likedByCurrentUser)
            //__context.numLike++;
            numLike++;
        else if (numLike > 0)
            numLike--;
        //__context.numLike--;
    }

    public String getCover() {
        return cover;
    }

    public String getHorizontalCover(){ return horizontalCover; }

    public String getItemsCount() {
        return String.valueOf((null != itemRefs) ? itemRefs.length : 0);
    }


    @Override
    public String toString() {
        return "MongoShow{" +
                "__context=" + __context +
                ", _id='" + _id + '\'' +
                ", cover='" + cover + '\'' +
                ", horizontalCover='" + horizontalCover + '\'' +
                ", video='" + video + '\'' +
                ", posters=" + Arrays.toString(posters) +
                ", numLike=" + numLike +
                ", itemRefs=" + Arrays.toString(itemRefs) +
                ", coverMetadata=" + coverMetadata +
                ", horizontalCoverMetadata=" + horizontalCoverMetadata +
                ", recommend=" + recommend +
                '}';
    }
    public class Recommend implements Serializable {
        public String group;
        public GregorianCalendar date;
        public String description;
    }


}

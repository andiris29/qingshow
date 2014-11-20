package com.focosee.qingshow.entity;

/**
 * Created by jackyu on 11/21/14.
 */
public class ShowEntity extends AbsEntity {

    //--- Public interface
    // id
    public String getShowId(){
        return id;
    }

    // the image of the show
    public String getShowImgSrc() {
        return "";
    }

    // model img
    public String getModelImgSrc() {
        return "";
    }

    // model name
    public String getModelName() {
        return "";
    }

    // model height
    public String getModelHeight() {
        return "";
    }

    // model weight
    public String getModelWeight() {
        return "";
    }

    // model word
    public String getModelWord() {
        return "";
    }

    // the number of people who agree with this show
    public String getAgreePeopleNumber() {
        return "";
    }


    //--- Inner data
    public String id;

    //--- Generated setMethod (User do not need to know, only for json conversion)
    public void setId(String id) {
        this.id = id;
    }
}

package com.focosee.qingshow.model;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.util.ValueUtil;

/**
 * Created by i068020 on 2/21/15.
 */
public enum QSModel {
    INSTANCE;

    private MongoPeople user;

    public boolean loggedin() {
        return !TextUtils.isEmpty(QSApplication.instance().getPreferences().getString("id",""));
    }

    public MongoPeople getUser() {
        return user;
    }

    public boolean isGuest(){
        return QSApplication.instance().getPreferences().contains(ValueUtil.GUEST_ID);
    }

    public void setUser(MongoPeople _user) {
        this.user = _user;
        saveUser(_user._id);
    }

    public void saveUser(String id){
        SharedPreferences.Editor editor = QSApplication.instance().getPreferences().edit();
        editor.putString("id", id);
        editor.commit();
    }

    public void login(MongoPeople _user){
        setUser(_user);
        saveUser(_user._id);
        if(QSApplication.instance().getPreferences().contains(ValueUtil.GUEST_ID)){
            SharedPreferences.Editor editor = QSApplication.instance().getPreferences().edit();
            editor.remove(ValueUtil.GUEST_ID);
            editor.commit();
        }
    }

    public void removeUser(){
        SharedPreferences.Editor editor = QSApplication.instance().getPreferences().edit();
        editor.remove("id");
        if(QSApplication.instance().getPreferences().contains(ValueUtil.GUEST_ID)){
            editor.remove(ValueUtil.GUEST_ID);
        }
        editor.commit();
        this.user = null;
    }

    public String getUserId(){
        return QSApplication.instance().getPreferences().getString("id", "");
    }

    public void setUserStatus(int status){
        SharedPreferences.Editor editor = QSApplication.instance().getPreferences().edit();
        editor.putInt(ValueUtil.USER_STATUS, status);
        editor.commit();
    }

    public int getUserStatus(){
        return QSApplication.instance().getPreferences().getInt(ValueUtil.USER_STATUS, 0);
    }

    public boolean isFinished(int status){
        return getUserStatus() >= status;
    }

    public boolean putGuestId(String guestId){
        SharedPreferences.Editor editor = QSApplication.instance().getPreferences().edit();
        editor.putString(ValueUtil.GUEST_ID, guestId);
        return editor.commit();
    }
}

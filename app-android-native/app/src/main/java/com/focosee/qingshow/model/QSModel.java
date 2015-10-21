package com.focosee.qingshow.model;

import android.content.SharedPreferences;
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
        return null != user;
    }

    public MongoPeople getUser() {
        return user;
    }

    public boolean isGuest(){
        if(null == user) return true;
        if(user.role == null) return false;
        if(user.role.intValue() == 0) return true;
        return false;
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
    }

    public void removeUser(){
        SharedPreferences.Editor editor = QSApplication.instance().getPreferences().edit();
        editor.remove("id");
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

}

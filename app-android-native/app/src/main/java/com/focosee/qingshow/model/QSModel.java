package com.focosee.qingshow.model;

import android.content.SharedPreferences;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;

import java.util.HashMap;
import java.util.Map;

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
        if(user.role == MongoPeople.GUEST) return true;
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
        if(isGuest()) {
            Map<String, Integer> params = new HashMap<>();
            params.put("role", 1);
            UserCommand.update(params, new Callback());
        }
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

}

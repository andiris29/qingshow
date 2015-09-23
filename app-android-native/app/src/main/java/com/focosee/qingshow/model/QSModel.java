package com.focosee.qingshow.model;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;

/**
 * Created by i068020 on 2/21/15.
 */
public enum QSModel {
    INSTANCE;

    private MongoPeople user;

    public boolean loggedin() {
//        if (user == null) {
//            String id = QSApplication.instance().getPreferences().getString("id", "");
//            Log.d(QSModel.class.getSimpleName(), "_id:" + id);
//            if (TextUtils.isEmpty(id))
//                return false;
//        }
        return null != user;
    }

    public MongoPeople getUser() {
        return user;
    }

    public void setUser(MongoPeople _user) {
        this.user = _user;
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

}

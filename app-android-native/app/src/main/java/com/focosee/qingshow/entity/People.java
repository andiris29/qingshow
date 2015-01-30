package com.focosee.qingshow.entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;

public class People implements Serializable{
    public String _id;
    public UserInfo userinfo;
    public String update;
    public String create;
    public String[] followBrandRefs;
    public String[] followRefs;
    public String[] followerRefs;
    public String[] likingShowRefs;
    public String[] hairTypes;
    public String[] roles;
    public int __v;
    public int gender;
    public String portrait;
    public String height;
    public String name;
    public String weight;
    public String background;
    public String clothingSize;
    public String shoeSize;

    public class UserInfo {
        public String id;
        public String encryptedPassword;
    }

    public static People getPeopleEntitis(JSONObject response) {

        try {
            String tempString = response.getJSONObject("data").getJSONObject("people").toString();

            Type type = new TypeToken<People>() {
            }.getType();

            Gson gson = new Gson();
            return gson.fromJson(tempString, type);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
}

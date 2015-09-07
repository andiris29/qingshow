package com.focosee.qingshow.httpapi.response.dataparser;

import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/9/6.
 */
public class ReturnReceiverPaser {

    public static MongoPeople.Receiver paser(JSONObject response){
        try {
            Gson gson = QSGsonFactory.create();
            String receiver = response.getJSONObject("data").getJSONArray("receiver").toString();
            return gson.fromJson(receiver, new TypeToken<ArrayList<MongoPeople.Receiver>>() {
            }.getType());
        } catch (JSONException e) {
            return null;
        }
    }

}

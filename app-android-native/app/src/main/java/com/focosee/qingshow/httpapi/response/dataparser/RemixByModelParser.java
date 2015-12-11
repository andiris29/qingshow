package com.focosee.qingshow.httpapi.response.dataparser;

import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.model.vo.remix.RemixByItem;
import com.focosee.qingshow.model.vo.remix.RemixByModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * Created by Administrator on 2015/12/3.
 */
public class RemixByModelParser {

    public static RemixByModel parse(JSONObject response) {
        return ParserGenerator.parse(response, TypeToken.get(RemixByModel.class), "");
    }
}

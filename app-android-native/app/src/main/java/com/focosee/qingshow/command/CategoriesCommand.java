package com.focosee.qingshow.command;

import com.android.volley.Response;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.CategoryParser;
import com.focosee.qingshow.model.CategoriesModel;
import com.focosee.qingshow.model.vo.mongo.MongoCategories;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/9/22.
 */
public class CategoriesCommand {

    public static void getCategories(final Callback callback) {

        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getQueryCategories(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (!MetadataParser.hasError(response)) {
                    Map<String, MongoCategories> categoriesMap = new HashMap<>();
                    for (MongoCategories categories : CategoryParser.parseQuery(response)) {
                        categoriesMap.put(categories._id, categories);
                    }
                    CategoriesModel.INSTANCE.setCategories(categoriesMap);
                    callback.onComplete();
                }
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }
}

package com.focosee.qingshow.httpapi;

import android.util.Log;

import com.android.volley.Request.Method;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.RxRequest;
import com.focosee.qingshow.httpapi.response.dataparser.CategoryParser;
import com.focosee.qingshow.httpapi.response.dataparser.FeedingAggregationParser;
import com.focosee.qingshow.httpapi.response.dataparser.ItemFeedingParser;
import com.focosee.qingshow.httpapi.response.dataparser.ShowParser;
import com.focosee.qingshow.model.vo.aggregation.FeedingAggregation;
import com.focosee.qingshow.model.vo.mongo.MongoCategories;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.model.vo.mongo.MongoShow;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Administrator on 2015/11/19.
 */
public class QSRxApi {

    public static Observable<List<FeedingAggregation>> queryFeedingaggregationMatchNew(String date){
        return RxRequest.createJsonRequest(Method.GET, QSAppWebAPI.getFeedingaggregationMatchnew(date), null)
                .map(new Func1<JSONObject, List<FeedingAggregation>>() {
                    @Override
                    public List<FeedingAggregation> call(JSONObject jsonObject) {
                        return FeedingAggregationParser.parseQuery(jsonObject);
                    }
                });
    }

    public static Observable<List<MongoShow>> createFeedingMatchNewRequest(int pageNo, int pageSize, String from, String to){
        Map<String, Object> reqData = new HashMap<>();
        reqData.put("from", from);
        reqData.put("to", to);

        return RxRequest.createJsonRequest(Method.GET, QSAppWebAPI.getMatchNewApi(pageNo, pageSize, from, to), null)
                .map(new Func1<JSONObject, List<MongoShow>>() {
                    @Override
                    public List<MongoShow> call(JSONObject jsonObject) {
                        return ShowParser.parseQuery(jsonObject);
                    }
                });
    }

    public static Observable<JSONObject> queryCategories(){
        return RxRequest.createJsonRequest(Method.GET, QSAppWebAPI.getQueryCategories(), null);
    }

    public static Observable<List<MongoItem>> queryMatherItems(int pageNo, int pageSize, String categoryRef){
        return RxRequest.createJsonRequest(Method.GET, QSAppWebAPI.getQueryItems(pageNo, pageSize, categoryRef), null)
                .map(new Func1<JSONObject, List<MongoItem>>() {
                    @Override
                    public List<MongoItem> call(JSONObject jsonObject) {
                        return ItemFeedingParser.parse(jsonObject);
                    }
                });
    }
}

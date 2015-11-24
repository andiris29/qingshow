package com.focosee.qingshow.httpapi;

import com.android.volley.Request.Method;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.RxRequest;
import com.focosee.qingshow.httpapi.response.dataparser.ShowParser;
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

    public static Observable<List<MongoShow>> createFeedingaggregationMatchNewRequest(Date from, Date to){
        Map<String, Object> reqData = new HashMap<>();
        reqData.put("from", from);
        reqData.put("to", to);

        return RxRequest.createJsonRequest(Method.POST, QSAppWebAPI.getFeedingaggregationMatchnew(), new JSONObject(reqData))
                .map(new Func1<JSONObject, List<MongoShow>>() {
                    @Override
                    public List<MongoShow> call(JSONObject jsonObject) {
                        return ShowParser.parseQuery(jsonObject);
                    }
                });
    }

    public static Observable<List<MongoShow>> createFeedingMatchNewRequest(int pageNo, int pageSize, Date from, Date to){
        Map<String, Object> reqData = new HashMap<>();
        reqData.put("from", from);
        reqData.put("to", to);

        return RxRequest.createJsonRequest(Method.GET, QSAppWebAPI.getMatchNewApi(pageNo, pageSize), new JSONObject(reqData))
                .map(new Func1<JSONObject, List<MongoShow>>() {
                    @Override
                    public List<MongoShow> call(JSONObject jsonObject) {
                        return ShowParser.parseQuery(jsonObject);
                    }
                });
    }
}

package com.focosee.qingshow.httpapi;

import android.util.Log;

import com.android.volley.Request.Method;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.RxRequest;
import com.focosee.qingshow.httpapi.response.dataparser.CategoryParser;
import com.focosee.qingshow.httpapi.response.dataparser.FeedingAggregationParser;
import com.focosee.qingshow.httpapi.response.dataparser.ItemFeedingParser;
import com.focosee.qingshow.httpapi.response.dataparser.ShowParser;
import com.focosee.qingshow.httpapi.response.dataparser.TradeParser;
import com.focosee.qingshow.model.vo.Remix;
import com.focosee.qingshow.model.vo.aggregation.FeedingAggregation;
import com.focosee.qingshow.model.vo.mongo.MongoCategories;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;

import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
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

    public static Observable<List<FeedingAggregation>> queryFeedingaggregationLatest(){
        return RxRequest.createJsonRequest(Method.GET, QSAppWebAPI.getFeedingaggregationLatest(), null)
                .map(new Func1<JSONObject, List<FeedingAggregation>>() {
                    @Override
                    public List<FeedingAggregation> call(JSONObject jsonObject) {
                    List<FeedingAggregation> feedingAggregations = FeedingAggregationParser.parseQuery(jsonObject);
                    Collections.sort(feedingAggregations, new Comparator<FeedingAggregation>() {
                        @Override
                        public int compare(FeedingAggregation lhs, FeedingAggregation rhs) {
                            return Integer.parseInt(rhs.key) - Integer.parseInt(lhs.key);
                        }
                    });
                    return feedingAggregations;
                    }
                }).flatMap(new Func1<List<FeedingAggregation>, Observable<FeedingAggregation>>() {
                    @Override
                    public Observable<FeedingAggregation> call(List<FeedingAggregation> feedingAggregations) {
                        return Observable.from(feedingAggregations);
                    }
                }).filter(new Func1<FeedingAggregation, Boolean>() {
                    @Override
                    public Boolean call(FeedingAggregation feedingAggregation) {
                        return feedingAggregation.topShows.size() > 0;
                    }
                }).toList();
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

    public static Observable<List<MongoTrade>> tradeOwn(int pageNo, int pageSize){
        return RxRequest.createJsonRequest(Method.GET, QSAppWebAPI.getTradeOwn(pageNo, pageSize), null)
            .map(new Func1<JSONObject, List<MongoTrade>>() {
                @Override
                public List<MongoTrade> call(JSONObject jsonObject) {
                    return TradeParser.parseQuery(jsonObject);
                }
            });
    }

    public static Observable<Remix> matcherRemix(String itemRef){
        return RxRequest.createJsonRequest(Method.GET, QSAppWebAPI.getMatcherRemix(itemRef), null)
                .map(new Func1<JSONObject, Remix>() {
                    @Override
                    public Remix call(JSONObject jsonObject) {
                        return null;
                    }
                });
    }

}

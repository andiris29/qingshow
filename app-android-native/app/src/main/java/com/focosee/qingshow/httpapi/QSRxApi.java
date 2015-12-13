package com.focosee.qingshow.httpapi;

import com.android.volley.Request.Method;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.RxRequest;
import com.focosee.qingshow.httpapi.response.dataparser.BonusParser;
import com.focosee.qingshow.httpapi.response.dataparser.FeedingAggregationParser;
import com.focosee.qingshow.httpapi.response.dataparser.PeopleParser;
import com.focosee.qingshow.httpapi.response.dataparser.RemixByItemParser;
import com.focosee.qingshow.httpapi.response.dataparser.RemixByModelParser;
import com.focosee.qingshow.httpapi.response.dataparser.ShowParser;
import com.focosee.qingshow.httpapi.response.dataparser.TradeParser;
import com.focosee.qingshow.model.vo.aggregation.FeedingAggregation;
import com.focosee.qingshow.model.vo.mongo.MongoBonus;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.model.vo.remix.RemixByItem;
import com.focosee.qingshow.model.vo.remix.RemixByModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.functions.Func1;

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

    public static Observable<List<MongoShow>> feedingTime(int pageNo, int pageSize, String from, String to){
        Map<String, Object> reqData = new HashMap<>();
        reqData.put("from", from);
        reqData.put("to", to);

        return RxRequest.createJsonRequest(Method.GET, QSAppWebAPI.getFeedingTimeApi(pageNo, pageSize, from, to), null)
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

    public static Observable<RemixByItem> remixByItem(String itemRef){
        return RxRequest.createJsonRequest(Method.GET, QSAppWebAPI.getRemixByItem(itemRef), null)
                .map(new Func1<JSONObject, RemixByItem>() {
                    @Override
                    public RemixByItem call(JSONObject jsonObject) {
                        return RemixByItemParser.parse(jsonObject);
                    }
                });
    }

    public static Observable<RemixByModel> remixByModel(String modelRef){
        return RxRequest.createJsonRequest(Method.GET, QSAppWebAPI.getRemixByModel(modelRef),null)
            .map(new Func1<JSONObject, RemixByModel>() {
                @Override
                public RemixByModel call(JSONObject jsonObject) {
                    return RemixByModelParser.parse(jsonObject);
                }
            });
    }

    public static Observable<List<MongoPeople>> queryBuyers(String itemRef){
        Map<String, Object> reqData = new HashMap<>();
        reqData.put("itemRef", itemRef);
        return RxRequest.createJsonRequest(Method.POST, QSAppWebAPI.getQueryBuyers(),new JSONObject(reqData))
                .map(new Func1<JSONObject, List<MongoPeople>>() {
                    @Override
                    public List<MongoPeople> call(JSONObject jsonObject) {
                        return PeopleParser.parseQuery(jsonObject);
                    }
                });
    }

    public static Observable<List<MongoPeople>> queryPeople(String ... ids){
        return RxRequest.createJsonRequest(Method.GET, QSAppWebAPI.getPeopleQueryApi(ids), null)
                .map(new Func1<JSONObject, List<MongoPeople>>() {
                    @Override
                    public List<MongoPeople> call(JSONObject jsonObject) {
                        return PeopleParser.parseQuery(jsonObject);
                    }
                });
    }

    public static Observable<ArrayList<MongoBonus>> getOwnBonus(){
        return RxRequest.createJsonRequest(Method.GET,QSAppWebAPI.getBonusOwn(),null)
                .map(new Func1<JSONObject, ArrayList<MongoBonus>>() {
                    @Override
                    public ArrayList<MongoBonus> call(JSONObject jsonObject) {
                        return BonusParser.parseQuery(jsonObject);
                    }
                });
    }

    public static Observable<ArrayList<MongoBonus>> queryBonus(String ... ids){
        return RxRequest.createJsonRequest(Method.GET, QSAppWebAPI.getQueryBonus(ids),null)
                .map(new Func1<JSONObject, ArrayList<MongoBonus>>() {
                    @Override
                    public ArrayList<MongoBonus> call(JSONObject jsonObject) {
                        return BonusParser.parseQuery(jsonObject);
                    }
                });
    }


}

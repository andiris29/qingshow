package com.focosee.qingshow.util;

import com.focosee.qingshow.model.vo.mongo.MongoTrade;

import java.util.List;

/**
 * Created by Administrator on 2015/7/30.
 */
public class TradeUtil {

    public static List<MongoTrade> tradelistSort(List<MongoTrade> trades){

        for (int i = 0; i < trades.size(); i++) {
            MongoTrade trade = trades.get(i);
            if(trade.status == 1){
                trades.remove(i);
                trades.add(0, trade);
            }
        }
        return trades;
    }

}

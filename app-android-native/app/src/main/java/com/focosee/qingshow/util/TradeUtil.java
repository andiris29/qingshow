package com.focosee.qingshow.util;

import com.focosee.qingshow.model.vo.mongo.MongoTrade;

import java.util.List;

/**
 * Created by Administrator on 2015/7/30.
 */
public class TradeUtil {

    public static List<MongoTrade> tradelistSort(List<MongoTrade> trades){

        MongoTrade temp = null;
        for (int i = 0; i < trades.size() - 1; i++) {
//            MongoTrade trade = trades.get(i);
            for (int j = i;j<trades.size() - i - 1;j++)
            if(trades.get(i).update.compareTo(trades.get(i + 1).update) == 1){
//                trades.remove(i);
//                trades.add(0, trade);
                temp = trades.get(i);
                trades.set(i, trades.get(i + 1));
                trades.set(i + 1, temp);
            }
        }
        return trades;
    }

}

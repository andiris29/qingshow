package com.focosee.qingshow.util.sku;

import android.text.TextUtils;

import java.util.Map;

/**
 * Created by Administrator on 2015/9/8.
 */
public class SkuHelper {
    public static int obtainSkuStock(Map<String,String> skuTable, String props){
        if (!skuTable.containsKey(props))
            return -1;
        String values = skuTable.get(props);
        String stock = TextUtils.split(values,":")[0];
        if (TextUtils.isEmpty(stock))
            return -1;
        return Integer.parseInt(stock);
    }
}

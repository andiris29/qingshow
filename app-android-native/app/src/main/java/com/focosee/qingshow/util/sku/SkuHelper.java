package com.focosee.qingshow.util.sku;

import android.text.TextUtils;

import com.focosee.qingshow.util.StringUtil;

import java.util.Map;

/**
 * Created by Administrator on 2015/9/8.
 */
public class SkuHelper {
    public static float obtainSkuStock(Map<String,String> skuTable, String props){
        props = StringUtil.ignoreDot(props);
        if (!skuTable.containsKey(props))
            return -1;
        String values = skuTable.get(props);
        String stock = TextUtils.split(values,":")[0];
        if (TextUtils.isEmpty(stock))
            return -1;
        return Float.parseFloat(stock);
    }
}

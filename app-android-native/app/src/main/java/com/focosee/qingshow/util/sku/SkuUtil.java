package com.focosee.qingshow.util.sku;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/3/18.
 */
public class SkuUtil {

    public static ArrayMap<String, List<String>> filter(List<String> skuProp) {
        ArrayMap<String, List<String>> result = new ArrayMap<>();
        int i = 1;
        for (String prop : skuProp) {
            if (result.containsKey(getPropName(prop))) {
                result.put("_" + i, getValues(prop));
                i++;
            }
            result.put(getPropName(prop), getValues(prop));
        }
        return result;
    }

    public static ArrayMap<String, List<String>> filter(List<String> skuProp, List<String> keys_order) {
        ArrayMap<String, List<String>> result = new ArrayMap<>();
        int i = 1;
        for(String key : keys_order){
            if(result.containsKey(key)){
                result.put("_" + i, getValues(skuProp.get(keys_order.indexOf(key))));
                i++;
            }
            result.put(key, getValues(skuProp.get(keys_order.indexOf(key))));
        }

        return result;
    }

    public static List<String> getKeyOrder(List<String> skuProp){
        List<String> keys = new ArrayList<>();
        int i = 1;
        for (String prop : skuProp) {
            if (keys.indexOf(getPropName(prop)) > -1) {
                keys.add("_" + i);
                i++;
            }
            keys.add(getPropName(prop));
        }
        return keys;
    }

    public static String getPropName(String prop) {
        return TextUtils.split(prop, ":")[0] == null ? "" : TextUtils.split(prop, ":")[0];
    }

    public static List<String> getValues(String prop) {
        List<String> values = new ArrayList<>();
        String[] split = TextUtils.split(prop, ":");
        if (split.length > 1) {
            for (int i = 1; i < split.length; i++) {
                values.add(split[i]);
            }
        }
        return values;
    }

    public static List<String> propParser(Map<String, List<String>> props, List<String> keys_order) {
        List<String> result = new ArrayList<>();
        for (String key : keys_order) {
            result.add(propParser(key, props.get(key)));
        }
        return result;
    }

    public static String propParser(String key, List<String> values) {
        StringBuilder sb = new StringBuilder();
        sb.append(key.split("_")[0]).append(":");
        for (int i = 0; i < values.size(); i++) {
            if (i == values.size() - 1) {
                sb.append(values.get(i));
            } else
                sb.append(values.get(i)).append(":");
        }
        return sb.toString();
    }

    /**
     * 引入keys_order是为了排序
     * @param selectProps
     * @param keys_order
     * @return
     */
    public static String formetPropsAsTableKey(Map<String, List<String>> selectProps, List<String> keys_order){
        String props = "";

        for(String key : keys_order){
            if(selectProps.containsKey(key)){
                props += ":" + selectProps.get(key).get(0);
            }
        }
        if(!TextUtils.isEmpty(props))
            props = props.substring(1);
        return props;
    }


    public static String getSkuId(String url) {
        Map<String, String> params = getUrlParam(url);
        return params.get("skuId");
    }

    /**
     * 解析出url参数中的键值对
     *
     * @param URL url地址
     * @return url请求参数部分
     */
    public static Map<String, String> getUrlParam(String URL) {
        Map<String, String> mapRequest = new HashMap<String, String>();
        String[] arrSplit = null;
        String strUrlParam = null;
        try {
            URL url = new URL(URL);
            strUrlParam = url.getQuery();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (strUrlParam == null) {
            return mapRequest;
        }
        //每个键值为一组
        arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("[=]");
            //解析出键值
            if (arrSplitEqual.length > 1) {
                //正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
            } else {
                if (arrSplitEqual[0] != "") {
                    //只有参数没有值，不加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }
}

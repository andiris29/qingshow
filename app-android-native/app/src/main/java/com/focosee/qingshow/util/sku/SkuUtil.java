package com.focosee.qingshow.util.sku;

import android.text.TextUtils;

import com.focosee.qingshow.model.vo.mongo.MongoItem;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2015/3/18.
 */
public class SkuUtil {

    public static Map<String, List<String>> filter(List<String> skuProp) {
        Map<String, List<String>> result = new HashMap<>();
        for (String prop : skuProp) {
            result.put(getPropName(prop), getValues(prop));
        }
        return result;
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

    public static List<String> propParser(Map<String, List<String>> props) {
        List<String> result = new ArrayList<>();
        for (String key : props.keySet()) {
            result.add(propParser(key, props.get(key)));
        }
        return result;
    }

    public static String propParser(String key, List<String> values) {
        StringBuilder sb = new StringBuilder();
        sb.append(key).append(":");
        for (int i = 0; i < values.size(); i++) {
            if (i == values.size())
                sb.append(values.get(i));
            else
                sb.append(values.get(i)).append(":");
        }
        return sb.toString();
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

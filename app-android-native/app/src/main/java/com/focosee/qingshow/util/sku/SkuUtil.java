package com.focosee.qingshow.util.sku;

import android.text.TextUtils;

import com.focosee.qingshow.model.vo.mongo.MongoItem;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2015/3/18.
 */
public class SkuUtil {

    public enum KEY {

        COLOR("color", "1627207"),
        SIZE_1("size", "20509"),
        SIZE_2("size", "20518"),
        SIZE_3("size", "20549");

        public String name;
        public String id;

        KEY(String name, String id) {
            this.name = name;
            this.id = id;
        }
    }

    public static HashMap<ArrayList<Prop>, MongoItem.TaoBaoInfo.SKU> filter(LinkedList<MongoItem.TaoBaoInfo.SKU> skus) {

        HashMap<ArrayList<Prop>, MongoItem.TaoBaoInfo.SKU> skusProp;
        if (null == skus) {
            return null;
        }
        skusProp = new HashMap<>();
        for (MongoItem.TaoBaoInfo.SKU sku : skus) {
            skusProp.put(filter(sku), sku);
        }

        return skusProp;
    }


    public static ArrayList<Prop> filter(MongoItem.TaoBaoInfo.SKU sku) {
        ArrayList<Prop> props = new ArrayList<>();
        for (int i = 0; i < sku.properties.split(";").length - 1; i++) {

            Prop prop = new Prop();

            String propStr = sku.properties.split(";")[i + 1];
            if (!TextUtils.isEmpty(propStr)) {
                String strs[] = propStr.split(":");
                String propId = strs[0];
                String propValue = strs[1];
                prop.setPropId(propId);
                prop.setPropValue(propValue);
            }

            if (!TextUtils.isEmpty(sku.properties_name) && sku.properties_name.split(";").length >= i) {
                String name = sku.properties_name.split(";")[i];
                prop.setName(name);
            }

            props.add(prop);
        }
        return props;
    }

    public static String getPropValue(LinkedList<MongoItem.TaoBaoInfo.SKU> skus, String... key){

        String[] keyArgs = key;
        HashMap<ArrayList<Prop>, MongoItem.TaoBaoInfo.SKU> skusProp = filter(skus);
        if(null == skusProp)return null;
        Set<ArrayList<Prop>> keys = skusProp.keySet();
        for(ArrayList<Prop> props : keys){
            for (Prop prop : props){
                for (int i = 0; i < keyArgs.length; i++) {
                    if(prop.propId.equals(keyArgs[i])){
                        return prop.name;
                    }
                }
            }
        }
        return null;
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

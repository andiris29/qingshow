package com.focosee.qingshow.util.sku;

import android.text.TextUtils;

import com.focosee.qingshow.model.vo.mongo.MongoItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

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
        skusProp = new HashMap<ArrayList<Prop>, MongoItem.TaoBaoInfo.SKU>();
        for (MongoItem.TaoBaoInfo.SKU sku : skus) {
            skusProp.put(filter(sku), sku);
        }

        return skusProp;
    }


    public static ArrayList<Prop> filter(MongoItem.TaoBaoInfo.SKU sku) {
        ArrayList<Prop> props = new ArrayList<Prop>();
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

            if(!TextUtils.isEmpty(sku.properties_name) && sku.properties_name.split(";").length >= i){
                String name = sku.properties_name.split(";")[i];
                prop.setName(name);
            }

            props.add(prop);
        }
        return props;
    }
}

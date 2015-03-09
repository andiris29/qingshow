package com.focosee.qingshow.model.vo.mongo;

import android.util.Log;

import com.focosee.qingshow.model.vo.metadata.ImageMetadata;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.GregorianCalendar;
import java.util.LinkedList;

/**
 * Created by i068020 on 2/8/15.
 */
public class MongoItem implements Serializable {
    public static final String DEBUG_TAG = "MongoItem";

    public String _id;

    public int category;
    public String name;
    public LinkedList<Image> images;
    public ImageMetadata imageMetadata;
    public String source;
    public String price;

    public MongoBrand brandRef;
    public BrandNewInfo brandNewInfo;
    public BrandDiscountInfo brandDiscountInfo;
    public TaoBaoInfo taobaoInfo;

    public class Image implements Serializable {
        public String url;
        public String description;
    }

    public class BrandNewInfo implements Serializable {
        public int order;
    }

    public class BrandDiscountInfo implements Serializable {
        public int order;
        public Number price;
    }

    public class TaoBaoInfo implements Serializable{

        public GregorianCalendar refreshTime;
        public String nick;
        public String top_title;
        public String top_num_iid;
        public LinkedList<SKU> skus;

        private class SKU implements Serializable{

            public String stock;
            public String promo_price;
            public String price;
            public String properties_name;
            public String properties;
            public String sku_id;
            public String properties_thumbnail;

        }

        public String getMinPromoPrice(){
            String minPrice = null;
            if(skus == null){
                return null;
            }
            if(skus.size() == 0){
                return null;
            }
            for(int i = 0;i < skus.size();i++){
                if(skus.get(i).promo_price == null){
                    continue;
                }
                if(i == 0){
                    minPrice = skus.get(i).promo_price;
                }else{
                    minPrice = String.valueOf(Math.min(Double.parseDouble(skus.get(i - 1).promo_price),
                            Double.parseDouble(skus.get(i).promo_price)));
                }
            }
            if(null == minPrice){
                return null;
            }
            return minPrice;
        }

        public String getPromoPrice(){
            if(getMinPromoPrice() != null){
                return FormatPrice(String.valueOf(Double.parseDouble(getMinPromoPrice()) - 0.01));
            }
            if(getMinPrice() != null){
                return FormatPrice(String.valueOf(Double.parseDouble(getMinPrice()) - 0.01));
            }
            return "";
        }

        public String getMinPrice(){
            String minPrice = null;
            if(skus == null){
                return null;
            }
            if(skus.size() == 0){
                return null;
            }
            for(int i = 0;i < skus.size();i++){
                if(skus.get(i).price == null){
                    continue;
                }
                if(i == 0){
                    minPrice = skus.get(i).price;
                }else{
                    minPrice = String.valueOf(Math.min(Double.parseDouble(skus.get(i - 1).price),
                            Double.parseDouble(skus.get(i).price)));
                }
            }
            if(null == minPrice){
                return null;
            }
            return minPrice;
        }

        public String getMaxPrice(){
            String maxPrice = null;
            if(skus == null){
                return null;
            }
            if(skus.size() == 0){
                return null;
            }
            for(int i = 0;i < skus.size();i++){
                if(skus.get(i).price == null){
                    continue;
                }
                if(i == 0){
                    maxPrice = skus.get(i).price;
                }else{
                    maxPrice = String.valueOf(Math.max(Double.parseDouble(skus.get(i - 1).price),
                            Double.parseDouble(skus.get(i).price)));
                }
            }
            if(null == maxPrice){
                return null;
            }
            return maxPrice;
        }

        public String getPrice(){
            String minPrice = getMinPrice();
            String maxPrice = getMaxPrice();

            if(maxPrice == null || minPrice == null){
                return null;
            }
            if(minPrice.equals(maxPrice)) {
                return FormatPrice(maxPrice);
            }
            return FormatPrice(minPrice) +"-"+ new DecimalFormat("0.00").format(new BigDecimal(maxPrice));
        }

        public String  FormatPrice(String price){
            return "￥" + new DecimalFormat("0.00").format(new BigDecimal(price));
        }

    }

    public String getItemName() {
        return name;
    }

    public String getItemCategory() {
        String categoryName;
        switch (category) {
            case 0:
                categoryName = "上装";
                break;
            case 1:
                categoryName = "下装";
                break;
            case 2:
                categoryName = "鞋子";
                break;
            case 3:
                categoryName = "配饰";
                break;
            default:
                categoryName = "未定义";
                break;
        }
        return categoryName;
    }

    public String getBrandPortrait() {
        return (null != imageMetadata) ? imageMetadata.url : null;
    }

    public String getSource() {
        return source;
    }

    public String getOriginPrice() {
        return price;
    }

    public String getPrice() {
        Log.i("tag",_id + "_id");
        if (taobaoInfo != null){
            if(taobaoInfo.getPromoPrice() != null) {
                return taobaoInfo.getPromoPrice();
            }else{
                return "";
            }
        } else {
            return "";
        }
    }

    public MongoBrand getBrandRef() {
        return brandRef;
    }

    public String getBrandId() {
        if (null == brandRef) return "";
        return String.valueOf(brandRef.get_id());
    }

    public String getSourcePrice() {
        if (taobaoInfo != null) {
            if (taobaoInfo.getPrice() == null) {
                return "";
            } else {
                return taobaoInfo.getPrice();
            }
        }else {
            return "";
        }
    }

}

package com.focosee.qingshow.model.vo.mongo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import static com.focosee.qingshow.util.StringUtil.FormatPrice;

/**
 * Created by i068020 on 2/8/15.
 */
public class MongoItem implements Serializable {

    public String _id;

    public MongoCategories categoryRef;
    public String name;
    public LinkedList<Image> images;
    public String source;
    public String thumbnail;
    public boolean deactive;
    public int numLike;
    public TaoBaoInfo taobaoInfo;
    public GregorianCalendar create;

    public class Image implements Serializable {
        public String url;
        public String description;
    }

    public class TaoBaoInfo implements Serializable{

        public GregorianCalendar refreshTime;
        public String nick;
        public String top_title;
        public String top_num_iid;
        public LinkedList<SKU> skus;

        public class SKU implements Serializable{

            public String stock;
            public String promo_price;
            public String price;
            public String properties_name;
            public String properties;
            public String sku_id;
            public String properties_thumbnail;

        }

        public String getMinPromoPrice(){
            double minPrice = 0;
            if(skus == null || skus.size() == 0){
                return null;
            }
            for(int i = 0;i < skus.size();i++){
                if(skus.get(i).promo_price == null){
                    continue;
                }
                if(i == 0)
                    minPrice = Double.parseDouble(skus.get(i).promo_price);
                else
                    minPrice = (minPrice > Double.parseDouble(skus.get(i).promo_price)) ? Double.parseDouble(skus.get(i).promo_price) : minPrice;
            }
            if(0 == minPrice){
                return null;
            }
            return String.valueOf(minPrice);
        }

        public String getPromoPrice(){
            if(getMinPromoPrice() != null){
                return FormatPrice(String.valueOf(Double.parseDouble(getMinPromoPrice())));
            }
            if(getMinPrice() != null){
                return FormatPrice(String.valueOf(Double.parseDouble(getMinPrice())));
            }
            return "";
        }

        public String getMinPrice(){
            double minPrice = 0;
            if(skus == null || skus.size() == 0){
                return null;
            }
            for(int i = 0;i < skus.size();i++){
                if(skus.get(i).price == null){
                    continue;
                }
                if(i == 0)
                    minPrice = Double.parseDouble(skus.get(i).price);
                else
                    minPrice = (minPrice >Double.parseDouble(skus.get(i).price)) ? Double.parseDouble(skus.get(i).price) : minPrice;
            }
            if(0 == minPrice){
                return null;
            }
            return String.valueOf(minPrice);
        }

        public String getMaxPrice(){
            double maxPrice = 0;
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
                maxPrice = (maxPrice > Double.parseDouble(skus.get(i).price)) ? maxPrice : Double.parseDouble(skus.get(i).price);
            }
            if(0 == maxPrice){
                return null;
            }
            return String.valueOf(maxPrice);
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

    }

    public String getItemName() {
        return name;
    }

//    public String getItemCategory() {
//        String categoryName;
//        switch (categoryRef) {
//            case :
//                categoryName = "上装";
//                break;
//            case 1:
//                categoryName = "下装";
//                break;
//            case 2:
//                categoryName = "鞋子";
//                break;
//            case 3:
//                categoryName = "配饰";
//                break;
//            default:
//                categoryName = "未定义";
//                break;
//        }
//        return categoryName;
//    }

    public String getSource() {
        return source;
    }

    public String getPrice() {
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

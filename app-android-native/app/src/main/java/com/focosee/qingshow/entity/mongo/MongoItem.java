package com.focosee.qingshow.entity.mongo;

import com.focosee.qingshow.entity.metadata.ImageMetadata;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
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
        if (null == brandDiscountInfo){//tab:最新
            return "￥ " + new DecimalFormat("0.00").format(new BigDecimal(price));
        } else {//tab:优惠
            return "￥" + new DecimalFormat("0.00").format(new BigDecimal(brandDiscountInfo.price.toString()));
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
        return "￥ " + new DecimalFormat("0.00").format(new BigDecimal(price));
    }
}

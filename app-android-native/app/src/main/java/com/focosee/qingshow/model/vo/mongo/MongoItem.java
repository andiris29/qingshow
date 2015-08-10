package com.focosee.qingshow.model.vo.mongo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import static com.focosee.qingshow.util.StringUtil.FormatPrice;

/**
 * Created by i068020 on 2/8/15.
 */
public class MongoItem implements Serializable {

    public String _id;

    public MongoCategories categoryRef;
    public String name;
    public String source;
    public String thumbnail;
    public String price;
    public String promoPrice;
    public String minExpectedPrice;
    public List<String> skuProperties;
    public GregorianCalendar create;
    public String delist;
    public boolean readOnly;

    public class Image implements Serializable {
        public String url;
        public String description;
    }

    public String getItemName() {
        return name;
    }

    public String getSource() {
        return source;
    }

}

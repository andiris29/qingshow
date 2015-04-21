package com.focosee.qingshow.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by Administrator on 2015/3/19.
 */
public class StringUtil {
    public static String  FormatPrice(String price){
        return "￥" + new DecimalFormat("0.00").format(new BigDecimal(price));
    }
}

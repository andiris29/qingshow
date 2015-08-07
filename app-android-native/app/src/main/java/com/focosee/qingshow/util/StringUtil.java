package com.focosee.qingshow.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created by Administrator on 2015/3/19.
 */
public class StringUtil {
    public static String formatDiscount(String current, String original) {
        String str;
        double dis = Double.parseDouble(current) / Double.parseDouble(original) * 100;
        NumberFormat fmt = NumberFormat.getPercentInstance();
        fmt.setMaximumFractionDigits(2);
        str = fmt.format(dis);
        return str + "%";
    }

    public static String FormatPrice(String price) {
        return "¥" + new DecimalFormat("0.00").format(new BigDecimal(price));
    }

    public static String formatHeightAndWeight(int heigth, int weight) {
        return heigth + "cm," + weight + "kg";
    }
}

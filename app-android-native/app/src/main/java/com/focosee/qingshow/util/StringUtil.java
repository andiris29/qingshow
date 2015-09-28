package com.focosee.qingshow.util;

import android.text.TextUtils;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/3/19.
 */
public class StringUtil {
    public static String formatDiscount(String current, String original) {
        if(TextUtils.isEmpty(current) || TextUtils.isEmpty(original))return "";
        String str = "";
        double dis = Double.parseDouble(current) / Double.parseDouble(original);
        if (dis < 0.1) {
            str = "1";
        } else if (dis > 0.9) {
            str = "9";
        } else {
            str = String.valueOf((int)Math.floor((dis * 10)));
        }
        return str + "折";
    }

    public static String FormatPrice(String price) {
        return "¥" + formatPriceWithoutSign(price);
    }

    public static String formatPriceWithoutSign(String price){
        if(TextUtils.isEmpty(price)) return "0.00";
        return new DecimalFormat("0.00").format(new BigDecimal(price));
    }

    public static String formatHeightAndWeight(Number heigth, Number weight) {
        return (heigth == null ? 0 : heigth) + "cm," + (weight == null ? 0 : weight) + "kg";
    }

    public static String formatSKUProperties(List<String> properties) {
        if (null == properties) return "";
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < properties.size(); i++) {
            String p = properties.get(i).trim();
            if(TextUtils.isEmpty(p))continue;
            p = p.replace(" ", "");
            String[] values = p.split(":");
            if(values.length > 1){
                buffer.append(values[1]);
            }
            buffer.append(" ");
        }
        return buffer.toString();
    }

    public static String formatPriceDigits(double price, int which) {//取小数点后两位
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(which);
        return String.valueOf(nf.format(price));
    }

    public static String calculationException(double expectedPrice, String promoPrice) {
        return calculationException(expectedPrice, Double.parseDouble(promoPrice));
    }

    public static String calculationException(double expectedPrice, double promoPrice) {
        if(promoPrice <= 0)return "";
        int result = (int)(expectedPrice * 10 / promoPrice);
        if (result < 1) result = 1;
        if (result > 9) result = 9;
        return String.valueOf(result) + "折";
    }

    public static String ignoreDot(String str){
        return str.replace(".","");
    }

    public static boolean matchNum(String str){
        String pattern = "[0-9]+(.[0-9]+)?";
        // 对()的用法总结：将()中的表达式作为一个整体进行处理，必须满足他的整体结构才可以。
        // (.[0-9]+)? ：表示()中的整体出现一次或一次也不出现
        return Pattern.compile(pattern).matcher(str).matches();
    }

}

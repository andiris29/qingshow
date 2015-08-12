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
        String str = "";
        double dis = Double.parseDouble(current) / Double.parseDouble(original);
        if (dis < 0.1) {
            str = "1" + "折";
        } else if (dis > 0.9) {
            str = "9" + "折";
        } else {
            str = (int) (dis * 10) + "折";
        }
        return str;
    }

    public static String FormatPrice(String price) {
        return "¥" + new DecimalFormat("0.00").format(new BigDecimal(price));
    }

    public static String formatHeightAndWeight(int heigth, int weight) {
        return heigth + "cm," + weight + "kg";
    }

    public static String formatSKUProperties(List<String> properties) {
        if (null == properties) return "";
        StringBuffer buffer = new StringBuffer();
        int length = properties.size() > 2 ? 2 : properties.size();
        for (int i = 0; i < length; i++) {
            String p = properties.get(i);
            p = p.replace(":, ", "\n");
            p = p.replace("尺码", "");
            p = p.replace("[", "");
            p = p.replace("]", "");
            p = p.replace("，", "");
            p = p.replace(",", "");
            p = p.replace(":", "：");
            if (p.indexOf("：") == 0) {
                p = p.substring(1);
            }
            if (p.lastIndexOf("：") == p.length() - 1) {
                p = p.substring(0, p.length() - 1);
            }
            buffer.append(p);
            buffer.append("\n");
        }
        String str = buffer.toString().substring(0, buffer.length() - "\n".length());
        if (buffer.toString().indexOf("规格") > -1) return str;
        return "规格：" + str;
    }

    public static String formatPriceDigits(double price) {//取小数点后两位
        return FormatPrice(formatPriceDigits(price, 2));
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
        int result = new BigDecimal(formatPriceDigits(expectedPrice * 10 / promoPrice, 2)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        if (result < 1) result = 1;
        if (result > 9) result = 9;
        return String.valueOf(result) + "折";
    }
}

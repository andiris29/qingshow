package com.focosee.qingshow.util;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/3/19.
 */
public class StringUtil {
    public static String formatDiscount(String current, String original) {
        String str = "";
        double dis = Double.parseDouble(current) / Double.parseDouble(original);
        if (dis < 0.1) {
            str = "1";
        } else if (dis > 0.9) {
            str = "9";
        } else {
            str = String.valueOf((dis * 10));
        }
        return str + "折";
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
        for (int i = 0; i < properties.size(); i++) {
            String p = properties.get(i).trim();
            if(TextUtils.isEmpty(p))continue;
            p = p.replace(" ", "");
            System.out.println("properties:" + p);
            String[] values = p.split(":");
            if(values.length > 1){
                buffer.append(values[1]);
            }
            buffer.append(" ");
        }
        return buffer.toString();
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

    /**
     * @param number
     * @return
     * @Title : filterNumber
     * @Type : FilterStr
     * @date : 2014年3月12日 下午7:23:03
     * @Description : 过滤出数字
     */
    public static String filterNumber(String number) {
        number = number.replaceAll("[^(0-9)]", "");
        return number;
    }

    /**
     * @param alph
     * @return
     * @Title : filterAlphabet
     * @Type : FilterStr
     * @date : 2014年3月12日 下午7:28:54
     * @Description : 过滤出字母
     */
    public static String filterAlphabet(String alph) {
        alph = alph.replaceAll("[^(A-Za-z)]", "");
        return alph;
    }

    /**
     * 过滤特殊字符
     * @param pro
     * @return
     */
    public static String filterIllegal(String pro) {
        String regEx = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(pro);
        return m.replaceAll("").trim();
    }

}

package com.focosee.qingshow.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Administrator on 2015/3/19.
 */
public class StringUtil {
    public static String  FormatPrice(String price){
        return "￥" + new DecimalFormat("0.00").format(new BigDecimal(price));
    }

    public static String formatHeightAndWeight(int heigth, int weight){
        return heigth + "cm," + weight + "kg";
    }

    public static String formatSKUProperties(List<String> properties){
        StringBuffer skuProperties = new StringBuffer();
        for (String p : properties){
            if(p.indexOf(":") == 0){
                p = p.substring(1);
            }
            if(p.lastIndexOf("：") == p.length() - 1 || p.lastIndexOf(":") == p.length() - 1){
                p = p.substring(0, p.length() - 1);
            }
            skuProperties.append(p);
            skuProperties.append("\n\n");
        }

        return "尺码:" + skuProperties.toString().substring(0, skuProperties.toString().length() - "\n\n".length());
    }
}

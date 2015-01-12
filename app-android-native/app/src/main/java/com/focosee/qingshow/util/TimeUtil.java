package com.focosee.qingshow.util;

public class TimeUtil {
    public static String getS04CommentTimeFormatString(String inputString) {
        String resultString = "";
        try {
            resultString += inputString.split("T")[0] + " " + inputString.split("T")[1].split("\\.")[0];
        } catch (Exception e) {
            resultString = inputString;
        }
        return resultString;
    }
}

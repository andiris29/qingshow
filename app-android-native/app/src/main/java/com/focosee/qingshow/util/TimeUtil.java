package com.focosee.qingshow.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class TimeUtil {
    public static String getS04CommentTimeFormatString(String inputString) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return simpleDateFormat.format(TimeUtil.getStringToCal(inputString).getTime());
    }

    public static Calendar getStringToCal(String date) {
        final String year = date.substring(0, 4);
        final String month = date.substring(5, 7);
        final String day = date.substring(8, 10);
        final String hour = date.substring(11, 13);
        final String minute = date.substring(14, 16);
        final String second = date.substring(17, 19);
        final int millisecond = Integer.valueOf(date.substring(20, 23));
        Calendar result =
                new GregorianCalendar(Integer.valueOf(year),
                        Integer.valueOf(month) - 1, Integer.valueOf(day),
                        Integer.valueOf(hour), Integer.valueOf(minute),
                        Integer.valueOf(second));
        result.set(Calendar.MILLISECOND, millisecond);
        result.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        return result;
    }

    public static String formatWeekInfo(int dayOfWeek) {
        String result;
        switch (dayOfWeek) {
            case 1:
                result = "星期日 SUN";
                break;
            case 2:
                result = "星期一 MON";
                break;
            case 3:
                result = "星期二 TUE";
                break;
            case 4:
                result = "星期三 WED";
                break;
            case 5:
                result = "星期四 THURS";
                break;
            case 6:
                result = "星期五 FRI";
                break;
            default:
                result = "星期六 SAT";
                break;
        }
        return result;
    }

}

package com.focosee.qingshow.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtil {

    public static GregorianCalendar parseUTC(String utc) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(dateFormat.parse(utc));
        return calendar;
    }

    public static String parseDateString(GregorianCalendar calendar){

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(calendar.getTime());
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

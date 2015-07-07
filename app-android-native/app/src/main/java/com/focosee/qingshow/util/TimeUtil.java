package com.focosee.qingshow.util;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    public static String parseDateString(GregorianCalendar calendar) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(calendar.getTime());
    }

    public static String formatWeekInfo(int dayOfWeek) {
        String result;
        switch (dayOfWeek) {
            case 1:
                result = "SUNDAY";
                break;
            case 2:
                result = "MONDAY";
                break;
            case 3:
                result = "TUESDAY";
                break;
            case 4:
                result = "WEDNESDAY";
                break;
            case 5:
                result = "THURSDAY";
                break;
            case 6:
                result = "FRIDAY";
                break;
            default:
                result = "SATURDAY";
                break;
        }
        return result;
    }

    public static String formatManthInfo(int manth) {
        String result = "";
        switch (manth) {
            case 0:
                result = "Jan";
                break;
            case 1:
                result = "Feb";
                break;
            case 2:
                result = "Mar";
                break;
            case 3:
                result = "Apr";
                break;
            case 4:
                result = "May";
                break;
            case 5:
                result = "Jun";
                break;
            case 6:
                result = "Jul";
                break;
            case 7:
                result = "Aug";
                break;
            case 8:
                result = "Sep";
                break;
            case 9:
                result = "Oct";
                break;
            case 10:
                result = "Nov";
                break;
            case 11:
                result = "Dec";
                break;
        }
        return result;
    }

    public static String formatDateTime(long time) {
        if (0 == time) {
            return "";
        }
        SimpleDateFormat _mDateFormat = new SimpleDateFormat("MM-dd HH:mm");
        return _mDateFormat.format(new Date(time));
    }

}

package com.focosee.qingshow.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.StringTokenizer;
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

        DateFormat dateFormat = new SimpleDateFormat("yyyy- MM- dd");
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

    public static String formatDateTime(GregorianCalendar time){
        return formatDateTime(time.getTime().getTime());
    }

    public static String formatDateTime_CN_Pre(GregorianCalendar time){
        if(null == time){
            return null;
        }

        int timeI = (int)Math.abs(time.getTimeInMillis() - System.currentTimeMillis()) / 1000;//s
        int m = timeI / 60;//m
        if(m < 60){
            return String.valueOf(m + "分钟");
        }else{
            m = m / 60;//h
        }
        if(m < 24){
            return String.valueOf(m + "小时");
        }else{
            m = m / 24;//d
        }
        if(m < 30){
            return String.valueOf(m + "天");
        }else{
            return String.valueOf(m / 30 + "月");//month
        }
    }

    public static String formatDateTime_CN(GregorianCalendar time){
        if(null == time){
            return null;
        }
        SimpleDateFormat simpleDateFormat = null;
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        int day = (int)Math.abs(time.getTimeInMillis() - todayStart.getTimeInMillis()) / (1000 * 60 * 60 * 24);
        if(day < 1) {
            simpleDateFormat = new SimpleDateFormat("HH:mm");
            return simpleDateFormat.format(new Date(time.getTimeInMillis()));
        }
        if(day == 1){
            return "昨天";
        }
        simpleDateFormat = new SimpleDateFormat("yy/MM/dd");
        return simpleDateFormat.format(new Date(time.getTimeInMillis()));
    }

    public static int day_between(GregorianCalendar date1, GregorianCalendar date2){
        return Integer.parseInt(String.valueOf(Math.abs(date1.getTimeInMillis() - date2.getTimeInMillis()) / (1000 * 3600 * 24)));
    }

}

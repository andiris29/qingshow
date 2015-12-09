package com.focosee.qingshow.util;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        return dateFormat.format(calendar.getTime());
    }

    public static String formatTime(GregorianCalendar calendar){
        calendar.setTimeZone(TimeZone.getDefault());
        try {
            String timeZone = URLEncoder.encode(new SimpleDateFormat("Z", Locale.US).format(calendar.getTime()), "UTF-8");
            String date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(calendar.getTime());
            return date + timeZone;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
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

    public static String formatDateTime(long time, SimpleDateFormat _mDateFormat) {
        if (0 == time) {
            return "";
        }
        return _mDateFormat.format(new Date(time));
    }


    public static String formatDateTime(GregorianCalendar time, SimpleDateFormat _mDateFormat){
        return formatDateTime(time.getTime().getTime(), _mDateFormat);
    }

    public static String formatDateTimeUS(GregorianCalendar time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time.getTimeInMillis());
        String result = formatManthInfo(calendar.get(Calendar.MONTH)) + "." + calendar.get(Calendar.DAY_OF_MONTH);
        return result;
    }

    public static String formatDateTime_CN_Pre(GregorianCalendar time){
        if(null == time){
            return "1m";
        }

        int timeI = (int)Math.abs(time.getTimeInMillis() - System.currentTimeMillis()) / 1000;//s
        int m = timeI / 60;//m
        if(m == 0) return "1m";
        if(m < 0){
            m = Math.abs(m);
        }
        if(m < 60){
            return String.valueOf(m + "m");
        }else{
            m = m / 60;//h
        }
        if(m < 24){
            return String.valueOf(m + "h");
        }

        return formatDateTimeUS(time);//month
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
        int day = (int)((time.getTimeInMillis() - todayStart.getTimeInMillis()) / (1000 * 60 * 60));
        if(day > 0) {
            simpleDateFormat = new SimpleDateFormat("HH:mm");
            return simpleDateFormat.format(new Date(time.getTimeInMillis()));
        }
        if(day > -24 && day < 0){
            return "昨天";
        }
        simpleDateFormat = new SimpleDateFormat("yy/MM/dd");
        return simpleDateFormat.format(new Date(time.getTimeInMillis()));
    }

    public static int day_between(GregorianCalendar date1, GregorianCalendar date2){
        return Integer.parseInt(String.valueOf(Math.abs(date1.getTimeInMillis() - date2.getTimeInMillis()) / (1000 * 3600 * 24)));
    }

}

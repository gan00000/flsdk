package com.core.base.utils;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtil {
    private static final String TAG = TimeUtil.class.getSimpleName();
    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String getDateStr(String timeStamp, String format){
        if (SStringUtil.isEmpty(format)){
            format = TIME_FORMAT;
        }
        Date ydate = new Date(Long.parseLong(timeStamp));
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(ydate);
    }

    public static String getDateStr(Date mDate, String format){
        if (SStringUtil.isEmpty(format)){
            format = TIME_FORMAT;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(mDate);
    }

    public static Date getYesterday() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date yesterdayDate = calendar.getTime();

        return yesterdayDate;
    }

    public static Date getYesterday(long timeStamp) {
        Date date = new Date(timeStamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date yesterdayDate = calendar.getTime();

        return yesterdayDate;
    }

    public static String getDisplayTime(Context context) {
        String displayTime = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_FORMAT);
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        displayTime = simpleDateFormat.format(date);
        Log.i(TAG, "Display time : " + displayTime);
        return displayTime;
    }

    public static String getBeiJingTime(Context context) {
        String bjTime = "";
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        bjTime = dateFormat.format(date);
        Log.i(TAG+ " beijing time: ", bjTime);
        return bjTime;
    }

    public static boolean isSameMonth(long startTime) {
        if(startTime == 0) {
            PL.i("时间戳为0");
            return false;
        }
        long currentTime = System.currentTimeMillis();
        Date startDate = new Date(startTime);
        Date currentDate = new Date(currentTime);

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);
        int startYear = startCalendar.get(Calendar.YEAR);
        int startMonth = startCalendar.get(Calendar.MONTH);

        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(currentDate);
        int currentYear = currentCalendar.get(Calendar.YEAR);
        int currentMonth = currentCalendar.get(Calendar.MONTH);

        if(currentYear > startYear) {
            return false;
        } else {
            return currentMonth == startMonth;
        }
    }
}

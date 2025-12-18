package com.core.base.utils;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
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

    public static String getCurTime(Context context) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_FORMAT);
        //获取当前时间
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

    public static long getTimestamp(String dateString) {
        return getTimestamp(dateString, TIME_FORMAT);
    }

    public static long getTimestamp(String dateString, String pattern) {

        // 创建 SimpleDateFormat 实例
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        // 设置时区为北京时区 (Asia/Shanghai)
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 或 "GMT+8"

        try {
            // 解析日期字符串
            Date date = sdf.parse(dateString);
            // 返回毫秒级时间戳
            return date.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0; // 解析失败时返回 -1

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

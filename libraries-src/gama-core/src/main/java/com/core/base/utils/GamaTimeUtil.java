package com.core.base.utils;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class GamaTimeUtil {
    private static final String TAG = GamaTimeUtil.class.getSimpleName();
    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

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
}

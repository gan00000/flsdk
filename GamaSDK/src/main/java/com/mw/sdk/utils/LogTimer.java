package com.mw.sdk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.core.base.utils.PL;
import com.mw.sdk.ads.EventConstant;
import com.mw.sdk.R;
import com.mw.sdk.ads.SdkEventLogger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Log Event 定時回報程式
 *
 * @author yangchaofu
 * @since 2018/9/5
 */
public class LogTimer {

    private static LogTimer instance;
    private long startTime;
    private Activity context;
    private boolean isCanceled = true;
    private static final String TAG = "Gama LogTimer";

    public static LogTimer getInstance() {
        if (instance == null) {
            instance = new LogTimer();
        }
        return instance;
    }

    private Timer timer;

    public void start(Activity context) {
        this.context = context;
        if (isCanceled) {
            startTime = System.currentTimeMillis();
            timer = new Timer(true);
            TimerTask task = new TimerTask() {
                public void run() {
                    isCanceled = false;
                    end();
                }
            };
            timer.schedule(task, 1000, 1000 * 60);
        }
    }

    private void end() {
        SharedPreferences sp;
        sp = context.getSharedPreferences("log_timer", Context.MODE_PRIVATE);
        long endTime = System.currentTimeMillis();
        long previous = sp.getLong("previous_time", 0);
        long result = (endTime - startTime) + previous;
        startTime = System.currentTimeMillis();

        sp.edit().putLong("previous_time", result).apply();
        Log.d(TAG, String.valueOf(result));
        log(sp, result);
    }

    public void reset(Context context) {
        SharedPreferences sp = context.getSharedPreferences("log_timer", Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }

    private void log(SharedPreferences sp, long time) {
        int[] intArray = context.getResources().getIntArray(R.array.gama_minute);
        if (intArray.length < 1) {
            PL.e(TAG, "no minute event set!");
            cancel();
            return;
        }
        for (int i = 0; i < intArray.length; i++) {
            int minute = intArray[i];
            String eventName = String.format(EventConstant.GAMA_EVENT_MINUTE, minute);
            if (time >= minute * 1000 * 60 && !sp.getBoolean(eventName, false)) {
                SdkEventLogger.trackingWithEventName(context, eventName, null, null);
                sp.edit().putBoolean(eventName, true).apply();
//                if(i == intArray.length - 1) {
//                    PL.i(TAG, "任務完成，不用再執行。");
//                    cancel();
//                }
            }
            if(i == intArray.length - 1 && sp.getBoolean(eventName, false)) {
                PL.i(TAG, "任務完成，不用再執行。");
                cancel();
            }
        }
    }

    public void cancel() {
        PL.i(TAG, "cancel logTimer。");
        timer.cancel();
        isCanceled = true;
    }

}

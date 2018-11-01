package com.gama.sdk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.core.base.utils.PL;
import com.facebook.appevents.AppEventsLogger;

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
    private Context context;
    private boolean isCanceled = true;
    private AppEventsLogger logger;

    public static LogTimer getInstance() {
        if (instance == null) {
            instance = new LogTimer();
        }
        return instance;
    }

    private Timer timer;

    public void start(Context context) {
        this.context = context;
        if (isCanceled) {
            logger = AppEventsLogger.newLogger(context);
            startTime = System.currentTimeMillis();
            timer = new Timer(true);
            TimerTask task = new TimerTask() {
                public void run() {
                    isCanceled = false;
                    end();
                }
            };
            timer.schedule(task, 1000, 60000);
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
        Log.d("LogTimer", String.valueOf(result));
        log(sp, result);
    }

    public void reset(Context context) {
        SharedPreferences sp = context.getSharedPreferences("log_timer", Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }

    private void log(SharedPreferences sp, long time) {
        if(time >= 10000 * 60 && !sp.getBoolean("log_1", false)) {
//            Log.i("LogTimer", "此程式從第一次啟動後，運作了 10 秒");
            logger.logEvent("10min");
            sp.edit().putBoolean("log_1", true).apply();
        }
        if(time >= 20000 * 60 && !sp.getBoolean("log_2", false)) {
//            Log.i("LogTimer", "此程式從第一次啟動後，運作了 20 秒");
            logger.logEvent("20min");
            sp.edit().putBoolean("log_2", true).apply();
        }
        if(time >= 30000 * 60 && !sp.getBoolean("log_3", false)) {
//            Log.i("LogTimer", "此程式從第一次啟動後，運作了 30 秒，任務完成。");
            logger.logEvent("30min");
            sp.edit().putBoolean("log_3", true).apply();
            cancel();
        } else if (time >= 30000 * 60) {
            PL.i("LogTimer", "任務完成，不用再執行。");
            cancel();
        }
    }

    public void cancel() {
        timer.cancel();
        isCanceled = true;
    }

}

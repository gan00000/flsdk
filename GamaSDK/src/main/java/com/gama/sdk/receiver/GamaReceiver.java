package com.gama.sdk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.gama.base.utils.SLog;
import com.gama.pay.gp.GooglePayHelper;
import com.gama.sdk.ads.GamaAdsConstant;
import com.gama.sdk.ads.StarEventLogger;

/**
 * 用于处理一些跨模块的事情
 */
public class GamaReceiver extends BroadcastReceiver {
    private static final String TAG = GamaReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = "";
        if (intent != null) {
            action = intent.getAction();
        }
        if (TextUtils.isEmpty(action)) {
            return;
        }
        switch (action) {
            case GooglePayHelper.ACTION_PAY_REPLACE_OK:
                Bundle extras = intent.getExtras();
                if(extras != null) {
                    SLog.logI(TAG, "Pay replace success, start event tracking");
                    StarEventLogger.trackinPayEvent(context, extras);
                } else {
                    SLog.logI(TAG, "Pay replace success, but purchase null, stop event tracking");
                }
                break;

            case GooglePayHelper.ACTION_PAY_QUERY_TASK_START:
                StarEventLogger.trackingWithEventName(context, GamaAdsConstant.GAMA_EVENT_PAY_QUERY, null, null);
                break;
        }
    }
}

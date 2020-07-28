package com.flyfun.sdk.receiver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.flyfun.pay.gp.GooglePayHelper;
import com.flyfun.sdk.ads.SdkAdsConstant;
import com.flyfun.base.utils.SLog;
import com.flyfun.sdk.ads.StarEventLogger;

public class SDKReceiver extends BaseGamaReceiver {
    private static final String TAG = SDKReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
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
                StarEventLogger.trackingWithEventName(context, SdkAdsConstant.GAMA_EVENT_PAY_QUERY, null, null);
                break;
        }
    }
}

package com.mw.sdk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 用于处理一些跨模块的事情
 */
public class BaseGamaReceiver extends BroadcastReceiver {
    private static final String TAG = BaseGamaReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}

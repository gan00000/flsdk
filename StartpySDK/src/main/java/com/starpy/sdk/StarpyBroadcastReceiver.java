package com.starpy.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.starpy.base.utils.StarPyUtil;

/**
 * Created by gan on 2017/8/29.
 */

public class StarpyBroadcastReceiver extends BroadcastReceiver {

    public static final String INSTALL_REFERRER_ACTION = "com.android.vending.INSTALL_REFERRER";

    @Override
    public void onReceive(Context context, Intent intent) {
        PL.i("StarpyBroadcastReceiver onReceive");
        if (intent != null && INSTALL_REFERRER_ACTION.equals(intent.getAction())){
            String referrer = intent.getStringExtra("referrer");
            PL.i("referrer:" + referrer);
            if (SStringUtil.isNotEmpty(referrer)){
                StarPyUtil.saveReferrer(context,referrer);
            }
        }
    }
}

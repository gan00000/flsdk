package com.gamamobi.cafe;

import android.content.Context;

import com.core.base.utils.PL;
import com.core.base.utils.ResUtil;
import com.naver.glink.android.sdk.Glink;

public class GamaCafeHelper {
    private static final String TAG = GamaCafeHelper.class.getSimpleName();

    public static void initCafe(Context context) {
        int cafeId = Integer.parseInt(ResUtil.findStringByName(context, "cafeId"));
        String clientSecret = ResUtil.findStringByName(context, "clientSecret");
        String clientId = ResUtil.findStringByName(context, "clientId");
        PL.i(TAG, "cafeId : " + cafeId );
        Glink.init(context, clientId, clientSecret, cafeId);

    }

    public static void showCafe(Context context) {
        Glink.startHome(context);
    }

    public static void stopCafe(Context context) {
        Glink.stop(context);
    }
}

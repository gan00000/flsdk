package com.gamamobi.ads.plug.aj;

import android.content.Context;
import android.text.TextUtils;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;
import com.adjust.sdk.LogLevel;
import com.core.base.callback.ISReqCallBack;
import com.core.base.utils.PL;
import com.flyfun.base.cfg.ResConfig;
import com.flyfun.sdk.ads.SdkAdsConstant;
import com.gamamobi.ads.plug.aj.cmd.GamaAjEventListCmd;
import com.gamamobi.ads.plug.aj.utils.GamaAjHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class GamaAj {
    private static final String TAG = GamaAj.class.getSimpleName();

    private static boolean isAdjustEnable = false;

    public static void activeAj(final Context context) {
        //adjust激活
        String appToken = ResConfig.getConfigInAssetsProperties(context,"sdk_ads_adjust_dev_key");
        String appSecret = ResConfig.getConfigInAssetsProperties(context,"sdk_ads_adjust_secret");
        if(TextUtils.isEmpty(appToken)) {
            PL.e(TAG, "aj dev key empty!");
            isAdjustEnable = false;
            return;
        } else {
            isAdjustEnable = true;
            String environment = AdjustConfig.ENVIRONMENT_PRODUCTION;
//            String environment = AdjustConfig.ENVIRONMENT_SANDBOX;
            AdjustConfig config = new AdjustConfig(context, appToken, environment);
            config.setLogLevel(LogLevel.VERBOSE);
            if(!TextUtils.isEmpty(appSecret)){
                String[] secrets = appSecret.split(",");
                PL.i(TAG, "appSecret : " + appSecret);
                config.setAppSecret(Long.parseLong(secrets[0]), Long.parseLong(secrets[1]),
                        Long.parseLong(secrets[2]), Long.parseLong(secrets[3]), Long.parseLong(secrets[4]));
            }
            Adjust.addSessionCallbackParameter("gameCode", ResConfig.getGameCode(context));
            Adjust.onCreate(config);
        }

        GamaAjEventListCmd cmd = new GamaAjEventListCmd(context);
        cmd.setReqCallBack(new ISReqCallBack() {
            @Override
            public void success(Object o, String rawResult) {
                try {
                    JSONObject jsonObject = new JSONObject(rawResult);
                    String jsonResult = jsonObject.toString();
                    GamaAjHelper.saveGamaAjList(context, jsonResult);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void timeout(String code) {

            }

            @Override
            public void noData() {

            }

            @Override
            public void cancel() {

            }
        });
        cmd.excute();
    }

    public static void onResume(Context context) {
        PL.i(TAG, "onResume");
        if(!isAdjustEnable) {
            PL.i(TAG, "adjust disable");
            return;
        }
        Adjust.onResume();
    }

    public static void onPause(Context context) {
        PL.i(TAG, "onPause");
        if(!isAdjustEnable) {
            PL.i(TAG, "adjust disable");
            return;
        }
        Adjust.onPause();
    }

    public static void trackEvent(Context context, String event, Map<String, Object> map) {
        if(!isAdjustEnable) {
            PL.i(TAG, "adjust disable");
            return;
        }
        String eventToken = GamaAjHelper.getEventToken(context, event);
        if(TextUtils.isEmpty(eventToken)) {
            PL.e(TAG, "no token found");
            return;
        }
        AdjustEvent ae = new AdjustEvent(eventToken);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            ae.addCallbackParameter(entry.getKey(), entry.getValue().toString());
        }
        if(map.get(SdkAdsConstant.GAMA_EVENT_PAY_VALUE) != null) {
            double value = (double) map.get(SdkAdsConstant.GAMA_EVENT_PAY_VALUE);
            String currenty = "USD";
//            if(map.get("gama_event_currency") != null) {
//                currenty = (String) map.get("gama_event_currency");
//            }
            ae.setRevenue(value, currenty);
        }
        ae.addCallbackParameter("gameCode", ResConfig.getGameCode(context));
        Adjust.trackEvent(ae);
    }

    /**
     * 用于统计卸载事件，传入fcm的token
     * @param context
     * @param pushToken
     */
    public static void setPushToken(Context context, String pushToken) {
        if(!isAdjustEnable) {
            PL.i(TAG, "adjust disable");
            return;
        }
        Adjust.setPushToken(pushToken, context);
    }
}

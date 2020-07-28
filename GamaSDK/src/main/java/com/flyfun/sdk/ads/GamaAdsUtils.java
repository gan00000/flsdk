package com.flyfun.sdk.ads;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.appsflyer.AppsFlyerLib;
import com.core.base.utils.PL;
import com.flyfun.base.bean.GamaOnlineRequestBean;
import com.flyfun.base.excute.GamaOnlineRequestTask;
import com.flyfun.base.excute.GamaRoleInfoRequestTask;
import com.flyfun.base.utils.GamaUtil;
import com.gama.sdk.R;
import com.flyfun.thirdlib.facebook.SFacebookProxy;
import com.flyfun.thirdlib.google.SGoogleProxy;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GamaAdsUtils {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 计算留存
     */
    public static void caculateRetention(Context context, String userid) {
        long firstLoginDate = GamaUtil.getFirstLoginDate(context, userid);
        PL.i("firstLoginDate : " + firstLoginDate);
        if(firstLoginDate > 0) { //有登入记录
            Date firstDate = new Date(firstLoginDate);
            Date nowDate = new Date(System.currentTimeMillis());
            int bewteen = betweenDate(firstDate, nowDate);
            int[] intArray = context.getResources().getIntArray(R.array.gama_retention_day);
            for(int i : intArray) {
                PL.i("intArray : " + i);
                if(bewteen == i) {
                    String retentions = String.format(SdkAdsConstant.GAMA_RETENTION, i);
                    PL.i("retentions : " + retentions);
                    StarEventLogger.trackingWithEventName((Activity) context, retentions, null, null);
                }
            }
        } else {
            GamaUtil.saveFirstLoginDate(context, userid);
        }
    }

    public static int betweenDate(Date startDate, Date endDate) {
        int between = 0;
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        try {
            Date bDate = format.parse(format.format(startDate));
            Date eDate = format.parse(format.format(endDate));
            between = (int) ((eDate.getTime() - bDate.getTime()) / (1000 * 60 * 60 * 24));
            PL.i("bDate : " + bDate.toString());
            PL.i("eDate : " + eDate.toString());
            PL.i("between : " + between);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return between;
    }

    //上报角色信息
    public static void upLoadRoleInfo(Context context, Map<String, Object> map) {
        GamaRoleInfoRequestTask task = new GamaRoleInfoRequestTask(context, map);
        task.excute();
    }

    public enum GamaOnlineType {
        TYPE_EXIT_GAME,
        TYPE_CHANGE_ROLE
    }

    public static void uploadOnlineTime(Context context, GamaOnlineType type) {
        String onlineTimeInfo = GamaUtil.getOnlineTimeInfo(context);

        if(TextUtils.isEmpty(onlineTimeInfo)) {
            PL.i("没有用户在线时长信息，记录登入时间戳");
            GamaUtil.saveOnlineTimeInfo(context, System.currentTimeMillis());
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(onlineTimeInfo);

            long startTime = jsonObject.optLong("timestamp");
            long currentTime = System.currentTimeMillis();

            if(startTime == 0) {
                PL.i("新登入用户，记录登入时间戳");
                GamaUtil.saveOnlineTimeInfo(context, currentTime);
                return;
            }

            long lastTime = currentTime - startTime;
            if (lastTime > 60 * 1000) {
                PL.i("需要上传在线时长");
                String userId = jsonObject.optString(GamaUtil.GAMA_LOGIN_USER_ID);
                String roleId = jsonObject.optString(GamaUtil.GAMA_LOGIN_ROLE_ID);
                String serverCode = jsonObject.optString(GamaUtil.GAMA_LOGIN_ROLE_SERVER_CODE);
                String roleLevel = jsonObject.optString(GamaUtil.GAMA_LOGIN_ROLE_LEVEL);
                String roleVip = jsonObject.getString(GamaUtil.GAMA_LOGIN_ROLE_VIP);
                String serverName = jsonObject.getString(GamaUtil.GAMA_LOGIN_ROLE_VIP);
                String roleName = jsonObject.getString(GamaUtil.GAMA_LOGIN_ROLE_VIP);

                GamaOnlineRequestBean bean = new GamaOnlineRequestBean(context);
                bean.setUserId(userId);
                bean.setServerCode(serverCode);
                bean.setRoleId(roleId);
                bean.setRoleLevel(roleLevel);
                bean.setStartTime(startTime + "");
                bean.setEndTime(currentTime + "");

                GamaOnlineRequestTask task = new GamaOnlineRequestTask(context, bean);
                task.excute();

                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put(SdkAdsConstant.GAMA_EVENT_ROLEID, roleId);
                map.put(SdkAdsConstant.GAMA_EVENT_ROLENAME, roleName);
                map.put(SdkAdsConstant.GAMA_EVENT_ROLE_LEVEL, roleLevel);
                map.put(SdkAdsConstant.GAMA_EVENT_ROLE_VIP_LEVEL, roleVip);
                map.put(SdkAdsConstant.GAMA_EVENT_SERVERCODE, serverCode);
                map.put(SdkAdsConstant.GAMA_EVENT_SERVERNAME, serverName);
                map.put(SdkAdsConstant.GAMA_EVENT_USER_ID, userId);
                map.put(SdkAdsConstant.GAMA_EVENT_ONLINE_TIME, lastTime);

                //facebook和firebase的属性列表
                Bundle b = new Bundle();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    b.putString(entry.getKey(), entry.getValue().toString());
                }

                //Facebook上报
                SFacebookProxy.trackingEvent(context, SdkAdsConstant.GAMA_EVENT_ONLINE_TIME, (double) lastTime, b);

                //firebase上报,
                SGoogleProxy.firebaseAnalytics(context, SdkAdsConstant.GAMA_EVENT_ONLINE_TIME, b);

                //AppsFlyer上报
                AppsFlyerLib.getInstance().trackEvent(context.getApplicationContext(), SdkAdsConstant.GAMA_EVENT_ONLINE_TIME, map);

            } else {
                PL.i("不需要上传在线时长");
            }

            switch (type) {
                case TYPE_CHANGE_ROLE:
                    //用户切换角色，更新开始时间
                    GamaUtil.saveOnlineTimeInfo(context, System.currentTimeMillis());
                    break;

                case TYPE_EXIT_GAME:
                    //用户离开游戏，重置在线时长
                    GamaUtil.resetOnlineTimeInfo(context);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}

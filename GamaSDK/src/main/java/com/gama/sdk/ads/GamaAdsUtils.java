package com.gama.sdk.ads;

import android.app.Activity;
import android.content.Context;

import com.core.base.utils.PL;
import com.gama.base.excute.GamaRoleInfoRequestTask;
import com.gama.base.utils.GamaUtil;
import com.gama.sdk.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
                    String retentions = String.format(GamaAdsConstant.GAMA_RETENTION, i);
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
}

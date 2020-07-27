package com.gamamobi.ads.plug.aj.utils;

import android.content.Context;
import android.text.TextUtils;

import com.core.base.utils.FileUtil;
import com.core.base.utils.SPUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GamaAjHelper {

    public static String getEventToken(Context context, String event) {
        if (TextUtils.isEmpty(event)) {
            return null;
        }
        String token = null;
        String adList = GamaAjHelper.getGamaAjList(context);
        if(TextUtils.isEmpty(adList)) {
            adList = FileUtil.readAssetsTxtFile(context, "flsdk/adlist");
        }
        if (!adList.isEmpty() && adList.contains(event)) {
            try {
                JSONObject eventList = new JSONObject(adList);
                JSONArray ja = eventList.getJSONArray("events");
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject o = ja.getJSONObject(i);
                    if(event.equals(o.optString("name"))) {
                        token = o.optString("token");
                        return token;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static final String GAMA_AJ_FILE = "gama_aj_file";
    private static final String GAMA_AJ_LIST = "gama_aj_list";
    public static void saveGamaAjList(Context context, String ajList) {
        SPUtil.saveSimpleInfo(context, GAMA_AJ_FILE, GAMA_AJ_LIST, ajList);
    }
    public static String getGamaAjList(Context context) {
        return SPUtil.getSimpleString(context, GAMA_AJ_FILE, GAMA_AJ_LIST);
    }
}

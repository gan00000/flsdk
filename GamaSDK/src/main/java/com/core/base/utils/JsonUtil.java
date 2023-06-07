package com.core.base.utils;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class JsonUtil {

    public static JSONArray getArrayObjByKey(String jsonStr, String key) {

        if (!TextUtils.isEmpty(jsonStr)) {
            try {
                JSONObject jsonObject = new JSONObject(jsonStr);
                if (jsonObject.has(key)) {
                    return jsonObject.optJSONArray(key);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getValueByKey(Context context, String jsonStr, String key) {
        return  getValueByKey(context, jsonStr, key,"");
    }


    public static String getValueByKey(Context context, String jsonStr, String key, String defaultValue) {

        if (!TextUtils.isEmpty(jsonStr)) {
            try {
                JSONObject jsonObject = new JSONObject(jsonStr);
                if (jsonObject.has(key)) {
                    String value = jsonObject.optString(key, defaultValue);
                    if (!TextUtils.isEmpty(value)) {
                        return value;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

    public static String setValueByKey(Context context, String jsonStr, String key, String newValue) {

        if (!TextUtils.isEmpty(jsonStr)) {
            try {
                JSONObject jsonObject = new JSONObject(jsonStr);
                jsonObject.put(key, newValue);
                return jsonObject.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        } else {
            return "";
        }
    }

    public static boolean isJson(String str) {
        try {
            if (TextUtils.isEmpty(str))
                return false;
            JSONObject j = new JSONObject(str);
            return true;
        } catch (JSONException e) {
            //e.printStackTrace();
            PL.i("str is not json");
        }
        return false;
    }

    public static String map2jsonString(Map<String, String> map) throws JSONException {
        if (map == null || map.isEmpty()) {
            return "";
        }
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!TextUtils.isEmpty(entry.getKey())) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }
        }

        return jsonObject.toString();
    }

    public static JSONObject map2Json(Map<String, Object> map) {

        JSONObject jsonObject = new JSONObject();
        if (map == null || map.isEmpty()) {
            return jsonObject;
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!TextUtils.isEmpty(entry.getKey())) {
                try {
                    String key = entry.getKey();
                    Object vObj = entry.getValue();
                    jsonObject.put(key, vObj);
                } catch (JSONException e) {

                }
//                String value_str = "";
//                if (vObj instanceof Integer){
//                    jsonObject.put(key, vObj);
//                }
//                else if (vObj instanceof Long){
//                    jsonObject.put(key, vObj);
//                }
//                else if (vObj instanceof Boolean){
//                    jsonObject.put(key, vObj);
//                }
//                else if (vObj instanceof Float){
//                    jsonObject.put(key, vObj);
//                }
//                else if (vObj instanceof String){
//                    jsonObject.put(key, vObj);
//                }
//                else{
//
//                }
            }
        }

        return jsonObject;
    }
}

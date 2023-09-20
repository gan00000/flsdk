package com.core.base.bean;

import android.content.Context;
import androidx.annotation.NonNull;

import com.appsflyer.AppsFlyerLib;
import com.core.base.utils.ApkInfoUtil;
import com.core.base.utils.SStringUtil;
import com.mw.sdk.utils.SdkUtil;
import com.mw.sdk.BuildConfig;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用反射进行拼接post请求，不能进行代码混淆
 * Created by gan on 2016/11/24.
 */

public class BaseReqeustBean extends AbsReqeustBean {

    private String androidId = "";
    private String imei = "";//因Google警告，不再获取imei信息
    private String systemVersion = "";
    private String osVersion = "";
    private String deviceType = "";
    private String mac = "";//因Google警告，不再获取mac信息
    private String osLanguage = "";//系统语言


    private String packageName = "";
    private String versionCode = "";
    private String versionName = "";
    private String appsflyerId = "";

    private String sdkJarVersion = "";

    public String getAndroidId() {
        return androidId;
    }

    public String getImei() {
        return imei;
    }

    public String getSystemVersion() {
        return systemVersion;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getMac() {
        return mac;
    }

    public String getOsLanguage() {
        return osLanguage;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getAppsflyerId() {
        return appsflyerId;
    }

    public void setAppsflyerId(String appsflyerId) {
        this.appsflyerId = appsflyerId;
    }

    public BaseReqeustBean() {}

    public BaseReqeustBean(Context context) {

        systemVersion = ApkInfoUtil.getOsVersion();
        osVersion = systemVersion;
        deviceType = ApkInfoUtil.getDeviceType();
        androidId = ApkInfoUtil.getAndroidId(context);
        osLanguage = ApkInfoUtil.getOsLanguage();

        if (context != null) {
            imei = "";
            mac = "";

            packageName = context.getPackageName();
            versionCode = ApkInfoUtil.getVersionCode(context);
            versionName = ApkInfoUtil.getVersionName(context);
            appsflyerId = AppsFlyerLib.getInstance().getAppsFlyerUID(context);

            sdkJarVersion = SdkUtil.getSdkInnerVersion(context) + "_" + BuildConfig.JAR_VERSION;
        }
    }

    /**
     * 将请求的Bean转换成Map
     */
    public Map<String, String> fieldValueToMap() {

        //如果主动设置此值，不再变量字段
        if (getRequestParamsMap() != null && !getRequestParamsMap().isEmpty()){
            return getRequestParamsMap();
        }

        Map<String, String> postParams = new HashMap<String, String>();
        Class c = this.getClass();
        while (c != null && c != BaseReqeustBean.class.getSuperclass()) {

            Field[] fields = c.getDeclaredFields();
            Field.setAccessible(fields, true);

            for (int i = 0; i < fields.length; i++) {
                try {
                    Object value = null;
                    if (fields[i].get(this) != null) {
                        value = fields[i].get(this);
                        if (fields[i].getType() == String.class) {
                            postParams.put(fields[i].getName(), (String) value);
                        } else if (fields[i].getType() == int.class) {
                            postParams.put(fields[i].getName(), String.valueOf((int) value));
                        }

                    }

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            c = c.getSuperclass();
        }
        return postParams;
    }

    public String fieldValueToString() {
        return SStringUtil.map2strData(fieldValueToMap());
    }


    public String createPreRequestUrl(){
        String s = getCompleteUrl();
        return getStyeUrl(s);
    }

    @NonNull
    private String getStyeUrl(String s) {
        if (SStringUtil.isNotEmpty(s)){
            String fieldParams = fieldValueToString();
            if (SStringUtil.isEmpty(fieldParams)){
                return s;
            }
            if (s.endsWith("?")){
                return s + fieldParams;
            }
            if (s.contains("?")){//已经拼接了部分参数
                return s + "&" + fieldParams;
            }
            return s + "?" + fieldParams;
        }
        return "";
    }

    public String createSpaRequestUrl(){
        String s = getCompleteSpaUrl();
        return getStyeUrl(s);
    }

    public String[] createRequestUrls(){
        String[] m = new String[2];
        m[0] = createPreRequestUrl();
        m[1] = createSpaRequestUrl();
        return m;
    }

}

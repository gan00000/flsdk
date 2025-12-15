package com.mw.sdk.utils;

import android.content.Context;

import com.core.base.utils.FileUtil;
import com.core.base.utils.PL;
import com.core.base.utils.ResUtil;
import com.core.base.utils.SStringUtil;
import com.mw.sdk.R;
import com.mw.sdk.constant.SGameLanguage;

import java.util.Properties;

public class ResConfig {

    //===========================================参数配置start===============================================
    //===========================================每個遊戲每個渠道都可能不一樣=======================================
    //===========================================参数配置start================================================

    public static String getChannelPlatform(Context context) {
       return context.getResources().getString(R.string.channel_platform);
    }

    /**
     * 获取gameCode
     */
    public static String getGameCode(Context context) {

        String mmValue = context.getResources().getString(R.string.sdk_game_code);
        if (SStringUtil.isNotEmpty(mmValue)){
            return mmValue;
        }
        return getConfigInAssetsProperties(context, "sdk_game_code");
    }

    public static boolean isMoreLanguage(Context context) {

        String mmValue = context.getResources().getString(R.string.sdk_more_language);
        if (SStringUtil.isNotEmpty(mmValue)){
            return SStringUtil.isEqual("true",mmValue);
        }
        String more = getConfigInAssetsProperties(context, "sdk_more_language");
        return SStringUtil.isEqual("true",more);
    }
    public static boolean isShowReferCode(Context context) {

        String mmValue = context.getResources().getString(R.string.mw_need_refer_code);
        if (SStringUtil.isNotEmpty(mmValue)){
            return SStringUtil.isEqual("true",mmValue);
        }
        return false;
    }

    public static String getDefaultServerLanguage(Context context) {
        String mmValue = context.getResources().getString(R.string.sdk_default_server_language);
        if (SStringUtil.isNotEmpty(mmValue)){
            return mmValue;
        }
        return getConfigInAssetsProperties(context, "sdk_default_server_language");
    }

    /**
     * 获取秘钥
     */
    public static String getAppKey(Context context) {
        String mmValue = context.getResources().getString(R.string.sdk_app_key);
        if (SStringUtil.isNotEmpty(mmValue)){
            return mmValue;
        }
        return getConfigInAssetsProperties(context, "sdk_app_key");
    }

    public static String getAfDevKey(Context context) {
        String mmValue = context.getResources().getString(R.string.sdk_appflyer_dev_key);
        if (SStringUtil.isNotEmpty(mmValue)){
            return mmValue;
        }
        return getConfigInAssetsProperties(context, "sdk_ads_appflyer_dev_key");
    }

    /**
     * 获取Google Client Id用于G+登录验证
     */
    public static String getGoogleClientId(Context context) {
        return ResUtil.findStringByName(context,"default_web_client_id");
    }


    //===========================================参数配置end===============================================
    //===========================================参数配置end===============================================
    //===========================================参数配置end===============================================


    /**
     * 添加地区前缀
     *
     * @param name
     * @return
     */
//    public static String getLocalSchemaName(Context context, String name) {
//        String localName;
//
//        SGameLanguage sGameLanguage = Localization.getSGameLanguage(context);
//        localName = sGameLanguage.getPrefixName() + "_" + name;
//        PL.i("local prefix : " + localName);
//        return localName;
//    }

    //===========================================域名获取start===============================================
    //=========================================== 根据地区改变，同一地区的游戏不变===================================
    //===========================================域名获取start===============================================

    /**
     * 获取登录主域名地址
     */
    public static String getLoginPreferredUrl(Context context) {
//        if (GamaUtil.getSdkCfg(context) != null && !TextUtils.isEmpty(GamaUtil.getSdkCfg(context).getS_Login_Pre_Url())) {
//            return GamaUtil.getSdkCfg(context).getS_Login_Pre_Url();
//        }
        return getConfigUrl(context, R.string.mw_sdk_login_pre_url);
    }

    /**
     * 获取登录备用域名地址
     */
    public static String getLoginSpareUrl(Context context) {
        return getConfigUrl(context, R.string.mw_sdk_login_spa_url);
    }

    /**
     * 获取储值主域名
     */
    public static String getPayPreferredUrl(Context context) {
        return getConfigUrl(context, R.string.mw_sdk_pay_pre_url);
    }

    /**
     * 获取储值备用域名
     */
    public static String getPaySpareUrl(Context context) {
        return getConfigUrl(context, R.string.mw_sdk_pay_spa_url);
    }


    /**
     * 获取CDN动态域名的主域名
     */
    public static String getCdnPreferredUrl(Context context) {
        return getConfigUrl(context, R.string.mw_sdk_cdn_pre_url);
    }

    /**
     * 获取CDN动态域名的备用域名
     */
    public static String getCdnSpareUrl(Context context) {
        return getConfigUrl(context, R.string.mw_sdk_cdn_spa_url);
    }

    /**
     * 获取服务条款链接
     */
    public static String getServiceTermUrl(Context context) {
        return getConfigUrl(context, R.string.mw_sdk_terms_service_url);
    }


    public static String getLogPreferredUrl(Context context) {
        return getConfigUrl(context, R.string.mw_sdk_log_pre_url);
    }

    public static String getMemberPreferredUrl(Context context) {
        return getConfigUrl(context, R.string.mw_sdk_member_pre_url);
    }
    public static String getPlatPreferredUrl(Context context) {
        return getConfigUrl(context, R.string.mw_sdk_plat_pre_url);
    }

    /**
     * <p>Description: 先获取动态域名，然后获取本地域名，配置文件的key和本地的xml key需要一致</p>
     */
//    public static String getCdnLocalUrl(Context context, String resName) {
//
//        String url = SdkUtil.getCfgValueByKey(context, resName, "");
//        if (SStringUtil.isEmpty(url)) {
//            url = getConfigUrl(context, resName);
//        }
//        return url;
//    }



//===========================================域名获取end===============================================	
//===========================================域名获取end===============================================	
//===========================================域名获取end===============================================	


    private static String getConfigUrl(Context context, int id_url) {

//        String isGlobal = getConfigInAssetsProperties(context, "gama_url_is_global");
//        if ("100".equals(isGlobal)) {
//            xmlSchemaName = "g_" + xmlSchemaName;
//        }

//        String localSchemaName = getLocalSchemaName(context, xmlSchemaName);
//
//        String url = getResStringByName(context, localSchemaName);

//		if (TextUtils.isEmpty(url)) {
//			return "";
//		}
//		if (url.contains(".com") || url.contains("http") || url.contains("//")) {
//			return url;
//		}
        return context.getResources().getString(id_url);
    }

    private static String getResStringByName(Context context, String xmlSchemaName) {

        String xmlSchemaContent = "";
        try {
            xmlSchemaContent = ResUtil.findStringByName(context, xmlSchemaName);
        } catch (Exception e) {
            PL.w("String not find:" + xmlSchemaName);
            e.printStackTrace();
            return "";
        }
        return xmlSchemaContent;
    }

    private static Properties properties;

    /**
     * 从assets/gama/gama_gameconfig.properties中获取游戏配置信息
     */
    private static String getConfigInAssetsProperties(Context context, String key) {

        if (properties == null) {
            properties = FileUtil.readAssetsPropertiestFile(context, "mwsdk/gameconfig.properties");
            PL.i("获取游戏assets配置文件: " + (properties != null ? properties.toString() : "失败"));
        }
        if (properties == null) {
//            PL.e("获取游戏assets配置文件失败");
            return "";
        }
        return properties.getProperty(key, "");
    }

}

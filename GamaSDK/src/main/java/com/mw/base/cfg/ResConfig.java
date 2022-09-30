package com.mw.base.cfg;

import android.content.Context;
import android.text.TextUtils;

import com.core.base.utils.FileUtil;
import com.core.base.utils.JsonUtil;
import com.core.base.utils.PL;
import com.core.base.utils.ResUtil;
import com.core.base.utils.SPUtil;
import com.core.base.utils.SStringUtil;
import com.mw.base.bean.SGameLanguage;
import com.mw.base.utils.SdkUtil;
import com.mw.base.utils.Localization;

import java.util.Properties;

public class ResConfig {
    private static final String TAG = ResConfig.class.getSimpleName();

    //===========================================参数配置start===============================================
    //===========================================每個遊戲每個渠道都可能不一樣=======================================
    //===========================================参数配置start================================================

    /**
     * 获取gameCode
     */
    public static String getGameCode(Context context) {
//        if (GamaUtil.getSdkCfg(context) != null && !TextUtils.isEmpty(GamaUtil.getSdkCfg(context).getS_GameCode())) {
//            return GamaUtil.getSdkCfg(context).getS_GameCode();
//        }
//		return getResStringByName(context, "star_game_code");
        return getConfigInAssetsProperties(context, "sdk_game_code");
    }

    public static boolean isMoreLanguage(Context context) {
        String more = getConfigInAssetsProperties(context, "sdk_more_language");
        return SStringUtil.isEqual("true",more);
    }

    /**
     * 获取秘钥
     */
    public static String getAppKey(Context context) {
//        if (GamaUtil.getSdkCfg(context) != null && !TextUtils.isEmpty(GamaUtil.getSdkCfg(context).getS_AppKey())) {
//            return GamaUtil.getSdkCfg(context).getS_AppKey();
//        }
        return getConfigInAssetsProperties(context, "sdk_app_key");
    }

    public static String getApplicationId(Context context) {
        return getConfigInAssetsProperties(context, "facebook_app_id");
    }


//    public static String getGameLanguage(Context context) {
//        String language = SPUtil.getSimpleString(context, SdkUtil.SDK_SP_FILE, SdkUtil.SDK_GAME_LANGUAGE);
//        if (TextUtils.isEmpty(language)) {
//            language = SGameLanguage.zh_TW.getLanguage();
//        }
//        return language;
//    }

//    public static void saveGameLanguage(Context context, String language) {
//        if (TextUtils.isEmpty(language)) {
//            return;
//        }
//        SPUtil.saveSimpleInfo(context, SdkUtil.SDK_SP_FILE, SdkUtil.SDK_GAME_LANGUAGE, language);
//    }
//
//    public static String getGameLanguageLower(Context context) {
//        return getGameLanguage(context).toLowerCase();
//    }

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
    public static String getLocalSchemaName(Context context, String name) {
        String localName;

        SGameLanguage sGameLanguage = Localization.getSGameLanguage(context);
        localName = sGameLanguage.getPrefixName() + "_" + name;
        PL.i("local prefix : " + localName);
        return localName;
    }

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
        return getConfigUrl(context, "sdk_login_pre_url");
    }

    /**
     * 获取登录备用域名地址
     */
    public static String getLoginSpareUrl(Context context) {
//        if (GamaUtil.getSdkCfg(context) != null && !TextUtils.isEmpty(GamaUtil.getSdkCfg(context).getS_Login_Spa_Url())) {
//            return GamaUtil.getSdkCfg(context).getS_Login_Spa_Url();
//        }
        return getConfigUrl(context, "sdk_login_spa_url");
    }

    /**
     * 获取储值主域名
     */
    public static String getPayPreferredUrl(Context context) {
//        if (GamaUtil.getSdkCfg(context) != null && !TextUtils.isEmpty(GamaUtil.getSdkCfg(context).getS_Pay_Pre_Url())) {
//            return GamaUtil.getSdkCfg(context).getS_Pay_Pre_Url();
//        }
        return getConfigUrl(context, "sdk_pay_pre_url");
    }

    /**
     * 获取储值备用域名
     */
    public static String getPaySpareUrl(Context context) {
//        if (GamaUtil.getSdkCfg(context) != null && !TextUtils.isEmpty(GamaUtil.getSdkCfg(context).getS_Pay_Spa_Url())) {
//            return GamaUtil.getSdkCfg(context).getS_Pay_Spa_Url();
//        }
        return getConfigUrl(context, "sdk_pay_spa_url");
    }

    /**
     * 获取客服主域名
     */
    public static String getCsPreferredUrl(Context context) {
        return getCdnLocalUrl(context, "sdk_cs_pre_url");
    }

    /**
     * 获取客服备用域名
     */
    public static String getCsSpareUrl(Context context) {
        return getCdnLocalUrl(context, "sdk_cs_spa_url");
    }

    /**
     * 获取活动主域名
     */
    public static String getActivityPreferredUrl(Context context) {
//        if (GamaUtil.getSdkCfg(context) != null && !TextUtils.isEmpty(GamaUtil.getSdkCfg(context).getS_Act_Pre_Url())) {
//            return GamaUtil.getSdkCfg(context).getS_Act_Pre_Url();
//        }
        return getConfigUrl(context, "sdk_act_pre_url");
    }

    /**
     * 获取活动备用域名
     */
    public static String getActivitySpareUrl(Context context) {
//        if (GamaUtil.getSdkCfg(context) != null && !TextUtils.isEmpty(GamaUtil.getSdkCfg(context).getS_Act_Spa_Url())) {
//            return GamaUtil.getSdkCfg(context).getS_Act_Spa_Url();
//        }
        return getConfigUrl(context, "sdk_act_spa_url");
    }

    /**
     * 获取CDN动态域名的主域名
     */
    public static String getCdnPreferredUrl(Context context) {
        return getConfigUrl(context, "sdk_cdn_pre_url");
    }

    /**
     * 获取CDN动态域名的备用域名
     */
    public static String getCdnSpareUrl(Context context) {
        return getConfigUrl(context, "sdk_cdn_spa_url");
    }

    /**
     * 获取服务条款链接
     */
    public static String getServiceTermUrl(Context context) {
        return getConfigUrl(context, "sdk_terms_service_url");
    }

    /**
     * 是否侵权
     */
    public static boolean isInfringement(Context context) {
        return SStringUtil.isEqual(getConfigInAssetsProperties(context, "sdk_infringement"), "true");
    }

    /**
     * 获取第三方支付的接口名
     */
    public static String getPayThirdMethod(Context context) {
        return getResStringByName(context, "sdk_third_method");
    }


    /**
     * 获取广告主域名
     */
    public static String getAdsPreferredUrl(Context context) {
        return getCdnLocalUrl(context, "gama_ads_pre_url");
    }

    /**
     * 获取广告备用域名
     */
    public static String getAdsSpareUrl(Context context) {
        return getCdnLocalUrl(context, "gama_ads_spa_url");
    }

    /**
     * 获取平台主域名
     */
    public static String getPlatPreferredUrl(Context context) {
        return getCdnLocalUrl(context, "gama_plat_pre_url");
    }

    /**
     * 获取平台备用域名
     */
    public static String getPlatSpareUrl(Context context) {
        return getCdnLocalUrl(context, "gama_plat_spa_url");
    }

    public static String getLogPreferredUrl(Context context) {
        return getCdnLocalUrl(context, "mw_log_pre_url");
    }

    /**
     * <p>Description: 先获取动态域名，然后获取本地域名，配置文件的key和本地的xml key需要一致</p>
     */
    public static String getCdnLocalUrl(Context context, String resName) {

        String url = SdkUtil.getCfgValueByKey(context, resName, "");
        if (SStringUtil.isEmpty(url)) {
            url = getConfigUrl(context, resName);
        }
        return url;
    }

    /**
     * 获取客服链接
     * @param context
     * @return
     */
    public static String getServiceUrl(Context context) {
        return getConfigUrl(context, "sdk_service_url");
    }

    /**
     * 获取区码的链接
     */
    public static String getAreaCodeUrl(Context context) {
        return getConfigUrl(context, "sdk_area_info_url");
    }

    /**
     * 获取验证码开关的链接
     */
    public static String getVfCodeSwitchUrl(Context context) {
        return getConfigUrl(context, "sdk_vfcode_switch_url");
    }


//===========================================域名获取end===============================================	
//===========================================域名获取end===============================================	
//===========================================域名获取end===============================================	


    private static String getConfigUrl(Context context, String xmlSchemaName) {

//        String isGlobal = getConfigInAssetsProperties(context, "gama_url_is_global");
//        if ("100".equals(isGlobal)) {
//            xmlSchemaName = "g_" + xmlSchemaName;
//        }

        String localSchemaName = getLocalSchemaName(context, xmlSchemaName);

        String url = getResStringByName(context, localSchemaName);

//		if (TextUtils.isEmpty(url)) {
//			return "";
//		}
//		if (url.contains(".com") || url.contains("http") || url.contains("//")) {
//			return url;
//		}
        return url;
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

    private static String gameConfig = "";

    public static String getConfigInAssets(Context context, String key) {

        if (SStringUtil.isEmpty(gameConfig)) {
            gameConfig = FileUtil.readAssetsTxtFile(context, "mwsdk/gama_game_config");
            PL.i("gameConfig:" + gameConfig);
        }
        String mVaule = "";
        if (SStringUtil.isNotEmpty(gameConfig)) {
            mVaule = JsonUtil.getValueByKey(context, gameConfig, key, "");
        }
        if (SStringUtil.isEmpty(mVaule)) {
            mVaule = getResStringByName(context, key);
        }
        return mVaule;
    }


    private static Properties properties;

    /**
     * 从assets/gama/gama_gameconfig.properties中获取游戏配置信息
     */
    public static String getConfigInAssetsProperties(Context context, String key) {

        if (properties == null) {
            properties = FileUtil.readAssetsPropertiestFile(context, "mwsdk/gameconfig.properties");
            PL.i(TAG, "获取游戏assets配置文件: " + (properties != null ? properties.toString() : "失败"));
        }
        if (properties == null) {
            PL.e(TAG, "获取游戏assets配置文件失败");
            return "";
        }
        return properties.getProperty(key, "");
    }

    public static String getStringWithLocal(Context context, String xmlSchemaName) {
        String localSchemaName = getLocalSchemaName(context, xmlSchemaName);
        return getResStringByName(context, localSchemaName);
    }
}

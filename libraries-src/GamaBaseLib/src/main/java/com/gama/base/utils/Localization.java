package com.gama.base.utils;

import android.content.Context;

import com.core.base.utils.ApkInfoUtil;
import com.core.base.utils.FileUtil;
import com.gama.base.bean.SGameLanguage;
import com.gama.base.cfg.ResConfig;

/**
 * Created by gan on 2017/4/10.
 */

public class Localization {

    public static void gameLanguage(Context context, SGameLanguage gameLanguage){
        if (gameLanguage == null){
            gameLanguage = SGameLanguage.DEFAULT;
        }

        String locale;
        if (gameLanguage == SGameLanguage.DEFAULT) {  //如果是default表示使用系统语言标识
            String language = ApkInfoUtil.getLocaleLanguage();
            if (isSupportLanguage(context, language)) { //如果在支持的语言列表中，就拼上国家区域
                locale = language + "_" + ApkInfoUtil.getLocaleCountry();
            } else { //不在支持的语言列表中就使用默认（英语）
                locale = SGameLanguage.en_US.getLanguage();
            }
        } else { //否则就使用传入的语言标识
            locale = gameLanguage.getLanguage();
        }
        ResConfig.saveGameLanguage(context, locale);

//        if (!locale.equals(ResConfig.getGameLanguage(context))){//如果语言改变，从新更新服务条款
//            GamaUtil.saveSdkLoginTerms(context,"");
//        }

        //由于语言跟系统显示，不再设定语言标识
        /*if (gameLanguage == SGameLanguage.zh_CH){

            ApkInfoUtil.updateConfigurationLocale(context, Locale.SIMPLIFIED_CHINESE);//简体

        }else if(gameLanguage == SGameLanguage.en_US){

            ApkInfoUtil.updateConfigurationLocale(context, Locale.US);//英文（美国）

        } else if(gameLanguage == SGameLanguage.ko_KR) {
            ApkInfoUtil.updateConfigurationLocale(context, Locale.KOREAN);//韩国
        } else if(gameLanguage == SGameLanguage.ja_JP) {
            ApkInfoUtil.updateConfigurationLocale(context, Locale.JAPANESE);//日本
        }
        else{

            ApkInfoUtil.updateConfigurationLocale(context, Locale.TRADITIONAL_CHINESE);//繁体

        }*/
    }

    /**
     * 只用于内部判断，不可用作请求参数
     * @param context
     * @return
     */
    public static SGameLanguage getSGameLanguage(Context context){
        String language = ResConfig.getGameLanguage(context);
        return SGameLanguage.getLanguageWithString(language);
       /* if (isSupportLanguage(context, language)) {
        } else {
            return SGameLanguage.en_US;//默认设置为繁体中文
        }*/

        /*if (SStringUtil.isEqual(SGameLanguage.en_US.getLanguage(), language)){
            return SGameLanguage.en_US;
        }
        if (SStringUtil.isEqual(SGameLanguage.zh_CH.getLanguage(), language)){
            return SGameLanguage.zh_CH;
        }
        if (SStringUtil.isEqual(SGameLanguage.ko_KR.getLanguage(), language)){
            return SGameLanguage.ko_KR;
        }
        if (SStringUtil.isEqual(SGameLanguage.ja_JP.getLanguage(), language)){
            return SGameLanguage.ja_JP;
        }
        if (SStringUtil.isEqual(SGameLanguage.es_ES.getLanguage(), language)){
            return SGameLanguage.es_ES;
        }
        if (SStringUtil.isEqual(SGameLanguage.pt_PT.getLanguage(), language)){
            return SGameLanguage.pt_PT;
        }
        if (SStringUtil.isEqual(SGameLanguage.it_IT.getLanguage(), language)){
            return SGameLanguage.it_IT;
        }
        if (SStringUtil.isEqual(SGameLanguage.fr_FR.getLanguage(), language)){
            return SGameLanguage.fr_FR;
        }
        if (SStringUtil.isEqual(SGameLanguage.zh_TW.getLanguage(), language)){
            return SGameLanguage.zh_TW;
        }
        if (SStringUtil.isEqual(SGameLanguage.de_DE.getLanguage(), language)){
            return SGameLanguage.de_DE;
        }*/

    }

    @Deprecated
    public static void updateSGameLanguage(Context context){
        //跟系统语言，不用再设置
//        gameLanguage(context,getSGameLanguage(context));
    }

    public static boolean isSupportLanguage(Context context, String language) {
        try {
            String supportLanguage = FileUtil.readAssetsTxtFile(context, "gamesword/supportLanguage");
            String lang = language.split("_")[0];
            return supportLanguage.contains(lang);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断当前是否中文
     * @param context
     * @return
     */
    public static boolean isChineseLanguage(Context context) {
        return SGameLanguage.zh_TW == SGameLanguage.getLanguageWithString(ResConfig.getGameLanguage(context));
    }
}

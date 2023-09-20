package com.mw.sdk.utils;

import android.content.Context;

import com.mw.sdk.constant.SGameLanguage;

/**
 * Created by gan on 2017/4/10.
 */

public class Localization {

    public static void gameLanguage(Context context, SGameLanguage gameLanguage){
//        if (gameLanguage == null){
//            gameLanguage = SGameLanguage.zh_TW;
//        }
//        ResConfig.saveGameLanguage(context,gameLanguage.getLanguage());

//        if (gameLanguage == SGameLanguage.zh_CN){
//
//            ApkInfoUtil.updateConfigurationLocale(context, Locale.SIMPLIFIED_CHINESE);//简体
//
//        }else if(gameLanguage == SGameLanguage.en_US){
//
//            ApkInfoUtil.updateConfigurationLocale(context, Locale.US);//英文（美国）
//
//        } else if(gameLanguage == SGameLanguage.ko_KR) {
//            ApkInfoUtil.updateConfigurationLocale(context, Locale.KOREAN);//韩国
//        } else if(gameLanguage == SGameLanguage.ja_JP) {
//            ApkInfoUtil.updateConfigurationLocale(context, Locale.JAPANESE);//日本
//        }
//        else{
//
//            ApkInfoUtil.updateConfigurationLocale(context, Locale.TRADITIONAL_CHINESE);//繁体
//
//        }
    }

    public static SGameLanguage getSGameLanguage(Context context){
//        String language = ResConfig.getGameLanguage(context);
//        if (SStringUtil.isEqual(SGameLanguage.en_US.getLanguage(), language)){
//            return SGameLanguage.en_US;
//        }
//        if (SStringUtil.isEqual(SGameLanguage.zh_CN.getLanguage(), language)){
//            return SGameLanguage.zh_CN;
//        }
//        if (SStringUtil.isEqual(SGameLanguage.ko_KR.getLanguage(), language)){
//            return SGameLanguage.ko_KR;
//        }
//        if (SStringUtil.isEqual(SGameLanguage.ja_JP.getLanguage(), language)){
//            return SGameLanguage.ja_JP;
//        }
        return SGameLanguage.zh_TW;//默认设置为繁体中文
    }

    public static void updateSGameLanguage(Context context){
//        gameLanguage(context,getSGameLanguage(context));
    }
}

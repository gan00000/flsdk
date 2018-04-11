package com.gama.base.utils;

import android.content.Context;

import com.core.base.utils.ApkInfoUtil;
import com.core.base.utils.SStringUtil;
import com.gama.base.bean.SGameLanguage;
import com.gama.base.cfg.ResConfig;

import java.util.Locale;

/**
 * Created by gan on 2017/4/10.
 */

public class Localization {

    public static void gameLanguage(Context context, SGameLanguage gameLanguage){
        if (gameLanguage == null){
            gameLanguage = SGameLanguage.zh_TW;
        }
        if (!gameLanguage.getLanguage().equals(ResConfig.getGameLanguage(context))){//如果语言改变，从新更新服务条款
            StarPyUtil.saveSdkLoginTerms(context,"");
        }
        ResConfig.saveGameLanguage(context,gameLanguage.getLanguage());

        if (gameLanguage == SGameLanguage.zh_CH){

            ApkInfoUtil.updateConfigurationLocale(context, Locale.SIMPLIFIED_CHINESE);//简体

        }else if(gameLanguage == SGameLanguage.en_US){

            ApkInfoUtil.updateConfigurationLocale(context, Locale.US);//英文（美国）

        }else{

            ApkInfoUtil.updateConfigurationLocale(context, Locale.TRADITIONAL_CHINESE);//繁体

        }
    }

    public static SGameLanguage getSGameLanguage(Context context){
        String language = ResConfig.getGameLanguage(context);
        if (SStringUtil.isEqual(SGameLanguage.en_US.getLanguage(), language)){
            return SGameLanguage.en_US;
        }
        if (SStringUtil.isEqual(SGameLanguage.zh_CH.getLanguage(), language)){
            return SGameLanguage.zh_CH;
        }
        return SGameLanguage.zh_TW;//默认设置为繁体中文
    }

    public static void updateSGameLanguage(Context context){
        gameLanguage(context,getSGameLanguage(context));
    }
}

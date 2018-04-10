package com.starpy.base.bean;

/**
 * Created by gan on 2017/3/27.
 */

public enum SGameLanguage {

    zh_TW("zh-TW"),

    en_US("en-US"),

    zh_CH("zh-CH");

    private String lang;


    SGameLanguage(String language) {
        lang = language;
    }

    public String getLanguage(){
        return lang;
    }
}

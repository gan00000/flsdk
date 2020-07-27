package com.gama.base.bean;

/**
 * Created by gan on 2017/3/27.
 */

public enum SGameLanguage {
    /**
     * 繁中
     */
    zh_TW("zh_TW"),

    /**
     * 英文
     */
    en_US("en_US"),

    /**
     * 韩文
     */
    ko_KR("ko"),

    /**
     * 日文
     */
    ja_JP("ja_JP"),

    /**
     * 简中
     */
    zh_CH("zh_CH");

    private String lang;


    SGameLanguage(String language) {
        lang = language;
    }

    public String getLanguage(){
        return lang;
    }

    public String getPrefixName() {
        return lang.replaceAll("-", "_").toLowerCase();
    }
}

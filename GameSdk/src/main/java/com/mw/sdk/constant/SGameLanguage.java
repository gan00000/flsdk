package com.mw.sdk.constant;

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
    ko_KR("ko_KR"),

    /**
     * 日文
     */
    ja_JP("ja_JP"),

    /**
     * 简中
     */
    zh_CN("zh_CN"),
    ru_RU("ru_RU"),

    /**
     * 越南
     */
    vi_VN("vi_VN");

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

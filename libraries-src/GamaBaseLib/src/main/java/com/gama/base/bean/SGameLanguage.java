package com.gama.base.bean;

/**
 * Created by gan on 2017/3/27.
 */

public enum SGameLanguage {
    /**
     * 系统默认
     */
    DEFAULT("default"),

    /**
     * 繁中
     */
    zh_TW("zh_TW"),

    /**
     * 英文
     */
    en_US("en_US");

//    /**
//     * 韩文
//     */
//    ko_KR("ko"),
//
//    /**
//     * 日文
//     */
//    ja_JP("ja_JP"),
//
//    /**
//     * 意大利文
//     */
//    it_IT("it_IT"),
//
//    /**
//     * 法文
//     */
//    fr_FR("fr_FR"),
//
//    /**
//     * 西班牙文
//     */
//    es_ES("es_ES"),
//
//    /**
//     * 德文
//     */
//    de_DE("de_DE"),
//
//    /**
//     * 葡文
//     */
//    pt_PT("pt_PT");

    private String lang;


    SGameLanguage(String language) {
        lang = language;
    }

    public String getLanguage(){
        return lang;
    }

    public static SGameLanguage getLanguageWithString(String language) {
        try {
            String lang = language.split("_")[0];
            for (SGameLanguage s : SGameLanguage.values()) {
                if (s == DEFAULT) {
                    continue;
                }
                if (s.getLanguage().contains(lang)) {
                    return s;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return en_US;
        }
        return en_US;
    }

//    public String getPrefixName() {
//        return lang.replaceAll("-", "_").toLowerCase();
//    }
}

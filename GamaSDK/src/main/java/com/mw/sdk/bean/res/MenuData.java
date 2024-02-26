package com.mw.sdk.bean.res;

import com.mw.sdk.constant.FloatMenuType;

public class MenuData {

    private String name;
    private String icon;
    private String url;
    private boolean isPage;//是否是网页
    private String code;
    private boolean display;
    private boolean isClick;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isPage() {
        return isPage;
    }

    public void setPage(boolean page) {
        isPage = page;
    }

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }

    public boolean isWithReddot(){
        return FloatMenuType.MENU_TYPE_CS.equals(code);
    }
}

package com.mw.sdk.bean.res;

import com.mw.sdk.constant.FloatMenuType;

public class PayChannelData {

    private int viewType;
    private String name = "";
    private String describe = "";
    private String icon;
    private String toUrl;
    private String code;
    private boolean isClick;
    private boolean showPayGG;
    private boolean showPayRutore;
    private boolean showPayXM;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getToUrl() {
        return toUrl;
    }

    public void setToUrl(String toUrl) {
        this.toUrl = toUrl;
    }

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public boolean isShowPayGG() {
        return showPayGG;
    }

    public void setShowPayGG(boolean showPayGG) {
        this.showPayGG = showPayGG;
    }

    public boolean isShowPayRutore() {
        return showPayRutore;
    }

    public void setShowPayRutore(boolean showPayRutore) {
        this.showPayRutore = showPayRutore;
    }

    public boolean isShowPayXM() {
        return showPayXM;
    }

    public void setShowPayXM(boolean showPayXM) {
        this.showPayXM = showPayXM;
    }
}

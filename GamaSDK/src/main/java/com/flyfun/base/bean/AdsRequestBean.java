package com.flyfun.base.bean;

import android.content.Context;

/**
 * <p>Title: AdsRequestBean</p> <p>Description: 接口请求参数实体</p>
 *
 * @author GanYuanrong
 * @date 2014年8月22日
 */
public class AdsRequestBean extends SSdkBaseRequestBean {

    private String referrerClickTime;
    private String appInstallTime;
    private boolean instantExperienceLaunched;

    public AdsRequestBean(Context context) {
        super(context);
    }

    public String getReferrerClickTime() {
        return referrerClickTime;
    }

    public void setReferrerClickTime(String referrerClickTime) {
        this.referrerClickTime = referrerClickTime;
    }

    public String getAppInstallTime() {
        return appInstallTime;
    }

    public void setAppInstallTime(String appInstallTime) {
        this.appInstallTime = appInstallTime;
    }

    public boolean isInstantExperienceLaunched() {
        return instantExperienceLaunched;
    }

    public void setInstantExperienceLaunched(boolean instantExperienceLaunched) {
        this.instantExperienceLaunched = instantExperienceLaunched;
    }
}

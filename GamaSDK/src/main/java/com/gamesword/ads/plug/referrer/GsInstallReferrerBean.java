package com.gamesword.ads.plug.referrer;

import java.io.Serializable;

public class GsInstallReferrerBean implements Serializable {

    private String referrerUrl;
    private String referrerClickTime;
    private String appInstallTime;
    private boolean instantExperienceLaunched;

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

    public String getReferrerUrl() {
        return referrerUrl;
    }

    public void setReferrerUrl(String referrerUrl) {
        this.referrerUrl = referrerUrl;
    }
}

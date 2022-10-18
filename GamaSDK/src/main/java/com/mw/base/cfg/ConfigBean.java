package com.mw.base.cfg;

import android.content.Context;

import com.core.base.utils.ApkInfoUtil;

import java.util.List;

/**
 * Created by gan on 2017/2/16.
 */

public class ConfigBean {

    private VersionData allVersion;
    private List<VersionData> subVersion;

    private UrlData url;

    public VersionData getAllVersion() {
        return allVersion;
    }

    public UrlData getUrl() {
        return url;
    }

    public VersionData getSdkConfigLoginData(Context context){

        String packageName = context.getPackageName();
        String versionCode = ApkInfoUtil.getVersionCode(context);
        String versionName = ApkInfoUtil.getVersionName(context);

        if (subVersion != null){

            for (int i = 0; i < subVersion.size(); i++) {
                VersionData versionData = subVersion.get(i);

                if (versionData != null && packageName.equals(versionData.getPackageName()) && versionCode.equals(versionData.getVersion())){
                    return versionData;
                }
            }

        }

        if (allVersion != null && packageName.equals(allVersion.getPackageName())){
            return allVersion;
        }

        return null;
    }

    public void setSubVersion(List<VersionData> subVersion) {
        this.subVersion = subVersion;
    }

    public static class VersionData {

        private String version = "";
        private String packageName = "";
        private boolean visitorLogin = true;
        private boolean fbLogin = true;
        private boolean googleLogin = true;
        private boolean lineLogin = true;
//        private boolean appleLogin = true;
        private boolean deleteAccount = false;
        private boolean showContract = true;
        private boolean showLogo = false;
        private boolean showForgetPwd = true;
        private boolean showNotice = false;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public boolean isVisitorLogin() {
            return visitorLogin;
        }

        public void setVisitorLogin(boolean visitorLogin) {
            this.visitorLogin = visitorLogin;
        }

        public boolean isFbLogin() {
            return fbLogin;
        }

        public void setFbLogin(boolean fbLogin) {
            this.fbLogin = fbLogin;
        }

        public boolean isGoogleLogin() {
            return googleLogin;
        }

        public void setGoogleLogin(boolean googleLogin) {
            this.googleLogin = googleLogin;
        }

        public boolean isLineLogin() {
            return lineLogin;
        }

        public void setLineLogin(boolean lineLogin) {
            this.lineLogin = lineLogin;
        }

//        public boolean isAppleLogin() {
//            return appleLogin;
//        }
//
//        public void setAppleLogin(boolean appleLogin) {
//            this.appleLogin = appleLogin;
//        }

        public boolean isDeleteAccount() {
            return deleteAccount;
        }

        public void setDeleteAccount(boolean deleteAccount) {
            this.deleteAccount = deleteAccount;
        }

        public boolean isShowContract() {
            return showContract;
        }

        public void setShowContract(boolean showContract) {
            this.showContract = showContract;
        }

        public boolean isShowLogo() {
            return showLogo;
        }

        public void setShowLogo(boolean showLogo) {
            this.showLogo = showLogo;
        }

        public boolean isShowForgetPwd() {
            return showForgetPwd;
        }

        public void setShowForgetPwd(boolean showForgetPwd) {
            this.showForgetPwd = showForgetPwd;
        }

        public boolean isShowNotice() {
            return showNotice;
        }

        public void setShowNotice(boolean showNotice) {
            this.showNotice = showNotice;
        }

        public boolean isAllThirdLoginTypeHiden(){
            return !visitorLogin && !fbLogin && !googleLogin && !lineLogin;
        }
    }

    public static class UrlData {

        private String noticeUrl = "";//公告地址
        private String agreementUrl = "";//服务条款地址
        private String csUrl = "";//客服地址

        public String getNoticeUrl() {
            return noticeUrl;
        }

        public void setNoticeUrl(String noticeUrl) {
            this.noticeUrl = noticeUrl;
        }

        public String getAgreementUrl() {
            return agreementUrl;
        }

        public void setAgreementUrl(String agreementUrl) {
            this.agreementUrl = agreementUrl;
        }

        public String getCsUrl() {
            return csUrl;
        }

        public void setCsUrl(String csUrl) {
            this.csUrl = csUrl;
        }
    }
}

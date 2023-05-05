package com.mw.base.cfg;

import android.content.Context;

import com.core.base.utils.ApkInfoUtil;
import com.mw.sdk.R;
import com.mw.sdk.constant.SdkInnerVersion;

import java.util.List;

/**
 * Created by gan on 2017/2/16.
 */

public class ConfigBean {

//    private VersionData allVersion;
    private List<VersionData> allVersion;
    private List<VersionData> subVersion;

    private UrlData url;

//    public VersionData getAllVersion() {
//        return allVersion;
//    }

    public UrlData getUrl() {
        return url;
    }

    public VersionData getSdkConfigLoginData(Context context){
        VersionData versionData = getVersionDataData(context);
//        versionData.setVisitorLogin(true);
//        versionData.setFbLogin(true);
//        versionData.setGoogleLogin(true);
//        versionData.setLineLogin(true);
//        versionData.setShowContract(true);
//        versionData.setDeleteAccount(true);
//        versionData.setShowRegPage(false);
//        versionData.setHuaweiData();
        return versionData;
    }

    private VersionData getVersionDataData(Context context){

        String packageName = context.getPackageName();
        String versionCode = ApkInfoUtil.getVersionCode(context);
        String versionName = ApkInfoUtil.getVersionName(context);
        String channel_platform = context.getResources().getString(R.string.channel_platform);

        if (subVersion != null){

            for (int i = 0; i < subVersion.size(); i++) {
                VersionData versionData = subVersion.get(i);

                if (versionData != null && packageName.equals(versionData.getPackageName()) && versionCode.equals(versionData.getVersion())
                    && channel_platform.equals(versionData.getPlatform())){

                    if (channel_platform.equals(SdkInnerVersion.HUAWEI.getSdkVeriosnName())){//设置一下特殊不需要显示的
                        versionData.setHuaweiData(context);
                    }
                    return versionData;
                }
            }

        }

//        if (allVersion != null && packageName.equals(allVersion.getPackageName())){
//            return allVersion;
//        }

        if (allVersion != null){

            for (int i = 0; i < allVersion.size(); i++) {
                VersionData versionData = allVersion.get(i);

                if (versionData != null && packageName.equals(versionData.getPackageName())
                        && channel_platform.equals(versionData.getPlatform())){

                    if (channel_platform.equals(SdkInnerVersion.HUAWEI.getSdkVeriosnName())){//设置一下特殊不需要显示的
                        versionData.setHuaweiData(context);
                    }
                    return versionData;
                }
            }

        }

        VersionData xxVersionData = new VersionData();
        if (channel_platform.equals(SdkInnerVersion.HUAWEI.getSdkVeriosnName())){//设置一下特殊不需要显示的
            xxVersionData.setHuaweiData(context);
        }
        return xxVersionData;
    }


    public static class VersionData {

        private String version = "";
        private String packageName = "";
        private String platform = "";
        private boolean visitorLogin = true;
        private boolean fbLogin = true;
        private boolean googleLogin = true;
        private boolean lineLogin = true;
        private boolean huaweiLogin = true; //华为登录
        private boolean deleteAccount = false;
        private boolean showContract = true;
        private boolean showLogo = false;
        private boolean showForgetPwd = true;
        private boolean showNotice = false;

        private boolean showRegPage = true;

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

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

        public boolean isHuaweiLogin() {
            return huaweiLogin;
        }

        public void setHuaweiLogin(boolean huaweiLogin) {
            this.huaweiLogin = huaweiLogin;
        }

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

        public boolean isShowRegPage() {
            return showRegPage;
        }

        public void setShowRegPage(boolean showRegPage) {
            this.showRegPage = showRegPage;
        }

        public boolean isHiden_Guest_Fb_Gg_Line(){
            return !visitorLogin && !fbLogin && !googleLogin && !lineLogin;
        }

        public boolean isFB_GOGOLE_LINE_LoginTypeHiden(){
            return !fbLogin && !googleLogin && !lineLogin;
        }

        //华为登录，下面的不需要显示
        public VersionData setHuaweiData(Context context){
            this.fbLogin = false;
            this.googleLogin = false;
            this.lineLogin = false;
            this.showContract = false;
            this.deleteAccount = false;
            return this;
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

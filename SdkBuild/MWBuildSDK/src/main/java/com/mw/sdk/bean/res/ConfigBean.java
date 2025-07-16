package com.mw.sdk.bean.res;

import android.content.Context;

import com.core.base.utils.ApkInfoUtil;
import com.mw.sdk.R;
import com.mw.sdk.constant.ChannelPlatform;
import com.mw.sdk.constant.SdkInnerVersion;
import com.mw.sdk.utils.ResConfig;

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
//        versionData.setHuaweiData(context);
//        versionData.setTwitterLogin(true);
//        versionData.setNaverLogin(true);
        return versionData;
    }

    private VersionData getVersionDataData(Context context){

        String packageName = context.getPackageName();
        String versionCode = ApkInfoUtil.getVersionCode(context);
        String versionName = ApkInfoUtil.getVersionName(context);
        String channel_platform = ResConfig.getChannelPlatform(context);

        if (subVersion != null){

            for (int i = 0; i < subVersion.size(); i++) {
                VersionData versionData = subVersion.get(i);

                if (versionData != null && packageName.equals(versionData.getPackageName()) && versionCode.equals(versionData.getVersion())
                    && channel_platform.equals(versionData.getPlatform())){

                    if (channel_platform.equals(ChannelPlatform.HUAWEI.getChannel_platform())){//设置一下特殊不需要显示的
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

                    if (channel_platform.equals(ChannelPlatform.HUAWEI.getChannel_platform())){//设置一下特殊不需要显示的
                        versionData.setHuaweiData(context);
                    }
                    return versionData;
                }
            }

        }

        VersionData xxVersionData = new VersionData();
        if (channel_platform.equals(ChannelPlatform.HUAWEI.getChannel_platform())){//设置一下特殊不需要显示的
            xxVersionData.setHuaweiData(context);
        }
        return xxVersionData;
    }


    public static class VersionData {

        private String version = "";
        private String packageName = "";
        private String platform = "";
        //=============================
        //登录方式start
        //=============================
        private boolean visitorLogin = true;
        private boolean fbLogin = true;
        private boolean googleLogin = true;

        private boolean twitterLogin = false;
        private boolean lineLogin = false;
        private boolean naverLogin = false;
        private boolean huaweiLogin = true; //华为登录，只有华为sdk用

        //=============================
        //登录方式end
        //=============================

        private boolean deleteAccount = false;
        private boolean showContract = true;
        private boolean showLogo = false;
        private boolean showForgetPwd = true;
        private boolean showNotice = false;

        private boolean showRegPage = true;
        //越南调整审核按钮是否显示
        private boolean vnIsReview = true;

        //是否显示登录页面的客服按钮(kr用到)
        private boolean showSdkCsCenter = true;
        private boolean togglePay = false;
        private boolean showMarket = false;//是否展示活动页

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
            return !visitorLogin && !fbLogin && !googleLogin && !lineLogin && !naverLogin && !twitterLogin;
        }

        public boolean isFB_GOGOLE_LINE_LoginTypeHiden(){
            return !fbLogin && !googleLogin && !lineLogin;
        }

        public boolean isVnIsReview() {
            return vnIsReview;
        }

        public void setVnIsReview(boolean vnIsReview) {
            this.vnIsReview = vnIsReview;
        }

        public boolean isTwitterLogin() {
            return twitterLogin;
        }

        public void setTwitterLogin(boolean twitterLogin) {
            this.twitterLogin = twitterLogin;
        }

        public boolean isNaverLogin() {
            return naverLogin;
        }

        public void setNaverLogin(boolean naverLogin) {
            this.naverLogin = naverLogin;
        }

        public boolean isShowSdkCsCenter() {
            return showSdkCsCenter;
        }

        public void setShowSdkCsCenter(boolean showSdkCsCenter) {
            this.showSdkCsCenter = showSdkCsCenter;
        }

        public boolean isTogglePay() {
            return togglePay;
        }

        public void setTogglePay(boolean togglePay) {
            this.togglePay = togglePay;
        }

        public boolean isShowMarket() {
            return showMarket;
        }

        public void setShowMarket(boolean showMarket) {
            this.showMarket = showMarket;
        }

        //华为登录，下面的不需要显示
        public VersionData setHuaweiData(Context context){
//            this.fbLogin = false;
            this.googleLogin = false;
            this.lineLogin = false;
            this.showContract = false;
            this.deleteAccount = false;
            this.twitterLogin = false;
            return this;
        }
    }

    public static class UrlData {

        private String noticeUrl = "";//公告地址
        private String agreementUrl = "";//服务条款地址
        private String csUrl = "";//客服地址
        private String vnReviewUrl = "";//越南审核按钮跳转地址

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

        public String getVnReviewUrl() {
            return vnReviewUrl;
        }

        public void setVnReviewUrl(String vnReviewUrl) {
            this.vnReviewUrl = vnReviewUrl;
        }
    }
}

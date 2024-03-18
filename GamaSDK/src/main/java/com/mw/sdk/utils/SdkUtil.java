package com.mw.sdk.utils;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.core.base.cipher.DESCipher;
import com.core.base.utils.ApkInfoUtil;
import com.core.base.utils.FileUtil;
import com.core.base.utils.JsonUtil;
import com.core.base.utils.PL;
import com.core.base.utils.SPUtil;
import com.core.base.utils.SStringUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mw.sdk.R;
import com.mw.sdk.bean.PhoneInfo;
import com.mw.sdk.bean.SUserInfo;
import com.mw.sdk.bean.res.ConfigBean;
import com.mw.sdk.constant.SGameLanguage;
import com.mw.sdk.constant.SLoginType;
import com.mw.sdk.bean.AccountModel;
import com.mw.sdk.login.model.response.SLoginResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by gan on 2017/2/7.
 */

public class SdkUtil {

    /**
     * 本地SharePreference数据库名
     */
    public static final String SDK_SP_FILE = "sdk_sp_file.xml";

    public static final String SDK_GAME_LANGUAGE = "SDK_GAME_LANGUAGE";

    public static final String SDK_LOGIN_ACCOUNT_INFO = "SDK_LOGIN_ACCOUNT_INFO";//保存用户的账号信息

    public static final String SDK_LOGIN_SERVER_RETURN_DATA = "SDK_LOGIN_SERVER_RETURN_DATA";//保存服务端返回的数据

    public static final String SDK_CFG = "SDK_CFG";//保存sdk配置 cdn文件
    /**
     * 上一次的登录类型
     */
    public static final String SDK_PREVIOUS_LOGIN_TYPE = "SDK_PREVIOUS_LOGIN_TYPE";


    public static void saveSdkCfg(Context context,String cfg){
        SPUtil.saveSimpleInfo(context, SDK_SP_FILE, SDK_CFG,cfg);
    }
    public static String getSdkCfgString(Context context){
        return SPUtil.getSimpleString(context, SDK_SP_FILE, SDK_CFG);
    }


    private static ConfigBean configBean;

    public static ConfigBean getSdkCfg(Context context){
        String cfg = getSdkCfgString(context);
        if (JsonUtil.isJson(cfg)){
            try {
                Gson gson = new Gson();
                configBean = gson.fromJson(cfg, ConfigBean.class);
                return configBean;
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
        return new ConfigBean();
    }

    public static void saveAccountModel(Context context, String account, String password,String userId,  String loginAccessToken,
                                        String loginTimestamp, boolean updateTime){

        //平台注册的默认绑定
        saveAccountModel(context,SLoginType.LOGIN_TYPE_MG,account,password,userId, loginAccessToken,loginTimestamp,"","",updateTime,true);
    }

//    public static void updateAccountModel(Context context, String userId, boolean isBind){
//        List<AccountModel> mls = getAccountModels(context);
//        //account = thirdId;
//        for (AccountModel a: mls) {
//            if (SStringUtil.isNotEmpty(a.getUserId()) && SStringUtil.isEqual(a.getUserId(), userId)){
//                a.setBind(isBind);
//            }
//        }
//        saveAccountModels(context,mls);
//    }

    public static void saveAccountModel(Context context,String loginType, String account, String password,String userId,
                                        String loginAccessToken,
                                        String loginTimestamp,
                                        String thirdId, String thirdAccount, boolean updateTime, boolean isBindAccount){
        /**
         * 1.每种第三方登录方式只保留最后一个
         * 2.如果第三方的账号uid和平台uid相同(绑定的情况下)，保留平台，没有平台账号记录才保存第三方
         */


        if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_MG, loginType)) {//账号登錄
            SdkUtil.removeAccountModelByUserId(context,userId);
//            List<AccountModel> mls = getAccountModels(context);
//            //account = thirdId;
//            for (AccountModel a: mls) {
//                if (SStringUtil.isNotEmpty(a.getUserId()) && SStringUtil.isEqual(a.getUserId(), userId) && a.getAccount().equals(account)){
//                    a.setPassword(password);
//                    a.setBind(isBindAccount);
//                    a.setUserId(userId);
//                    a.setLoginAccessToken(loginAccessToken);
//                    a.setLoginTimestamp(loginTimestamp);
//
//                    if (updateTime){
//                        a.setTime(System.currentTimeMillis());
//                    }
//                    saveAccountModels(context,mls);
//                    return;
//                }
//            }
        }else{//第三方账号
            removeAccountModelLoginType(context,loginType);
            List<AccountModel> mls = getAccountModels(context);
            for (AccountModel tempAccountModel: mls) {

                if (SStringUtil.isNotEmpty(tempAccountModel.getUserId()) && SStringUtil.isEqual(tempAccountModel.getUserId(), userId)){//uid账号已存在

                    if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_MG, tempAccountModel.getLoginType())) {
                        return;
                    }

                    //這裡應該是不會被執行到
                    tempAccountModel.setBind(isBindAccount);
                    tempAccountModel.setUserId(userId);
                    tempAccountModel.setLoginAccessToken(loginAccessToken);
                    tempAccountModel.setLoginTimestamp(loginTimestamp);

                    if (updateTime){
                        tempAccountModel.setTime(System.currentTimeMillis());
                    }
                    saveAccountModels(context,mls);

                    //這裡應該是不會被執行到 end
                    return;
                }
            }
        }

       /* else if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_GUEST, loginType)) {//游客

        } else if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_FB, loginType)) {//fb登錄

        }  else if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_GOOGLE, loginType)) {//gg登錄
        }else if(SStringUtil.isEqual(SLoginType.LOGIN_TYPE_TWITTER, loginType)) {
        }else if(SStringUtil.isEqual(SLoginType.LOGIN_TYPE_LINE, loginType)) {
        } else {
        }*/
        List<AccountModel> mls = getAccountModels(context);
        AccountModel newAccountModel = new AccountModel();//新添加一个保存
        newAccountModel.setAccount(account);
        newAccountModel.setPassword(password);
        newAccountModel.setLoginType(loginType);
        newAccountModel.setUserId(userId);

        newAccountModel.setThirdAccount(thirdAccount == null ? "" : thirdAccount);
        newAccountModel.setThirdId(thirdId);
        newAccountModel.setTime(System.currentTimeMillis());
        newAccountModel.setBind(isBindAccount);

        newAccountModel.setLoginAccessToken(loginAccessToken);
        newAccountModel.setLoginTimestamp(loginTimestamp);

        mls.add(newAccountModel);
        saveAccountModels(context,mls);

    }

   /* public static void removeAccountModelByAccountName(Context context, String account){
        List<AccountModel> mls = getAccountModels(context);
        List<AccountModel> removeModels  = new ArrayList<>();
        for (AccountModel a: mls) {
            if (account.equals(a.getAccount())){
                removeModels.add(a);
            }
        }

        if (!removeModels.isEmpty()){
            mls.removeAll(removeModels);
            saveAccountModels(context,mls);
        }
    }*/

    public static void removeAccountModelByUserId(Context context, String userId){//根据uid删除之前的账号记录
        List<AccountModel> mls = getAccountModels(context);
        List<AccountModel> removeModels  = new ArrayList<>();
        for (AccountModel a: mls) {
            if (userId.equals(a.getUserId())){
                removeModels.add(a);
            }
        }

        if (!removeModels.isEmpty()){
            mls.removeAll(removeModels);
            saveAccountModels(context,mls);
        }
    }

    private static void removeAccountModelLoginType(Context context, String loginType){//根据loginType删除之前的账号记录
        List<AccountModel> mls = getAccountModels(context);
        List<AccountModel> removeModels  = new ArrayList<>();
        for (AccountModel a: mls) {
            if (loginType.equals(a.getLoginType())){
                removeModels.add(a);
            }
        }

        if (!removeModels.isEmpty()){
            mls.removeAll(removeModels);
            saveAccountModels(context,mls);
        }
    }

    /**
     * 保存登入的账号,覆盖所有
     * @param context
     * @param accountModels
     */
    public static void saveAccountModels(Context context, List<AccountModel> accountModels){

        if (accountModels == null){
            return;
        }
        try {
            JSONArray jsonArray = new JSONArray();
            for (AccountModel accountModel: accountModels) {
                JSONObject accountObject = new JSONObject();
                accountObject.put("sdk_account", accountModel.getAccount());
                accountObject.put("sdk_password", accountModel.getPassword());
                accountObject.put("sdk_time", accountModel.getTime());
                accountObject.put("sdk_loginType", accountModel.getLoginType());
                accountObject.put("sdk_thirdId", accountModel.getThirdId());
                accountObject.put("sdk_userId", accountModel.getUserId());
                accountObject.put("sdk_thirdAccount", accountModel.getThirdAccount());
                accountObject.put("sdk_bindAccount", accountModel.isBind());
                accountObject.put("sdk_loginAccessToken", accountModel.getLoginAccessToken());
                accountObject.put("sdk_loginTimestamp", accountModel.getLoginTimestamp());
                jsonArray.put(accountObject);
            }
            SPUtil.saveSimpleInfo(context, SDK_SP_FILE, SDK_LOGIN_ACCOUNT_INFO, encryptText(jsonArray.toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取登入的账号
     * @param context
     * @return
     */
    public static List<AccountModel> getAccountModels(Context context){
        List<AccountModel> accountModels = new ArrayList<>();
        String accountStringInfo = decryptText(SPUtil.getSimpleString(context, SDK_SP_FILE, SDK_LOGIN_ACCOUNT_INFO));
       if (SStringUtil.isEmpty(accountStringInfo)){
           return accountModels;
       }
        try {
            JSONArray accountArray = new JSONArray(accountStringInfo);
            if (accountArray != null){
                for (int i = 0; i < accountArray.length(); i++) {
                    JSONObject accountObject = accountArray.getJSONObject(i);
                    if (accountObject != null){
                        AccountModel accountModel = new AccountModel();
                        accountModel.setAccount(accountObject.optString("sdk_account"));
                        accountModel.setPassword(accountObject.optString("sdk_password"));
                        accountModel.setTime(accountObject.optLong("sdk_time"));
                        accountModel.setLoginType(accountObject.optString("sdk_loginType"));
                        accountModel.setThirdId(accountObject.optString("sdk_thirdId"));
                        accountModel.setUserId(accountObject.optString("sdk_userId"));
                        accountModel.setThirdAccount(accountObject.optString("sdk_thirdAccount"));
                        accountModel.setBind(accountObject.optBoolean("sdk_bindAccount"));
                        accountModel.setLoginAccessToken(accountObject.optString("sdk_loginAccessToken"));
                        accountModel.setLoginTimestamp(accountObject.optString("sdk_loginTimestamp"));

                        accountModels.add(accountModel);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            accountModels.sort(new Comparator<AccountModel>() {
                @Override
                public int compare(AccountModel o1, AccountModel o2) {
                    if (o1.getTime() > o2.getTime()){
                        return -1;
                    }
                    return 1;
                }
            });
        }else{
            Collections.sort(accountModels,new Comparator<AccountModel>() {//排序
                @Override
                public int compare(AccountModel o1, AccountModel o2) {
                    if (o1.getTime() > o2.getTime()){
                        return -1;
                    }
                    return 1;
                }
            });
        }

        return accountModels;
    }

    public static AccountModel getLastLoginAccount(Context context){
        List<AccountModel> accountModels = getAccountModels(context);
        if (accountModels != null && !accountModels.isEmpty()){
            return accountModels.get(0);
        }
        return null;
    }

    /**
     * 保存登入方式
     * @param context
     * @param loginType
     */
    public static void savePreviousLoginType(Context context,String loginType){
        SPUtil.saveSimpleInfo(context, SDK_SP_FILE, SDK_PREVIOUS_LOGIN_TYPE, loginType);
    }

    /**
     * 获取登入方式
     * @param context
     * @return
     */
    public static String getPreviousLoginType(Context context){
        return SPUtil.getSimpleString(context, SDK_SP_FILE, SDK_PREVIOUS_LOGIN_TYPE);
    }

//    private final static String cipherKey = "20170314starpypassword";

    /**
     * 加密后的密码前缀
     */
//    private final static String cipherPasswordFlag = "888*****888";

    /**
     * 加密密码
     */
    private static String encryptText(String str){
        try {
            if (SStringUtil.isNotEmpty(str)){
                return DESCipher.encrypt3DES(str, getSecretKey());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 解密密码
     */
    private static String decryptText(String encryptText){
        try {
            if (SStringUtil.isNotEmpty(encryptText)){
                return DESCipher.decrypt3DES(encryptText, getSecretKey());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptText;
    }

    private static void saveSdkLoginData(Context context,String data){
        if (SStringUtil.isNotEmpty(data)){
            data = encryptText(data);//进行加密后保存
        }
        SPUtil.saveSimpleInfo(context, SDK_SP_FILE, SDK_LOGIN_SERVER_RETURN_DATA,data);
    }

    public static void updateLoginData(Context context, SLoginResponse sLoginResponse){
        if (sLoginResponse != null){
            try {
                sLoginResponse.getData().setGameCode(ResConfig.getGameCode(context));
                Gson gson = new Gson();
                String result = gson.toJson(sLoginResponse);
                saveSdkLoginData(context, result);
            } catch (Exception e) {
                e.printStackTrace();
                saveSdkLoginData(context, sLoginResponse.getRawResponse());
            }
        }
    }

    public static SLoginResponse getCurrentUserLoginResponse(Context context){
        String loginResult = SPUtil.getSimpleString(context, SDK_SP_FILE, SDK_LOGIN_SERVER_RETURN_DATA);
        loginResult = decryptText(loginResult);//进行解密
        if (SStringUtil.isEmpty(loginResult)){
            return null;
        }
        try {
            Gson gson = new Gson();
            return gson.fromJson(loginResult, SLoginResponse.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static boolean isLogin(Context context){
        SLoginResponse sLoginResponse = getCurrentUserLoginResponse(context);
        if (sLoginResponse != null && sLoginResponse.isRequestSuccess() && sLoginResponse.getData()!= null && SStringUtil.isNotEmpty(sLoginResponse.getData().getUserId())){
            return true;
        }
        return false;
    }


    /**
     * 获取当次登入的userId
     */
    public static String getUid(Context context){
        SLoginResponse sLoginResponse = getCurrentUserLoginResponse(context);
        if (sLoginResponse != null && sLoginResponse.isRequestSuccess() && sLoginResponse.getData()!= null && SStringUtil.isNotEmpty(sLoginResponse.getData().getUserId())){
            return sLoginResponse.getData().getUserId();
        }
        return "";
    }

    public static String getSdkTimestamp(Context context){
        SLoginResponse sLoginResponse = getCurrentUserLoginResponse(context);
        if (sLoginResponse != null && sLoginResponse.isRequestSuccess() && sLoginResponse.getData()!= null && SStringUtil.isNotEmpty(sLoginResponse.getData().getTimestamp())){
            return sLoginResponse.getData().getTimestamp();
        }
        return "";
    }

    /**
     * 获取登入的token
     * @param context
     * @return
     */
    public static String getSdkAccessToken(Context context){
        SLoginResponse sLoginResponse = getCurrentUserLoginResponse(context);
        if (sLoginResponse != null && sLoginResponse.isRequestSuccess() && sLoginResponse.getData()!= null && SStringUtil.isNotEmpty(sLoginResponse.getData().getToken())){
            return sLoginResponse.getData().getToken();
        }
        return "";
    }

    /**
     * 获取当次登入的账号是否已经绑定手机
     */
//    public static boolean isAccountLinked(Context context){
//        String beLink = JsonUtil.getValueByKey(context,getSdkLoginData(context), "beLinked", "");
//        return "1".equals(beLink);
//    }

    /**
     * 绑定手机后刷新当前账号绑定状态
     */
    public static void setAccountLinked(Context context){
//        String newSdkLoginData = JsonUtil.setValueByKey(context, getSdkLoginData(context), "beLinked", "1");
//        if(!TextUtils.isEmpty(newSdkLoginData)) {
//            saveSdkLoginData(context, newSdkLoginData);
//        }
    }

    public static final String GAMA_LOGIN_USER_ID = "GAMA_LOGIN_USER_ID";
    public static final String GAMA_LOGIN_ROLE_ID = "GAMA_LOGIN_ROLE_ID";
    public static final String GAMA_LOGIN_ROLE_NAME = "GAMA_LOGIN_ROLE_NAME";
    public static final String GAMA_LOGIN_ROLE_SERVER_CODE = "GAMA_LOGIN_ROLE_SERVER_CODE";
    public static final String GAMA_LOGIN_ROLE_SERVER_NAME = "GAMA_LOGIN_ROLE_SERVER_NAME";
    private static final String GAMA_LOGIN_ROLE_INFO = "GAMA_LOGIN_ROLE_INFO";
    public static final String GAMA_LOGIN_ROLE_LEVEL = "GAMA_LOGIN_ROLE_LEVEL";
    public static final String GAMA_LOGIN_ROLE_VIP = "GAMA_LOGIN_ROLE_VIP";

    /**
     * 获取Json形式保存的角色信息
     */
    public static String getRoleInfo(Context context){
        return SPUtil.getSimpleString(context, SDK_SP_FILE,GAMA_LOGIN_ROLE_INFO);
    }

    /**
     * 以Json形式保存角色信息
     */
    private static void saveRoleInfoJson(Context context,String roleInfo){
        SPUtil.saveSimpleInfo(context, SDK_SP_FILE,GAMA_LOGIN_ROLE_INFO,roleInfo);
    }

    /**
     * 获取角色id
     */
    public static String getRoleId(Context context){
        return JsonUtil.getValueByKey(context,getRoleInfo(context), GAMA_LOGIN_ROLE_ID, "");
    }

    /**
     * 获取角色名
     */
    public static String getRoleName(Context context){
        return JsonUtil.getValueByKey(context,getRoleInfo(context), GAMA_LOGIN_ROLE_NAME, "");
    }

    /**
     * 获取服务器id
     */
    public static String getServerCode(Context context) {
        return JsonUtil.getValueByKey(context,getRoleInfo(context), GAMA_LOGIN_ROLE_SERVER_CODE, "");
    }

    /**
     * 获取服务器名
     */
    public static String getServerName(Context context){
        return JsonUtil.getValueByKey(context,getRoleInfo(context), GAMA_LOGIN_ROLE_SERVER_NAME, "");
    }

    /**
     * 保存角色信息
     * @param roleId 角色id
     * @param roleName 角色名
     * @param roleLevel 角色等级
     * @param vipLevel 角色VIP等级
     * @param severCode 服务器code
     * @param serverName 服务器名
     */
    public static void saveRoleInfo(Context context, String roleId, String roleName, String roleLevel, String vipLevel, String severCode, String serverName) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(GAMA_LOGIN_USER_ID,getUid(context));
            jsonObject.put(GAMA_LOGIN_ROLE_ID,roleId);
            jsonObject.put(GAMA_LOGIN_ROLE_NAME,roleName);
            jsonObject.put(GAMA_LOGIN_ROLE_SERVER_CODE,severCode);
            jsonObject.put(GAMA_LOGIN_ROLE_SERVER_NAME,serverName);
            jsonObject.put(GAMA_LOGIN_ROLE_LEVEL, roleLevel);
            jsonObject.put(GAMA_LOGIN_ROLE_VIP, vipLevel);
            saveRoleInfoJson(context, jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取角色等级
     */
    public static String getRoleLevel(Context context){
        return JsonUtil.getValueByKey(context, getRoleInfo(context), GAMA_LOGIN_ROLE_LEVEL, "");
    }

    /**
     * 获取角色VIP等级
     */
    public static String getRoleVip(Context context){
        return JsonUtil.getValueByKey(context, getRoleInfo(context), GAMA_LOGIN_ROLE_VIP, "");
    }

    public static String getCfgValueByKey(Context context, String key, String defaultValue) {

        String cfg = getSdkCfgString(context);
        return JsonUtil.getValueByKey(context,cfg, key, defaultValue);
    }

    public static boolean checkAccount(String account){
        if (TextUtils.isEmpty(account)){
            return false;
        }
        if (account.contains(" "))
        {
            return false;
        }

//        if (account.contains("@")){//简单检查只是邮箱就行
//            return true;
//        }
        if (account.matches("^.+@\\w+\\..+$")){//简单检查只是邮箱就行
            return true;
        }
        return false;
    }

    public static boolean checkPassword(String password){
        if (TextUtils.isEmpty(password)){
            return false;
        }
        if (password.contains(" "))
        {
            return false;
        }
        if (password.matches("^.{6,20}$")){
            return true;
        }
        return false;
    }

    /**
     * 旧版密码验证，目前北美继续使用
     * @param password
     * @return
     */
//    public static boolean checkPasswordOld(String password){
//        if (TextUtils.isEmpty(password)){
//            return false;
//        }
//        if (password.matches("^[A-Za-z0-9]{6,18}$")){
//            return true;
//        }
//        return false;
//    }

    /**
     * 用于解密动态域名的secretKey
     */
//    private static final String GAMA_DY_ENCRYPT_SECRETKEY = "(starpy99988820170227dyrl)";

    private static String getSecretKey() {
        return getKeyPrefix() + getKeySurfix();
    }

    private static String getKeyPrefix() {
        return "(mwmwmw111888";
    }

    private static String getKeySurfix() {
        int index = 2017 * 10000;
        int sec = index + 227;
        return sec + "mwmw)";
    }

    /**
     * G+登录的三方ID
     */
    private static final String GAMA_LOGIN_GOOGLE_ID = "GAMA_LOGIN_GOOGLE_ID";

    /**
     * 保存G+登录的三方ID
     */
    public static void saveGoogleId(Context context, String googleId){
        SPUtil.saveSimpleInfo(context, SdkUtil.SDK_SP_FILE,GAMA_LOGIN_GOOGLE_ID,googleId);
    }

    /**
     * 获取保存的G+登录的三方ID
     */
    public static String getGoogleId(Context context){
        return SPUtil.getSimpleString(context, SdkUtil.SDK_SP_FILE,GAMA_LOGIN_GOOGLE_ID);
    }

    /**
     * Twitter登录的三方ID
     */
    private static final String GAMA_LOGIN_TWITTER_ID = "GAMA_LOGIN_TWITTER_ID";

    /**
     * 保存Twitter登录的三方ID
     */
    public static void saveTwitterId(Context context, String googleId){
        SPUtil.saveSimpleInfo(context, SdkUtil.SDK_SP_FILE,GAMA_LOGIN_TWITTER_ID,googleId);
    }

    /**
     * 获取保存的Twitter登录的三方ID
     */
    public static String getTwitterId(Context context){
        return SPUtil.getSimpleString(context, SdkUtil.SDK_SP_FILE,GAMA_LOGIN_TWITTER_ID);
    }

//    public static boolean isXM(Context context){
//        return ResConfig.getConfigInAssetsProperties(context,"gama_login_type").equals("100");
//    }
//    public static boolean isMainland(Context context){//100為大陸sdk,其他為海外
//        return ResConfig.getConfigInAssetsProperties(context,"gama_sdk_area").equals("100");
//    }

    private static final String GAMA_GOOGLE_ADVERTISING_ID = "GAMA_GOOGLE_ADVERTISING_ID";
    public static void saveGoogleAdId(Context context, String googleAdId){
        if (googleAdId.contains("00000000-")){
            googleAdId = "";
        }
        PL.i("save google ad id-->" + googleAdId);
        SPUtil.saveSimpleInfo(context, SdkUtil.SDK_SP_FILE,GAMA_GOOGLE_ADVERTISING_ID,googleAdId);
    }
    public static String getGoogleAdId(Context context){
        return SPUtil.getSimpleString(context, SdkUtil.SDK_SP_FILE,GAMA_GOOGLE_ADVERTISING_ID);
    }

    private static final String GAMA_GOOGLE_INSTALL_REFERRER = "GAMA_GOOGLE_INSTALL_REFERRER";
    public static void saveReferrer(Context context, String referrer){
        SPUtil.saveSimpleInfo(context, SdkUtil.SDK_SP_FILE,GAMA_GOOGLE_INSTALL_REFERRER,referrer);
    }
    public static String getReferrer(Context context){
        return SPUtil.getSimpleString(context, SdkUtil.SDK_SP_FILE,GAMA_GOOGLE_INSTALL_REFERRER);
    }
    private static final String GAMA_GOOGLE_TOKEN_ID_STRING = "GAMA_GOOGLE_TOKEN_ID_STRING";
    public static void saveGoogleIdToken(Context context, String idTokenString){
        SPUtil.saveSimpleInfo(context, SdkUtil.SDK_SP_FILE,GAMA_GOOGLE_TOKEN_ID_STRING,idTokenString);
    }
    public static String getGoogleIdToken(Context context){
        return SPUtil.getSimpleString(context, SdkUtil.SDK_SP_FILE,GAMA_GOOGLE_TOKEN_ID_STRING);
    }

    private static final String MW_UNIQUEID = "MW_SDK_UNIQUEID";
    private static void saveUniqueId(Context context, String uniqueId){
        SPUtil.saveSimpleInfo(context, SdkUtil.SDK_SP_FILE,MW_UNIQUEID,uniqueId);
    }
    private static String getUniqueId(Context context){
        return SPUtil.getSimpleString(context, SdkUtil.SDK_SP_FILE,MW_UNIQUEID);
    }

    /**
     * 生成免注册登入账号
     */
    public static  String getSdkUniqueId(Context ctx){

//        1、優先獲取谷歌ID
//        2、獲取不到谷歌ID再獲取安卓ID
//        3、谷歌ID和安卓ID都獲取不到，最後再自己生成一個ID
//        为防止上次獲取谷歌ID下次獲取安卓ID的情况，獲取的ID都保存到本地，後面從本地去讀取
        //PL.i("getSdkUniqueId start");
        String localUniqueId = getUniqueId(ctx);
        if (SStringUtil.isNotEmpty(localUniqueId) && !localUniqueId.contains("00000000-0000")){
            return localUniqueId;
        }
        String adId = SdkUtil.getGoogleAdId(ctx);
        if (SStringUtil.isNotEmpty(adId) && !adId.contains("00000000-0000")){//先Google id   //00000000-0000-0000-0000-000000000000
            saveUniqueId(ctx,adId);
            return adId;
        }
        String androidId = ApkInfoUtil.getAndroidId(ctx);
        if (SStringUtil.isNotEmpty(androidId)){//獲取不到谷歌ID再獲取安卓ID
            saveUniqueId(ctx,androidId);
            return androidId;
        }

        String sdkUUID = UUID.randomUUID().toString();
        saveUniqueId(ctx,sdkUUID);
        return sdkUUID;
    }

    private static final String SDK_SHOW_TERM = "sdk_show_term";
    public static void saveShowTerm(Context context, boolean isRead){
        SPUtil.saveSimpleInfo(context, SdkUtil.SDK_SP_FILE,SDK_SHOW_TERM, isRead);
    }
    public static boolean getShowTerm(Context context){
        return SPUtil.getSimpleBoolean(context, SdkUtil.SDK_SP_FILE, SDK_SHOW_TERM);
    }

    private static final String MW_AGE_QUA = "MW_SDK_AGE_QUA";
    public static void saveAgeQua14(Context context, boolean agree){
        SPUtil.saveSimpleInfo(context, SdkUtil.SDK_SP_FILE,MW_AGE_QUA, agree);
    }
    public static boolean getAgeQua14(Context context){
        return SPUtil.getSimpleBoolean(context, SdkUtil.SDK_SP_FILE, MW_AGE_QUA);
    }

    /**
     * 保存首次登入时间,userid为key
     */
    public static void saveFirstLoginDate(Context context, String userid){
        SPUtil.saveSimpleInfo(context, SdkUtil.SDK_SP_FILE, userid, System.currentTimeMillis());
    }
    /**
     * 保存首次登入时间,userid为key
     */
    public static long getFirstLoginDate(Context context, String userid){
        return SPUtil.getSimpleLong(context, SdkUtil.SDK_SP_FILE, userid);
    }

    private static final String SP_KEY_USER_INFO = "SP_KEY_USER_INFO_";
    public static SUserInfo getSUserInfo(Context context, String userId) {
        if (SStringUtil.isEmpty(userId)){
            return null;
        }
        String infoJson = SPUtil.getSimpleString(context, SdkUtil.SDK_SP_FILE, SP_KEY_USER_INFO + userId);
        if (SStringUtil.isNotEmpty(infoJson)){
            try {
                Gson gson = new Gson();
                SUserInfo sUserInfo = gson.fromJson(infoJson, SUserInfo.class);
                return sUserInfo;
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void saveSUserInfo(Context context, SLoginResponse loginResponse) {
        if (loginResponse != null && loginResponse.getData() != null){
            SUserInfo sUserInfo = getSUserInfo(context, loginResponse.getData().getUserId());
            if (sUserInfo != null){//已存在，即已注册
                return;
            }
            String userId = loginResponse.getData().getUserId();
            SUserInfo newUserInfo = new SUserInfo();
            newUserInfo.setUserId(userId);
            newUserInfo.setRegTime(loginResponse.getData().getTimestamp());//使用服务器时间戳
            try {
                Gson gson = new Gson();
                String result = gson.toJson(newUserInfo);
                SPUtil.saveSimpleInfo(context, SdkUtil.SDK_SP_FILE, SP_KEY_USER_INFO + userId, result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateUserInfo(Context context, SUserInfo sUserInfo) {
        try {
            if (sUserInfo == null){
                return;
            }
            Gson gson = new Gson();
            String result = gson.toJson(sUserInfo);
            SPUtil.saveSimpleInfo(context, SdkUtil.SDK_SP_FILE, SP_KEY_USER_INFO + sUserInfo.getUserId(), result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static final String GAMA_SWITCH_JSON = "GAMA_SWITCH_JSON";

    /**
     * 保存Switch的文档
     */
    public static void saveSwitchJson(Context context, String switchJson){
        SPUtil.saveSimpleInfo(context, SdkUtil.SDK_SP_FILE,GAMA_SWITCH_JSON, switchJson);
    }

    public static String getSwitchJson(Context context){
        return SPUtil.getSimpleString(context, SdkUtil.SDK_SP_FILE, GAMA_SWITCH_JSON);
    }

    /**
     * 登入验证码是否开启,false:登入不需要验证码；true:登入需要验证码
     */
    public static boolean getVfcodeSwitchStatus(Context context){
        String json = getSwitchJson(context);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            JSONObject vfcodeSch = jsonObject.optJSONObject("vfcodeSch");
            String code = vfcodeSch.optString("open");
            // "0:关闭,1:开启"
            if("0".equals(code)) {
                return false;
            } else {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return true;
        }
    }

    /**
     * 获取接收手机验证码测试的提示语
     */
    public static String getPhoneMsgLimitHint(Context context) {
        return JsonUtil.getValueByKey(context, getSwitchJson(context), "smsMsg", "");
    }

    /**
     * 判断是否.app接口
     */
    public static boolean isInterfaceSurfixWithApp(Context context) {
//        SGameLanguage sGameLanguage = Localization.getSGameLanguage(context);
//        if(SGameLanguage.en_US == sGameLanguage) {
//            return false;
//        }
        return true;
    }


    /*public static void setAccountWithIcon(AccountModel accountModel, ImageView imageView, EditText editText){
        int imageResId = R.mipmap.mw_smail_icon;
        String showName = accountModel.getUserId();//accountModel.getThirdAccount();
//        if (SStringUtil.isEmpty(showName)){
//            showName = accountModel.getUserId();
//        }
        if (SLoginType.LOGIN_TYPE_FB.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.fb_smail_icon;
        }else  if (SLoginType.LOGIN_TYPE_GOOGLE.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.google_smail_icon;
        }else  if (SLoginType.LOGIN_TYPE_GUEST.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.guest_smail_icon;
        }else if (SLoginType.LOGIN_TYPE_LINE.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.line_smail_icon;
        }else if (SLoginType.LOGIN_TYPE_MG.equals(accountModel.getLoginType())){
            imageResId = R.mipmap.mw_smail_icon;
            showName = accountModel.getAccount();
        }

        imageView.setImageResource(imageResId);
        // 使光标始终在最后位置
        try {
            if (SStringUtil.isNotEmpty(showName)) {
                editText.setText(showName);
                Editable etable = editText.getText();
                Selection.setSelection(etable, showName.length());
            }else{
                editText.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public static boolean isVersion1(Context context) {//是否是第一套sdk
        if ("v1".equals(getSdkInnerVersion(context))){
            return true;
        }
        return false;
    }

    public static boolean isVersion2(Context context) {//是否是第二套sdk
        if ("v2".equals(getSdkInnerVersion(context))){
            return true;
        }
        return false;
    }

    public static String getSdkInnerVersion(Context context) {//获取版本
        return context.getResources().getString(R.string.sdk_inner_version);
    }

    public static List<PhoneInfo> getPhoneInfoList(Context context){

        String areaJson = getAreaCodeInfo(context);//先读取远程下载
        if (SStringUtil.isEmpty(areaJson)){
            areaJson = FileUtil.readAssetsTxtFile(context, "mwsdk/areaInfo");
        }
        if (SStringUtil.isEmpty(areaJson)){
            return null;
        }
        Gson gson = new Gson();
        PhoneInfo[] areaBeanList = gson.fromJson(areaJson, PhoneInfo[].class);
        List<PhoneInfo> phoneInfos = Arrays.asList(areaBeanList);
        return phoneInfos;
    }

    public static PhoneInfo getPhoneInfoByAreaCode(Context context, String areaCode){ //通过区号获取
        if (SStringUtil.isEmpty(areaCode)){
            return null;
        }
        areaCode = areaCode.trim();
        if (areaCode.startsWith("+")){
            areaCode = areaCode.replace("+","");
        }
        List<PhoneInfo> phoneInfos = getPhoneInfoList(context);
        if (phoneInfos == null || phoneInfos.isEmpty()){
            return null;
        }
        for(PhoneInfo phoneInfo : phoneInfos){
            if (areaCode.equals(phoneInfo.getValue())){
                return phoneInfo;
            }
        }
        return null;
    }

    private static final String KEY_AREA_CODE_INFO = "KEY_AREA_CODE_INFO";
    public static void saveAreaCodeInfo(Context context, String areaCodeInfo) {
        SPUtil.saveSimpleInfo(context, SdkUtil.SDK_SP_FILE, KEY_AREA_CODE_INFO, areaCodeInfo);
    }
    public static String getAreaCodeInfo(Context context) {
        return SPUtil.getSimpleString(context, SdkUtil.SDK_SP_FILE, KEY_AREA_CODE_INFO);
    }

    public static String getGameLanguage(Context context){

        if (ResConfig.isMoreLanguage(context)){

            Locale locale = Locale.getDefault();//mw_#Hant  zh_HK_#Hant zh_CN_#Hans en_US
            String language = locale.getLanguage();//zh en
            String country = locale.getCountry();//TW US
            PL.d("language=%s, country=%s", language, country);
            if (language.equals("en")){
                return SGameLanguage.en_US.getLanguage();
            }
            if (language.equals("zh") && country.equals("CN")){
                return SGameLanguage.zh_CN.getLanguage();
            }
            if (language.equals("zh") && country.equals("TW")){
                return SGameLanguage.zh_TW.getLanguage();
            }
            if (language.equals("zh") && country.equals("HK")){
                return SGameLanguage.zh_TW.getLanguage();
            }
            if (language.equals("vi")){
                return SGameLanguage.vi_VN.getLanguage();
            }
            if (language.equals("ko")){//韩语
                return SGameLanguage.ko_KR.getLanguage();
            }
            //return SGameLanguage.zh_TW.getLanguage();//默认为繁体
        }
        //主动通过配置文件设置的默认语言
        if (SStringUtil.isNotEmpty(ResConfig.getDefaultServerLanguage(context))){
            return ResConfig.getDefaultServerLanguage(context);
        }
        return SGameLanguage.zh_TW.getLanguage();//默认为繁体
    }

    private static final String SDK_FLOAT_CFG_DATA = "SDK_FLOAT_CFG_DATA";//保存sdk配置 cdn文件
    public static void saveFloatCfgData(Context context,String cfg){
        SPUtil.saveSimpleInfo(context, SDK_SP_FILE, SDK_FLOAT_CFG_DATA,cfg);
    }
    public static String getFloatCfgData(Context context){
        return SPUtil.getSimpleString(context, SDK_SP_FILE, SDK_FLOAT_CFG_DATA);
    }

    private static final String SDK_FLOAT_MENU_RES_DATA = "SDK_FLOAT_MENU_RES_DATA";//保存sdk配置 cdn文件
    public static void saveFloatMenuResData(Context context, String cfg){
        SPUtil.saveSimpleInfo(context, SDK_SP_FILE, SDK_FLOAT_MENU_RES_DATA,cfg);
    }
    public static String getFloatMenuResData(Context context){
        return SPUtil.getSimpleString(context, SDK_SP_FILE, SDK_FLOAT_MENU_RES_DATA);
    }
}

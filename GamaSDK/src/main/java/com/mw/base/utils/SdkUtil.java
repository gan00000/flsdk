package com.mw.base.utils;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;

import com.core.base.cipher.DESCipher;
import com.core.base.utils.ApkInfoUtil;
import com.core.base.utils.GamaTimeUtil;
import com.core.base.utils.JsonUtil;
import com.core.base.utils.PL;
import com.core.base.utils.SPUtil;
import com.core.base.utils.SStringUtil;
import com.mw.base.bean.SGameLanguage;
import com.mw.base.bean.SLoginType;
import com.mw.base.cfg.ConfigBean;
import com.mw.base.cfg.ResConfig;
import com.mw.sdk.R;
import com.mw.sdk.login.model.AccountModel;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mw.sdk.login.model.response.SLoginResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        return null;
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

        newAccountModel.setThirdAccount(thirdAccount);
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
                        accountModel.setAccount(accountObject.getString("sdk_account"));
                        accountModel.setPassword(accountObject.getString("sdk_password"));
                        accountModel.setTime(accountObject.getLong("sdk_time"));
                        accountModel.setLoginType(accountObject.getString("sdk_loginType"));
                        accountModel.setThirdId(accountObject.getString("sdk_thirdId"));
                        accountModel.setUserId(accountObject.getString("sdk_userId"));
                        accountModel.setThirdAccount(accountObject.getString("sdk_thirdAccount"));
                        accountModel.setBind(accountObject.getBoolean("sdk_bindAccount"));
                        accountModel.setLoginAccessToken(accountObject.getString("sdk_loginAccessToken"));
                        accountModel.setLoginTimestamp(accountObject.getString("sdk_loginTimestamp"));

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

    public static void saveSdkLoginData(Context context,String data){
        if (SStringUtil.isNotEmpty(data)){
            data = encryptText(data);//进行加密后保存
        }
        SPUtil.saveSimpleInfo(context, SDK_SP_FILE, SDK_LOGIN_SERVER_RETURN_DATA,data);
    }

    public static SLoginResponse getSdkLoginData(Context context){
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
        SLoginResponse sLoginResponse = getSdkLoginData(context);
        if (sLoginResponse != null && sLoginResponse.isRequestSuccess() && sLoginResponse.getData()!= null && SStringUtil.isNotEmpty(sLoginResponse.getData().getUserId())){
            return true;
        }
        return false;
    }


    /**
     * 获取当次登入的userId
     */
    public static String getUid(Context context){
        SLoginResponse sLoginResponse = getSdkLoginData(context);
        if (sLoginResponse != null && sLoginResponse.isRequestSuccess() && sLoginResponse.getData()!= null && SStringUtil.isNotEmpty(sLoginResponse.getData().getUserId())){
            return sLoginResponse.getData().getUserId();
        }
        return "";
    }

    public static String getSdkTimestamp(Context context){
        SLoginResponse sLoginResponse = getSdkLoginData(context);
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
        SLoginResponse sLoginResponse = getSdkLoginData(context);
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

        if (account.contains("@")){//简单检查只是邮箱就行
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

    public static boolean isXM(Context context){
        return ResConfig.getConfigInAssetsProperties(context,"gama_login_type").equals("100");
    }
    public static boolean isMainland(Context context){//100為大陸sdk,其他為海外
        return ResConfig.getConfigInAssetsProperties(context,"gama_sdk_area").equals("100");
    }

    private static final String GAMA_GOOGLE_ADVERTISING_ID = "GAMA_GOOGLE_ADVERTISING_ID";
    public static void saveGoogleAdId(Context context, String googleAdId){
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

    /**
     * 生成免注册登入账号
     */
    public static  String getGoogleAdid1AndroidId(Context ctx){
        String adId = SdkUtil.getGoogleAdId(ctx);
        if (SStringUtil.isNotEmpty(adId)){//先Google id
            return adId;
        }
        return ApkInfoUtil.getAndroidId(ctx);
    }

    private static final String GAMA_START_TERM_STATUS = "GAMA_START_TERM_STATUS";
    public static void saveStartTermRead(Context context, boolean isRead){
        SPUtil.saveSimpleInfo(context, SdkUtil.SDK_SP_FILE,GAMA_START_TERM_STATUS, isRead);
    }
    public static boolean getStartTermRead(Context context){
        return SPUtil.getSimpleBoolean(context, SdkUtil.SDK_SP_FILE, GAMA_START_TERM_STATUS);
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

    /**
     * 判断是否有Twitter相关功能
     * @param context
     * @return
     */
    public static boolean hasTwitter(Context context) {
        SGameLanguage sGameLanguage = Localization.getSGameLanguage(context);
        if(SGameLanguage.ja_JP == sGameLanguage) {
            return true;
        }
        return false;
    }

    /**
     * 同时保存年龄和时间
     * @param context
     * @param age
     */
    public static void saveAgeAndTime(Context context, int age) {
        saveAgeTime(context);
        saveAge(context, age);
    }

    private static final String PREFIX_AGE_TIME = "GAMA_AGE_TIME_";

    /**
     * 保存当次年龄日期
     * @param context
     */
    public static void saveAgeTime(Context context) {
        SPUtil.saveSimpleInfo(context, SdkUtil.SDK_SP_FILE, PREFIX_AGE_TIME + getUid(context), System.currentTimeMillis());
    }

    /**
     * 获取上次年龄日期
     * @param context
     * @return
     */
    public static long getAgeTime(Context context) {
        return SPUtil.getSimpleLong(context, SdkUtil.SDK_SP_FILE, PREFIX_AGE_TIME + getUid(context));
    }

    private static final String PREFIX_AGE_ = "GAMA_AGE_";

    /**
     * 保存当次年龄日期
     * @param context
     */
    public static void saveAge(Context context, int age) {
        SPUtil.saveSimpleInteger(context, SdkUtil.SDK_SP_FILE, PREFIX_AGE_ + getUid(context), age);
    }

    /**
     * 获取上次年龄日期
     * @param context
     * @return
     */
    public static int getAge(Context context) {
        return SPUtil.getSimpleInteger(context, SdkUtil.SDK_SP_FILE, PREFIX_AGE_ + getUid(context));
    }

    /**
     * 判断是否有需要显示年齡限制页面
     * @param context
     * @return
     */
    public static boolean needShowAgePage(Context context) {
        SGameLanguage sGameLanguage = Localization.getSGameLanguage(context);
        if(SGameLanguage.ja_JP == sGameLanguage) {
            if(getAge(context) >= 20) {
                return false;
            } else if(GamaTimeUtil.isSameMonth(getAgeTime(context))) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 判断是否有需要查询年齡限制
     * @param context
     * @return
     */
    public static boolean needRequestAgeLimit(Context context) {
        SGameLanguage sGameLanguage = Localization.getSGameLanguage(context);
        if(SGameLanguage.ja_JP == sGameLanguage) {
            return getAge(context) < 20;
        }
        return false;
    }

    private static final String PREFIX_FIRSTPAY_ = "GAMA_FIRSTPAY_";

    /**
     * 保存用户首储状态
     * @param context
     */
    public static void saveFirstPay(Context context) {
        SPUtil.saveSimpleInfo(context, SdkUtil.SDK_SP_FILE, PREFIX_FIRSTPAY_ + getUid(context), true);
    }

    /**
     * 获取用户首储状态
     * @param context
     * @return
     */
    public static boolean getFirstPay(Context context) {
        return SPUtil.getSimpleBoolean(context, SdkUtil.SDK_SP_FILE, PREFIX_FIRSTPAY_ + getUid(context));
    }

    private static final String PREFIX_ONLINE = "GAMA_ONLINE";
    /**
     * 保存活跃时间戳
     * @param context
     */
    public static void saveOnlineTimeInfo(Context context, long timeStamp) {
        if(TextUtils.isEmpty(getUid(context))
                || TextUtils.isEmpty(getRoleId(context))
                || TextUtils.isEmpty(getServerCode(context))) {
            PL.i("没有角色信息，不保存在线时间");
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(GAMA_LOGIN_USER_ID, getUid(context));
            jsonObject.put(GAMA_LOGIN_ROLE_NAME, getRoleName(context));
            jsonObject.put(GAMA_LOGIN_ROLE_SERVER_CODE, getServerCode(context));
            jsonObject.put(GAMA_LOGIN_ROLE_SERVER_NAME, getServerName(context));
            jsonObject.put(GAMA_LOGIN_ROLE_ID, getRoleId(context));
            jsonObject.put(GAMA_LOGIN_ROLE_LEVEL, getRoleLevel(context));
            jsonObject.put(GAMA_LOGIN_ROLE_VIP, getRoleVip(context));
            jsonObject.put("timestamp", timeStamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SPUtil.saveSimpleInfo(context, SdkUtil.SDK_SP_FILE, PREFIX_ONLINE, jsonObject.toString());
    }

    /**
     * 获取在线时长
     * @param context
     * @return
     */
    public static String getOnlineTimeInfo(Context context) {
        return SPUtil.getSimpleString(context, SdkUtil.SDK_SP_FILE, PREFIX_ONLINE);
    }

    /**
     * 重置在线时长
     * @param context
     * @return
     */
    public static void resetOnlineTimeInfo(Context context) {
        SPUtil.saveSimpleInfo(context, SdkUtil.SDK_SP_FILE, PREFIX_ONLINE, "");
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
     * 判断是否北美地区
     */
    public static boolean isNorthAmarican(Context context) {
        SGameLanguage sGameLanguage = Localization.getSGameLanguage(context);
        if(SGameLanguage.en_US == sGameLanguage) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否日本地区
     */
    public static boolean isJapan(Context context) {
        SGameLanguage sGameLanguage = Localization.getSGameLanguage(context);
        if(SGameLanguage.ja_JP == sGameLanguage) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否韩国地区
     */
    public static boolean isKorea(Context context) {
        SGameLanguage sGameLanguage = Localization.getSGameLanguage(context);
        if(SGameLanguage.ko_KR == sGameLanguage) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否.app接口
     */
    public static boolean isInterfaceSurfixWithApp(Context context) {
        SGameLanguage sGameLanguage = Localization.getSGameLanguage(context);
        if(SGameLanguage.en_US == sGameLanguage) {
            return false;
        }
        return true;
    }


    /**
     * 判断是否需要请求登入验证码开关
     */
    public static boolean isNeedVfSwitch(Context context) {
        SGameLanguage sGameLanguage = Localization.getSGameLanguage(context);
        if(SGameLanguage.en_US == sGameLanguage) {
            return false;
        }
        return true;
    }


    public static void setAccountWithIcon(AccountModel accountModel, ImageView imageView, EditText editText){
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean isVersion2(Context context) {//是否是第二套sdk
        if ("v2".equals(context.getResources().getString(R.string.sdk_inner_version))){
            return true;
        }
        return false;
    }
}

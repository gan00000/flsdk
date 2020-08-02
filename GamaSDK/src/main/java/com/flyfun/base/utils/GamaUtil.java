package com.flyfun.base.utils;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.core.base.cipher.DESCipher;
import com.core.base.utils.ApkInfoUtil;
import com.core.base.utils.FileUtil;
import com.core.base.utils.GamaTimeUtil;
import com.core.base.utils.JsonUtil;
import com.core.base.utils.PL;
import com.core.base.utils.SPUtil;
import com.core.base.utils.SStringUtil;
import com.flyfun.base.bean.SGameLanguage;
import com.flyfun.base.cfg.ConfigBean;
import com.flyfun.base.cfg.ResConfig;
import com.flyfun.sdk.login.model.AccountModel;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by gan on 2017/2/7.
 */

public class GamaUtil {

    /**
     * 本地SharePreference数据库名
     */
    public static final String GAMA_SP_FILE = "gama_sp_file.xml";
    public static final String GAMA_SDK_LOGIN_TERMS_FILE = "gama_sdk_login_terms_file.xml";

    public static final String ADS_ADVERTISERS_NAME = "ADS_ADVERTISERS_NAME";
    public static final String GAMA_GAME_LANGUAGE = "GAMA_GAME_LANGUAGE";

    public static final String SDK_LOGIN_ACCOUNT_INFO = "SDK_LOGIN_ACCOUNT_INFO";//保存用户的账号信息

    public static final String GAMA_LOGIN_SERVER_RETURN_DATA = "GAMA_LOGIN_SERVER_RETURN_DATA";//保存服务端返回的数据

    public static final String GAMA_SDK_CFG = "GAMA_SDK_CFG";//保存sdk配置 cdn文件
    public static final String GAMA_SDK_LOGIN_TERMS = "GAMA_SDK_LOGIN_TERMS";
    public static final String GAMA_MAC_LOGIN_USERNAME = "GAMA_MAC_LOGIN_USERNAME";
    public static final String GAMA_MAC_LOGIN_PASSWORD = "GAMA_MAC_LOGIN_PASSWORD";
    /**
     * 上一次的登录类型
     */
    public static final String GAMA_PREVIOUS_LOGIN_TYPE = "GAMA_PREVIOUS_LOGIN_TYPE";


    public static void saveSdkCfg(Context context,String cfg){
        SPUtil.saveSimpleInfo(context,GAMA_SP_FILE,GAMA_SDK_CFG,cfg);
    }
    public static String getSdkCfgString(Context context){
        return SPUtil.getSimpleString(context,GAMA_SP_FILE,GAMA_SDK_CFG);
    }


    private static ConfigBean configBean;

    public static ConfigBean getSdkCfg(Context context){
//        if (configBean != null){//缓存
//            return configBean;
//        }
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

    public static void saveAccountModel(Context context, String account, String password, boolean updateTime){
        List<AccountModel> mls = getAccountModels(context);
        for (AccountModel a: mls) {
            if (a.getAccount().equals(account)){
                a.setPassword(password);
                if (updateTime){
                    a.setTime(System.currentTimeMillis());
                }
                saveAccountModels(context,mls);
                return;
            }
        }
        AccountModel newAccountModel = new AccountModel();//新添加一个保存
        newAccountModel.setAccount(account);
        newAccountModel.setPassword(password);
        newAccountModel.setTime(System.currentTimeMillis());
        mls.add(newAccountModel);
        saveAccountModels(context,mls);

    }

    public static void removeAccountModel(Context context, String account){
        List<AccountModel> mls = getAccountModels(context);
        AccountModel xxxmls = null;
        for (AccountModel a: mls) {
            if (a.getAccount().equals(account)){
                xxxmls = a;
                break;
            }
        }

        if (xxxmls != null && mls.contains(xxxmls)){
            mls.remove(xxxmls);
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
                jsonArray.put(accountObject);
            }
            SPUtil.saveSimpleInfo(context,GAMA_SP_FILE, SDK_LOGIN_ACCOUNT_INFO, jsonArray.toString());
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
        String accountStringInfo = SPUtil.getSimpleString(context,GAMA_SP_FILE, SDK_LOGIN_ACCOUNT_INFO);
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
    public static void saveMacAccount(Context context,String account){
        SPUtil.saveSimpleInfo(context,GAMA_SP_FILE, GAMA_MAC_LOGIN_USERNAME, account);
    }

    public static String getMacAccount(Context context){
        return SPUtil.getSimpleString(context,GAMA_SP_FILE, GAMA_MAC_LOGIN_USERNAME);
    }

    public static void saveMacPassword(Context context,String password){
        SPUtil.saveSimpleInfo(context,GAMA_SP_FILE, GAMA_MAC_LOGIN_PASSWORD, encryptPassword(password));
    }

    public static String getMacPassword(Context context){
        return decryptPassword(SPUtil.getSimpleString(context,GAMA_SP_FILE, GAMA_MAC_LOGIN_PASSWORD));
    }

    /**
     * 保存登入方式
     * @param context
     * @param loginType
     */
    public static void savePreviousLoginType(Context context,String loginType){
        SPUtil.saveSimpleInfo(context,GAMA_SP_FILE, GAMA_PREVIOUS_LOGIN_TYPE, loginType);
    }

    /**
     * 获取登入方式
     * @param context
     * @return
     */
    public static String getPreviousLoginType(Context context){
        return SPUtil.getSimpleString(context,GAMA_SP_FILE, GAMA_PREVIOUS_LOGIN_TYPE);
    }

//    private final static String cipherKey = "20170314starpypassword";

    /**
     * 加密后的密码前缀
     */
    private final static String cipherPasswordFlag = "888*****888";

    /**
     * 加密密码
     */
    private static String encryptPassword(String password){
        try {
            if (SStringUtil.isNotEmpty(password)){
                return cipherPasswordFlag + DESCipher.encrypt3DES(password, getSecretKey());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return password;
    }

    /**
     * 解密密码
     */
    private static String decryptPassword(String encryptText){
        try {
            if (SStringUtil.isNotEmpty(encryptText) && encryptText.startsWith(cipherPasswordFlag)){
                encryptText = encryptText.replace(cipherPasswordFlag,"");
                return DESCipher.decrypt3DES(encryptText, getSecretKey());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptText;
    }

    public static void saveSdkLoginData(Context context,String data){
        SPUtil.saveSimpleInfo(context,GAMA_SP_FILE,GAMA_LOGIN_SERVER_RETURN_DATA,data);
    }

    public static String getSdkLoginData(Context context){
        return SPUtil.getSimpleString(context,GAMA_SP_FILE,GAMA_LOGIN_SERVER_RETURN_DATA);
    }

    public static boolean isLogin(Context context){
        return SStringUtil.isNotEmpty(getSdkLoginData(context));
    }

    public static void saveSdkLoginTerms(Context context,String terms){
        SPUtil.saveSimpleInfo(context, GAMA_SDK_LOGIN_TERMS_FILE,GAMA_SDK_LOGIN_TERMS,terms);
    }

    /**
     * 获取服务条款
     * @param context
     * @return
     */
    public static String getSdkLoginTerms(Context context){
        String m = SPUtil.getSimpleString(context, GAMA_SDK_LOGIN_TERMS_FILE,GAMA_SDK_LOGIN_TERMS);
        if (TextUtils.isEmpty(m)){
            String gameLanguage = ResConfig.getGameLanguage(context);
            m = FileUtil.readAssetsTxtFile(context,"flsdk/" + gameLanguage + "/s_sdk_login_terms.txt");
        }
        if (isXM(context)){
            m = m.replaceAll("新玩意整合行銷有限公司","Gamamobi網絡科技有限公司");
        }
        return m;
    }

    /**
     * 获取当次登入的userId
     */
    public static String getUid(Context context){
        return JsonUtil.getValueByKey(context,getSdkLoginData(context), "userId", "");
    }

    public static String getSdkTimestamp(Context context){
        return JsonUtil.getValueByKey(context,getSdkLoginData(context), "timestamp", "");
    }

    /**
     * 获取登入的token
     * @param context
     * @return
     */
    public static String getSdkAccessToken(Context context){
        return JsonUtil.getValueByKey(context,getSdkLoginData(context), "accessToken", "");
    }

    /**
     * 获取当次登入的账号是否已经绑定手机
     */
    public static boolean isAccountLinked(Context context){
        String beLink = JsonUtil.getValueByKey(context,getSdkLoginData(context), "beLinked", "");
        return "1".equals(beLink);
    }

    /**
     * 绑定手机后刷新当前账号绑定状态
     */
    public static void setAccountLinked(Context context){
        String newSdkLoginData = JsonUtil.setValueByKey(context, getSdkLoginData(context), "beLinked", "1");
        if(!TextUtils.isEmpty(newSdkLoginData)) {
            saveSdkLoginData(context, newSdkLoginData);
        }
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
        return SPUtil.getSimpleString(context,GAMA_SP_FILE,GAMA_LOGIN_ROLE_INFO);
    }

    /**
     * 以Json形式保存角色信息
     */
    private static void saveRoleInfoJson(Context context,String roleInfo){
        SPUtil.saveSimpleInfo(context,GAMA_SP_FILE,GAMA_LOGIN_ROLE_INFO,roleInfo);
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
        if (account.matches("^[A-Za-z0-9]{6,18}$")){
            return true;
        }
        return false;
    }

    public static boolean checkPassword(String password){
        if (TextUtils.isEmpty(password)){
            return false;
        }
        if (password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?!.*[\\W]).{8,16}$")){
            return true;
        }
        return false;
    }

    /**
     * 旧版密码验证，目前北美继续使用
     * @param password
     * @return
     */
    public static boolean checkPasswordOld(String password){
        if (TextUtils.isEmpty(password)){
            return false;
        }
        if (password.matches("^[A-Za-z0-9]{6,18}$")){
            return true;
        }
        return false;
    }

    /**
     * 用于解密动态域名的secretKey
     */
//    private static final String GAMA_DY_ENCRYPT_SECRETKEY = "(starpy99988820170227dyrl)";

    private static String getSecretKey() {
        return getKeyPrefix() + getKeySurfix();
    }

    private static String getKeyPrefix() {
        return "(starpy999888";
    }

    private static String getKeySurfix() {
        int index = 2017 * 10000;
        int sec = index + 227;
        return sec + "dyrl)";
    }

    public static String encryptDyUrl(Context context,String data){
        try {
            return DESCipher.encrypt3DES(data, getSecretKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 解密动态域名信息
     */
    public static String decryptDyUrl(Context context,String data){
        try {
            return DESCipher.decrypt3DES(data, getSecretKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * G+登录的三方ID
     */
    private static final String GAMA_LOGIN_GOOGLE_ID = "GAMA_LOGIN_GOOGLE_ID";

    /**
     * 保存G+登录的三方ID
     */
    public static void saveGoogleId(Context context, String googleId){
        SPUtil.saveSimpleInfo(context, GamaUtil.GAMA_SP_FILE,GAMA_LOGIN_GOOGLE_ID,googleId);
    }

    /**
     * 获取保存的G+登录的三方ID
     */
    public static String getGoogleId(Context context){
        return SPUtil.getSimpleString(context, GamaUtil.GAMA_SP_FILE,GAMA_LOGIN_GOOGLE_ID);
    }

    /**
     * Twitter登录的三方ID
     */
    private static final String GAMA_LOGIN_TWITTER_ID = "GAMA_LOGIN_TWITTER_ID";

    /**
     * 保存Twitter登录的三方ID
     */
    public static void saveTwitterId(Context context, String googleId){
        SPUtil.saveSimpleInfo(context, GamaUtil.GAMA_SP_FILE,GAMA_LOGIN_TWITTER_ID,googleId);
    }

    /**
     * 获取保存的Twitter登录的三方ID
     */
    public static String getTwitterId(Context context){
        return SPUtil.getSimpleString(context, GamaUtil.GAMA_SP_FILE,GAMA_LOGIN_TWITTER_ID);
    }

    public static boolean isXM(Context context){
        return ResConfig.getConfigInAssetsProperties(context,"gama_login_type").equals("100");
    }
    public static boolean isMainland(Context context){//100為大陸sdk,其他為海外
        return ResConfig.getConfigInAssetsProperties(context,"gama_sdk_area").equals("100");
    }

    private static final String GAMA_GOOGLE_ADVERTISING_ID = "GAMA_GOOGLE_ADVERTISING_ID";
    public static void saveGoogleAdId(Context context, String googleAdId){
        SPUtil.saveSimpleInfo(context, GamaUtil.GAMA_SP_FILE,GAMA_GOOGLE_ADVERTISING_ID,googleAdId);
    }
    public static String getGoogleAdId(Context context){
        return SPUtil.getSimpleString(context, GamaUtil.GAMA_SP_FILE,GAMA_GOOGLE_ADVERTISING_ID);
    }

    private static final String GAMA_GOOGLE_INSTALL_REFERRER = "GAMA_GOOGLE_INSTALL_REFERRER";
    public static void saveReferrer(Context context, String referrer){
        SPUtil.saveSimpleInfo(context, GamaUtil.GAMA_SP_FILE,GAMA_GOOGLE_INSTALL_REFERRER,referrer);
    }
    public static String getReferrer(Context context){
        return SPUtil.getSimpleString(context, GamaUtil.GAMA_SP_FILE,GAMA_GOOGLE_INSTALL_REFERRER);
    }
    private static final String GAMA_GOOGLE_TOKEN_ID_STRING = "GAMA_GOOGLE_TOKEN_ID_STRING";
    public static void saveGoogleIdToken(Context context, String idTokenString){
        SPUtil.saveSimpleInfo(context, GamaUtil.GAMA_SP_FILE,GAMA_GOOGLE_TOKEN_ID_STRING,idTokenString);
    }
    public static String getGoogleIdToken(Context context){
        return SPUtil.getSimpleString(context, GamaUtil.GAMA_SP_FILE,GAMA_GOOGLE_TOKEN_ID_STRING);
    }

    /**
     * 生成免注册登入账号
     */
    public static  String getCustomizedUniqueId1AndroidId1Adid(Context ctx){
        String adId = GamaUtil.getGoogleAdId(ctx);
        if (SStringUtil.isNotEmpty(adId)){//先Google id
            return adId;
        }

        return ApkInfoUtil.getCustomizedUniqueIdOrAndroidId(ctx);
    }

    private static final String GAMA_START_TERM_STATUS = "GAMA_START_TERM_STATUS";
    public static void saveStartTermRead(Context context, boolean isRead){
        SPUtil.saveSimpleInfo(context, GamaUtil.GAMA_SP_FILE,GAMA_START_TERM_STATUS, isRead);
    }
    public static boolean getStartTermRead(Context context){
        return SPUtil.getSimpleBoolean(context, GamaUtil.GAMA_SP_FILE, GAMA_START_TERM_STATUS);
    }

    /**
     * 保存首次登入时间,userid为key
     */
    public static void saveFirstLoginDate(Context context, String userid){
        SPUtil.saveSimpleInfo(context, GamaUtil.GAMA_SP_FILE, userid, System.currentTimeMillis());
    }
    /**
     * 保存首次登入时间,userid为key
     */
    public static long getFirstLoginDate(Context context, String userid){
        return SPUtil.getSimpleLong(context, GamaUtil.GAMA_SP_FILE, userid);
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
        SPUtil.saveSimpleInfo(context, GamaUtil.GAMA_SP_FILE, PREFIX_AGE_TIME + getUid(context), System.currentTimeMillis());
    }

    /**
     * 获取上次年龄日期
     * @param context
     * @return
     */
    public static long getAgeTime(Context context) {
        return SPUtil.getSimpleLong(context, GamaUtil.GAMA_SP_FILE, PREFIX_AGE_TIME + getUid(context));
    }

    private static final String PREFIX_AGE_ = "GAMA_AGE_";

    /**
     * 保存当次年龄日期
     * @param context
     */
    public static void saveAge(Context context, int age) {
        SPUtil.saveSimpleInteger(context, GamaUtil.GAMA_SP_FILE, PREFIX_AGE_ + getUid(context), age);
    }

    /**
     * 获取上次年龄日期
     * @param context
     * @return
     */
    public static int getAge(Context context) {
        return SPUtil.getSimpleInteger(context, GamaUtil.GAMA_SP_FILE, PREFIX_AGE_ + getUid(context));
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
        SPUtil.saveSimpleInfo(context, GamaUtil.GAMA_SP_FILE, PREFIX_FIRSTPAY_ + getUid(context), true);
    }

    /**
     * 获取用户首储状态
     * @param context
     * @return
     */
    public static boolean getFirstPay(Context context) {
        return SPUtil.getSimpleBoolean(context, GamaUtil.GAMA_SP_FILE, PREFIX_FIRSTPAY_ + getUid(context));
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
        SPUtil.saveSimpleInfo(context, GamaUtil.GAMA_SP_FILE, PREFIX_ONLINE, jsonObject.toString());
    }

    /**
     * 获取在线时长
     * @param context
     * @return
     */
    public static String getOnlineTimeInfo(Context context) {
        return SPUtil.getSimpleString(context, GamaUtil.GAMA_SP_FILE, PREFIX_ONLINE);
    }

    /**
     * 重置在线时长
     * @param context
     * @return
     */
    public static void resetOnlineTimeInfo(Context context) {
        SPUtil.saveSimpleInfo(context, GamaUtil.GAMA_SP_FILE, PREFIX_ONLINE, "");
    }


    private static final String GAMA_SWITCH_JSON = "GAMA_SWITCH_JSON";

    /**
     * 保存Switch的文档
     */
    public static void saveSwitchJson(Context context, String switchJson){
        SPUtil.saveSimpleInfo(context, GamaUtil.GAMA_SP_FILE,GAMA_SWITCH_JSON, switchJson);
    }

    public static String getSwitchJson(Context context){
        return SPUtil.getSimpleString(context, GamaUtil.GAMA_SP_FILE, GAMA_SWITCH_JSON);
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

//    private static final String GAMA_MAC_LOGIN_COUNT = "GAMA_VFCGAMA_MAC_LOGIN_COUNTODE_SWITCH_STATUS";
//    public static void saveMacLoginCount(Context context){
//        int macLoginCount = getMacLoginCount(context) + 1;
//        SPUtil.saveSimpleInfo(context, GamaUtil.GAMA_SP_FILE,GAMA_MAC_LOGIN_COUNT, macLoginCount);
//    }
//    /**
//     * 获取免注册登入次数
//     */
//    public static int getMacLoginCount(Context context){
//        return SPUtil.getSimpleInteger(context, GamaUtil.GAMA_SP_FILE, GAMA_MAC_LOGIN_COUNT);
//    }
}

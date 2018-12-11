package com.gama.base.utils;

import android.content.Context;
import android.text.TextUtils;

import com.core.base.cipher.DESCipher;
import com.core.base.utils.ApkInfoUtil;
import com.core.base.utils.FileUtil;
import com.core.base.utils.JsonUtil;
import com.core.base.utils.SPUtil;
import com.core.base.utils.SStringUtil;
import com.gama.base.cfg.ConfigBean;
import com.gama.base.cfg.ResConfig;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

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

    public static final String GAMA_LOGIN_USERNAME = "GAMA_LOGIN_USERNAME";//保存用户的账号
    public static final String GAMA_LOGIN_PASSWORD = "GAMA_LOGIN_PASSWORD";//保存用户的密码

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

    public static void saveAccount(Context context,String account){
        SPUtil.saveSimpleInfo(context,GAMA_SP_FILE, GAMA_LOGIN_USERNAME, account);
    }

    public static String getAccount(Context context){
        return SPUtil.getSimpleString(context,GAMA_SP_FILE, GAMA_LOGIN_USERNAME);
    }
    public static void saveMacAccount(Context context,String account){
        SPUtil.saveSimpleInfo(context,GAMA_SP_FILE, GAMA_MAC_LOGIN_USERNAME, account);
    }

    public static String getMacAccount(Context context){
        return SPUtil.getSimpleString(context,GAMA_SP_FILE, GAMA_MAC_LOGIN_USERNAME);
    }

    public static void savePassword(Context context,String password){
        SPUtil.saveSimpleInfo(context,GAMA_SP_FILE, GAMA_LOGIN_PASSWORD, encryptPassword(password));
    }

    public static String getPassword(Context context){
        return decryptPassword(SPUtil.getSimpleString(context,GAMA_SP_FILE, GAMA_LOGIN_PASSWORD));
    }
    public static void saveMacPassword(Context context,String password){
        SPUtil.saveSimpleInfo(context,GAMA_SP_FILE, GAMA_MAC_LOGIN_PASSWORD, encryptPassword(password));
    }

    public static String getMacPassword(Context context){
        return decryptPassword(SPUtil.getSimpleString(context,GAMA_SP_FILE, GAMA_MAC_LOGIN_PASSWORD));
    }

    public static void savePreviousLoginType(Context context,String loginType){
        SPUtil.saveSimpleInfo(context,GAMA_SP_FILE, GAMA_PREVIOUS_LOGIN_TYPE, loginType);
    }

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
                return cipherPasswordFlag + DESCipher.encrypt3DES(password,GAMA_DY_ENCRYPT_SECRETKEY);
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
                return DESCipher.decrypt3DES(encryptText,GAMA_DY_ENCRYPT_SECRETKEY);
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
            m = FileUtil.readAssetsTxtFile(context,"gama/" + gameLanguage + "/s_sdk_login_terms.txt");
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

    public static String getSdkAccessToken(Context context){
        return JsonUtil.getValueByKey(context,getSdkLoginData(context), "accessToken", "");
    }

    private static final String GAMA_LOGIN_ROLE_ID = "GAMA_LOGIN_ROLE_ID";
    private static final String GAMA_LOGIN_ROLE_NAME = "GAMA_LOGIN_ROLE_NAME";
    private static final String GAMA_LOGIN_ROLE_SERVER_CODE = "GAMA_LOGIN_ROLE_SERVER_CODE";
    private static final String GAMA_LOGIN_ROLE_SERVER_NAME = "GAMA_LOGIN_ROLE_SERVER_NAME";
    private static final String GAMA_LOGIN_ROLE_INFO = "GAMA_LOGIN_ROLE_INFO";
    private static final String GAMA_LOGIN_ROLE_LEVEL = "GAMA_LOGIN_ROLE_LEVEL";
    private static final String GAMA_LOGIN_ROLE_VIP = "GAMA_LOGIN_ROLE_VIP";

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
        if (password.matches("^[A-Za-z0-9]{6,18}$")){
            return true;
        }
        return false;
    }

    /**
     * 用于解密动态域名的secretKey
     */
    private static final String GAMA_DY_ENCRYPT_SECRETKEY = "(starpy99988820170227dyrl)";
    public static String encryptDyUrl(Context context,String data){
        try {
            return DESCipher.encrypt3DES(data,GAMA_DY_ENCRYPT_SECRETKEY);
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
            return DESCipher.decrypt3DES(data,GAMA_DY_ENCRYPT_SECRETKEY);
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
}

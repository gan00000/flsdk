package com.flyfun.sdk.ads;

import android.content.Context;

import com.core.base.utils.ApkInfoUtil;
import com.core.base.utils.SStringUtil;
import com.flyfun.base.bean.SGameBaseRequestBean;
import com.flyfun.base.bean.SLoginType;
import com.flyfun.base.utils.GamaUtil;
import com.flyfun.sdk.login.model.AccountModel;

public class Ad2RequestBean extends SGameBaseRequestBean {

    private String game_code;
    private String game_name;
    private String role_id = "";
    private String role_name = "";
    private String server_code = "";
    private String server_name = "";
    private String user_id = "";
    private String user_name = "";

    private String device_type;
    private String system = "android";
    private String system_version = "";

    private String sigin = "";


    public Ad2RequestBean(Context context) {
        super(context);
    }


    public void setValue(Context context) {

        device_type = this.getDeviceType();
        system_version = this.getSystemVersion();

        user_id = this.getUserId();
        game_code = this.getGameCode();
        role_id = this.getRoleId();
        role_name = this.getRoleName();
        server_code = this.getServerCode();
        server_name = this.getServerName();
        game_name = ApkInfoUtil.getApplicationName(context);

        String previousLoginType = GamaUtil.getPreviousLoginType(context);
        if (SStringUtil.isEqual(SLoginType.LOGIN_TYPE_MG, previousLoginType)) {//自動登錄
            AccountModel accountModel = GamaUtil.getLastLoginAccount(context);
            if (accountModel != null){
                user_name = accountModel.getAccount();
            }

        }
        sigin = SStringUtil.toMd5(game_code + server_code + user_id + role_id + "FLYFUNGAME", true);
    }



    public String getGame_code() {
        return game_code;
    }

    public void setGame_code(String game_code) {
        this.game_code = game_code;
    }

    public String getGame_name() {
        return game_name;
    }

    public void setGame_name(String game_name) {
        this.game_name = game_name;
    }

    public String getRole_id() {
        return role_id;
    }

    public void setRole_id(String role_id) {
        this.role_id = role_id;
    }

    public String getServer_code() {
        return server_code;
    }

    public void setServer_code(String server_code) {
        this.server_code = server_code;
    }

    public String getServer_name() {
        return server_name;
    }

    public void setServer_name(String server_name) {
        this.server_name = server_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getSystem_version() {
        return system_version;
    }

    public void setSystem_version(String system_version) {
        this.system_version = system_version;
    }

    public String getSigin() {
        return sigin;
    }

    public void setSigin(String sigin) {
        this.sigin = sigin;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }
}

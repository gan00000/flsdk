package com.gama.base.bean;

import android.content.Context;

import com.core.base.utils.SStringUtil;
import com.gama.base.utils.GamaUtil;

/**
 * <p>Title: SSdkBaseRequestBean</p> <p>Description: 进入游戏角色之后请求参数实体，所有属性实例时都已经初始化</p>
 *
 * @author GanYuanrong
 * @date 2014年8月22日
 */
public class SGameBaseRequestBean extends SSdkBaseRequestBean {

    private String userId;
    private String serverCode;
    private String serverName;
    private String roleId = "";
    private String roleName = "";

    private String roleLevel = "";
    private String roleVipLevel = "";


    public SGameBaseRequestBean(Context context) {
        super(context);
        initGameField(context);
    }


    private void initGameField(Context context) {
        userId = GamaUtil.getUid(context);

        serverCode = GamaUtil.getServerCode(context);
        serverName = GamaUtil.getServerName(context);
        roleName = GamaUtil.getRoleName(context);
        roleId = GamaUtil.getRoleId(context);

        roleLevel = GamaUtil.getRoleLevel(context);
        roleVipLevel = GamaUtil.getRoleVip(context);
    }

    public boolean isInitOk(){
        return !SStringUtil.hasEmpty(userId,getGameCode(),serverCode,roleId);
    }

    public String getRoleLevel() {
        return roleLevel;
    }

    public void setRoleLevel(String roleLevel) {
        this.roleLevel = roleLevel;
    }

    public String getRoleVipLevel() {
        return roleVipLevel;
    }

    public void setRoleVipLevel(String roleVipLevel) {
        this.roleVipLevel = roleVipLevel;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}

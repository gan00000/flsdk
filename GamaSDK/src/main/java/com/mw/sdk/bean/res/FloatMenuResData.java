package com.mw.sdk.bean.res;

import com.core.base.bean.BaseResponseModel;

import java.util.ArrayList;

/**
 * Created by ganyuanrong on 2017/2/23.
 */

public class FloatMenuResData extends BaseResponseModel {

    @Override
    public boolean isRequestSuccess() {
        //2008表示已经发币
        return SUCCESS_CODE.equals(this.getCode());
    }

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data{

        private String buttonIcon;
        private boolean buttonSwitch;
        private String gameIcon;

        private ArrayList<MenuData> menuList;
        private FLUserInfo userInfo;


        public ArrayList<MenuData> getMenuList() {
            return menuList;
        }

        public void setMenuList(ArrayList<MenuData> menuList) {
            this.menuList = menuList;
        }

        public String getButtonIcon() {
            return buttonIcon;
        }

        public void setButtonIcon(String buttonIcon) {
            this.buttonIcon = buttonIcon;
        }

        public boolean isButtonSwitch() {
            return buttonSwitch;
        }

        public void setButtonSwitch(boolean buttonSwitch) {
            this.buttonSwitch = buttonSwitch;
        }

        public String getGameIcon() {
            return gameIcon;
        }

        public void setGameIcon(String gameIcon) {
            this.gameIcon = gameIcon;
        }

        public FLUserInfo getUserInfo() {
            return userInfo;
        }

        public void setUserInfo(FLUserInfo userInfo) {
            this.userInfo = userInfo;
        }
    }

    public static class FLUserInfo{

        private String roleId;
        private String userId;
        private String roleName;
        private String gameName;
        private String gameCode;
        private String serverCode;
        private String serverName;


        public String getRoleId() {
            return roleId;
        }

        public void setRoleId(String roleId) {
            this.roleId = roleId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }

        public String getGameName() {
            return gameName;
        }

        public void setGameName(String gameName) {
            this.gameName = gameName;
        }

        public String getGameCode() {
            return gameCode;
        }

        public void setGameCode(String gameCode) {
            this.gameCode = gameCode;
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
    }
}

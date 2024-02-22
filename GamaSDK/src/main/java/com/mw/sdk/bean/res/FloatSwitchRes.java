package com.mw.sdk.bean.res;

import com.core.base.bean.BaseResponseModel;

import java.util.ArrayList;

/**
 * Created by ganyuanrong on 2017/2/23.
 */

public class FloatSwitchRes extends BaseResponseModel {

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

        private String roleId;
        private String userId;
        private String roleName;
        private String gameName;
        private String gameCode;
        private String serverCode;
        private String serverName;

        private ArrayList<MenuData> menuList;


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

        public ArrayList<MenuData> getMenuList() {
            return menuList;
        }

        public void setMenuList(ArrayList<MenuData> menuList) {
            this.menuList = menuList;
        }
    }

}

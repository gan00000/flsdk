package com.mw.sdk.utils;

public class DataManager {

    private static DataManager dataManager;

    public synchronized static DataManager getInstance(){
        if (dataManager == null){
            dataManager = new DataManager();
        }
        return dataManager;
    }

    private boolean isLogin = false;
    public boolean isLogin() {
        return isLogin;
    }
    public void setLogin(boolean login) {
        isLogin = login;
    }
}

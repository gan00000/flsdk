package com.mw.sdk.bean.res;

import java.util.ArrayList;

public class FloatConfigData {

    private boolean buttonSwitch;
    private String buttonIcon;
    private String gameIcon;
    private ArrayList<MenuData> menuList;

    public boolean isButtonSwitch() {
        return buttonSwitch;
    }

    public void setButtonSwitch(boolean buttonSwitch) {
        this.buttonSwitch = buttonSwitch;
    }

    public String getButtonIcon() {
        return buttonIcon;
    }

    public void setButtonIcon(String buttonIcon) {
        this.buttonIcon = buttonIcon;
    }

    public String getGameIcon() {
        return gameIcon;
    }

    public void setGameIcon(String gameIcon) {
        this.gameIcon = gameIcon;
    }

    public ArrayList<MenuData> getMenuList() {
        return menuList;
    }

    public void setMenuList(ArrayList<MenuData> menuList) {
        this.menuList = menuList;
    }
}

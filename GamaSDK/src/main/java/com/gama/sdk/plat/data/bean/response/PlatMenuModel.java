package com.gama.sdk.plat.data.bean.response;

/**
 * Created by gan on 2017/8/10.
 */

public class PlatMenuModel {

    private String itemId;
    private String name;
    private String url;//要打开的页面地址
    private String icon;//icon地址


    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}

package com.mw.sdk.out.bean;

import java.io.Serializable;

public class EventItemDetailBean implements Serializable {

//    [{item_id":"1","item_name":"钻石","item_num":100},
    private String item_id;
    private String item_name;
    private int item_num;

    private int change_num;
    private int after_num;

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public int getItem_num() {
        return item_num;
    }

    public void setItem_num(int item_num) {
        this.item_num = item_num;
    }


    public int getChange_num() {
        return change_num;
    }

    public void setChange_num(int change_num) {
        this.change_num = change_num;
    }

    public int getAfter_num() {
        return after_num;
    }

    public void setAfter_num(int after_num) {
        this.after_num = after_num;
    }
}

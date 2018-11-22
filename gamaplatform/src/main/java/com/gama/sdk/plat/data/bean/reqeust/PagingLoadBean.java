package com.gama.sdk.plat.data.bean.reqeust;

import android.content.Context;

import com.gama.base.bean.SGameBaseRequestBean;

/**
 * Created by gan on 2017/8/21.
 */

public class PagingLoadBean extends SGameBaseRequestBean {


    public PagingLoadBean(Context context) {
        super(context);
    }

    private int amount = 1;//页数

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void resetPage(){
        amount = 1;
    }

    public void increasePage(){
        amount++;
    }

    @Override
    public String toString() {
        return "PagingLoadBean{" +
                "amount=" + amount +
                '}';
    }
}

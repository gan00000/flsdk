package com.gama.sdk.plat.data.bean.response;

import com.core.base.bean.BaseResponseModel;

import java.util.ArrayList;

/**
 * Created by gan on 2017/8/10.
 */

public class PlatMenuAllModel extends BaseResponseModel {

    private String flag;

    private ArrayList<PlatMenuModel> data;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public ArrayList<PlatMenuModel> getData() {
        return data;
    }

    public void setData(ArrayList<PlatMenuModel> data) {
        this.data = data;
    }
}

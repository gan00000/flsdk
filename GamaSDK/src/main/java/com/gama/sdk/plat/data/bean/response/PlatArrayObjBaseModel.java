package com.gama.sdk.plat.data.bean.response;

import com.core.base.bean.BaseResponseModel;

import java.util.ArrayList;

/**
 * Created by gan on 2017/8/16.
 */

public class PlatArrayObjBaseModel<T> extends BaseResponseModel {

    private ArrayList<T> data;

    public ArrayList<T> getData() {
        return data;
    }

    public void setData(ArrayList<T> data) {
        this.data = data;
    }
}

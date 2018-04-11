package com.gama.sdk.plat.data.bean.response;

import com.core.base.bean.BaseResponseModel;

/**
 * Created by gan on 2017/8/16.
 */

public class PlatObjBaseModel<T> extends BaseResponseModel {

    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

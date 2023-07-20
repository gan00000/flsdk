package com.mybase.request;

import com.mybase.bean.BaseReqModel;

/**
 * Created by gan on 2017/8/29.
 */

public class SimpleHttpRequest extends AbsHttpRequest {

    private BaseReqModel baseReqeustBean;

    public BaseReqModel getBaseReqeustBean() {
        return baseReqeustBean;
    }

    public void setBaseReqeustBean(BaseReqModel baseReqeustBean) {
        this.baseReqeustBean = baseReqeustBean;
    }


    @Override
    public BaseReqModel createRequestBean() {
        return baseReqeustBean;
    }
}

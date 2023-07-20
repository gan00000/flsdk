package com.mybase.request;

import android.content.Context;

import com.mybase.bean.BaseReqModel;

public class CfgFileRequest extends AbsHttpRequest {

    private Context context;
   private BaseReqModel baseReqeustBean;

    public CfgFileRequest(Context context) {
        this.context = context;
        setGetMethod(true,false);
    }


    @Override
    public BaseReqModel createRequestBean() {
        return baseReqeustBean;
    }

    @Override
    public <T> void onHttpSucceess(T responseModel) {

    }

    @Override
    public void onTimeout(String result) {

    }

    @Override
    public void onNoData(String result) {

    }

    public void setBaseReqeustBean(BaseReqModel baseReqeustBean) {
        this.baseReqeustBean = baseReqeustBean;
    }
}

package com.gamamobi.onestore.pay.task;

import android.content.Context;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.request.AbsHttpRequest;

/**
 * Created by ganyuanrong on 2017/2/20.
 */

public class OneStoreExchangeReqTask extends AbsHttpRequest {

    private Context activity;
    private BaseReqeustBean baseReqeustBean;


    public OneStoreExchangeReqTask(Context activity, BaseReqeustBean baseReqeustBean) {
        this.activity = activity;
        this.baseReqeustBean = baseReqeustBean;
    }

    @Override
    public BaseReqeustBean createRequestBean() {


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
}

package com.gama.pay.gp.task;

import android.app.Activity;

import com.core.base.request.AbsHttpRequest;
import com.core.base.bean.BaseReqeustBean;

/**
 * Created by ganyuanrong on 2017/2/20.
 */

public class GoogleExchangeReqTask extends AbsHttpRequest {

    private Activity activity;
    private BaseReqeustBean baseReqeustBean;


    public GoogleExchangeReqTask(Activity activity, BaseReqeustBean baseReqeustBean) {
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

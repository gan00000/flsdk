package com.ldy.sdk.pay.gp.task;

import android.content.Context;

import com.mybase.request.AbsHttpRequest;
import com.mybase.bean.BaseReqModel;
import com.mybase.utils.SStringUtil;
import com.ldy.sdk.pay.gp.req.GoogleExchangeReqBean;
import com.ldy.base.cfg.ResConfig;

/**
 * Created by ganyuanrong on 2017/2/20.
 */

public class GoogleExchangeReqTask extends AbsHttpRequest {

    private Context activity;
    private GoogleExchangeReqBean baseReqeustBean;


    public GoogleExchangeReqTask(Context activity, GoogleExchangeReqBean baseReqeustBean) {
        this.activity = activity;
        this.baseReqeustBean = baseReqeustBean;
    }

    @Override
    public BaseReqModel createRequestBean() {

        baseReqeustBean.setSignature(SStringUtil.toMd5(ResConfig.getAppKey(activity)
                + baseReqeustBean.getGameCode()
                +baseReqeustBean.getUserId()
                + baseReqeustBean.getTimestamp()));
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

package com.mw.sdk.api.task;

import android.content.Context;

import com.core.base.request.AbsHttpRequest;
import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.SStringUtil;
import com.mw.sdk.bean.req.PayExchangeReqBean;
import com.mw.sdk.utils.ResConfig;

/**
 * Created by ganyuanrong on 2017/2/20.
 */

public class GoogleExchangeReqTask extends AbsHttpRequest {

    private Context activity;
    private PayExchangeReqBean baseReqeustBean;


    public GoogleExchangeReqTask(Context activity, PayExchangeReqBean baseReqeustBean) {
        this.activity = activity;
        this.baseReqeustBean = baseReqeustBean;
    }

    @Override
    public BaseReqeustBean createRequestBean() {

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

package com.ldy.sdk.pay.gp.task;

import android.content.Context;

import com.mybase.bean.BaseReqModel;
import com.mybase.request.AbsHttpRequest;
import com.mybase.utils.SStringUtil;
import com.mybase.utils.ToastUtils;
import com.ldy.sdk.pay.gp.req.GooglePayCreateOrderIdReqBean;
import com.ldy.base.cfg.ResConfig;
import com.ldy.sdk.R;

/**
 * Created by gan on 2017/2/23.
 */

public class GoogleCreateOrderReqTask extends AbsHttpRequest {

    private Context context;
    private GooglePayCreateOrderIdReqBean createOrderIdReqBean;

    public GoogleCreateOrderReqTask(Context context, GooglePayCreateOrderIdReqBean createOrderIdReqBean) {
        this.context = context;
        this.createOrderIdReqBean = createOrderIdReqBean;
    }


    @Override
    public BaseReqModel createRequestBean() {

        createOrderIdReqBean.setSignature(SStringUtil.toMd5(ResConfig.getAppKey(context)
                + createOrderIdReqBean.getGameCode()
                +createOrderIdReqBean.getUserId()
                + createOrderIdReqBean.getTimestamp()));
        return createOrderIdReqBean;
    }

    @Override
    public <T> void onHttpSucceess(T responseModel) {

    }

    @Override
    public void onTimeout(String result) {
        ToastUtils.toast(context, R.string.mstr_palltodayry_pulchrster);
    }

    @Override
    public void onNoData(String result) {

    }
}

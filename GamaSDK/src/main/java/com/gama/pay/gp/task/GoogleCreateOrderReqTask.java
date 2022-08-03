package com.gama.pay.gp.task;

import android.content.Context;

import com.core.base.request.AbsHttpRequest;
import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.gama.pay.gp.bean.req.GooglePayCreateOrderIdReqBean;
import com.mw.base.cfg.ResConfig;
import com.mw.sdk.R;

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
    public BaseReqeustBean createRequestBean() {

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
        ToastUtils.toast(context, R.string.py_error_occur);
    }

    @Override
    public void onNoData(String result) {

    }
}

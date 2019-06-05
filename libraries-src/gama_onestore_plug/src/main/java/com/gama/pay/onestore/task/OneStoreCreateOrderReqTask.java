package com.gama.pay.onestore.task;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.request.AbsHttpRequest;
import com.gama.pay.onestore.bean.req.OneStoreCreateOrderIdReqBean;

/**
 * Created by gan on 2017/2/23.
 */

public class OneStoreCreateOrderReqTask extends AbsHttpRequest {

    private OneStoreCreateOrderIdReqBean createOrderIdReqBean;

    public OneStoreCreateOrderReqTask(OneStoreCreateOrderIdReqBean createOrderIdReqBean) {
        this.createOrderIdReqBean = createOrderIdReqBean;
    }


    @Override
    public BaseReqeustBean createRequestBean() {
        return createOrderIdReqBean;
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

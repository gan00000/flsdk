package com.mw.base.excute;

import android.content.Context;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.PL;
import com.mw.base.bean.GamaOnlineRequestBean;
import com.mw.base.cfg.ResConfig;
import com.mw.base.constant.RequestDomain;

public class GamaOnlineRequestTask extends GamaBaseRestRequestTask {

    private static final String TAG = GamaOnlineRequestTask.class.getSimpleName();
    private Context context;
    private GamaOnlineRequestBean requestBean;

    public GamaOnlineRequestTask(Context context, GamaOnlineRequestBean bean) {
        if (bean == null) {
            PL.e(TAG, "request bean is empty");
            return;
        }
        this.context = context;
        //指明使用Get请求
        setGetMethod(true, true);

        requestBean = bean;
        requestBean.setRequestUrl(ResConfig.getAdsPreferredUrl(context));
        requestBean.setRequestSpaUrl(ResConfig.getAdsSpareUrl(context));
        requestBean.setRequestMethod(RequestDomain.ONLINE_TIME);
    }

    @Override
    public BaseReqeustBean createRequestBean() {
        return requestBean;
    }


}

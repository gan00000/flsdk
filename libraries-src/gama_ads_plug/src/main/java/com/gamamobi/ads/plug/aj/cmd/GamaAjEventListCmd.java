package com.gamamobi.ads.plug.aj.cmd;

import android.content.Context;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.request.AbsHttpRequest;
import com.gama.base.cfg.ResConfig;
import com.gamamobi.ads.plug.aj.constant.GamaAjConstant;

public class GamaAjEventListCmd extends AbsHttpRequest {
    private Context context;

    public GamaAjEventListCmd(Context context) {
        this.context = context;
        setGetMethod(true, false);
    }

    @Override
    public BaseReqeustBean createRequestBean() {
        BaseReqeustBean baseReqeustBean = new BaseReqeustBean();
        baseReqeustBean.setCompleteUrl(ResConfig.getAdsPreferredUrl(context) + GamaAjConstant.GAMA_AJ_METHOD + ResConfig.getGameCode(context) + ".txt");
        return baseReqeustBean;
    }
}

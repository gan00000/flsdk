package com.gama.base.excute;

import android.content.Context;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.request.AbsHttpRequest;
import com.gama.base.cfg.ResConfig;

public class GsCommonSwitchTask extends AbsHttpRequest {
    private Context context;

    public GsCommonSwitchTask(Context context) {
        this.context = context;
        setGetMethod(true, false);
    }

    @Override
    public BaseReqeustBean createRequestBean() {
        BaseReqeustBean baseReqeustBean = new BaseReqeustBean();
        baseReqeustBean.setCompleteUrl(String.format(ResConfig.getCommonSwitchUrl(context), ResConfig.getGameCode(context)));
        return baseReqeustBean;
    }
}

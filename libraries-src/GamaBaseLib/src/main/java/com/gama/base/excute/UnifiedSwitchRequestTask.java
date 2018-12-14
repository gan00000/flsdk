package com.gama.base.excute;

import android.content.Context;
import android.text.TextUtils;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.request.AbsHttpRequest;
import com.core.base.utils.PL;
import com.gama.base.bean.unify.UnifiedSwitchRequestBean;
import com.gama.base.cfg.ResConfig;
import com.gama.base.constant.RequestDomain;

public class UnifiedSwitchRequestTask extends AbsHttpRequest {

    private static final String TAG = UnifiedSwitchRequestTask.class.getSimpleName();
    private Context context;
    private UnifiedSwitchRequestBean reqeustBean;

    public UnifiedSwitchRequestTask(Context context, String type) {
        if(TextUtils.isEmpty(type)) {
            PL.e(TAG, "type is null");
            return;
        }
        this.context = context;
        //指明使用Get请求，以及需要拼写参数
        setGetMethod(true, true);

        reqeustBean = new UnifiedSwitchRequestBean(context);
        String completeUrl = ResConfig.getPlatPreferredUrl(context) + RequestDomain.UNIFIED_SWITCH;
        reqeustBean.setCompleteUrl(completeUrl);
        reqeustBean.setType(type);
    }

    @Override
    public BaseReqeustBean createRequestBean() {

        return reqeustBean;
    }


}

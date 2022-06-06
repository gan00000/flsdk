package com.mw.base.excute;

import android.content.Context;
import android.text.TextUtils;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.PL;
import com.mw.base.cfg.ResConfig;
import com.mw.base.constant.RequestDomain;
import com.mw.base.bean.unify.UnifiedSwitchRequestBean;
import com.mw.base.utils.GamaUtil;

public class UnifiedSwitchRequestTask extends GamaBaseRestRequestTask {

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
        String completeUrl = "";
        if (GamaUtil.isInterfaceSurfixWithApp(context)) {
            completeUrl = ResConfig.getPlatPreferredUrl(context) + RequestDomain.UNIFIED_SWITCH_APP;
        } else {
            completeUrl = ResConfig.getPlatPreferredUrl(context) + RequestDomain.UNIFIED_SWITCH;
        }
        reqeustBean.setCompleteUrl(completeUrl);
        reqeustBean.setType(type);
    }

    @Override
    public BaseReqeustBean createRequestBean() {

        return reqeustBean;
    }


}

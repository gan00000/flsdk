package com.gama.base.excute;

import android.content.Context;
import android.text.TextUtils;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.PL;
import com.gama.base.bean.unify.UnifiedSwitchRequestBean;
import com.gama.base.cfg.ResConfig;
import com.gama.base.constant.RequestDomain;

public class GamaAreaInfoRequestTask extends GamaBaseRestRequestTask {

    private static final String TAG = GamaAreaInfoRequestTask.class.getSimpleName();
    private Context mContext;
    private BaseReqeustBean reqeustBean;

    public GamaAreaInfoRequestTask(Context context) {
        this.mContext = context;
        //指明使用Get请求，以及需要拼写参数
        setGetMethod(true, false);

    }

    @Override
    public BaseReqeustBean createRequestBean() {
        reqeustBean = new BaseReqeustBean();
        reqeustBean.setCompleteUrl(ResConfig.getAreaCodeUrl(mContext));
        return reqeustBean;
    }


}

package com.gama.data.login.execute;

import android.content.Context;

import com.core.base.bean.BaseReqeustBean;
import com.gama.base.bean.SSdkBaseRequestBean;
import com.gama.base.cfg.ResConfig;
import com.gama.base.excute.GamaBaseRestRequestTask;
import com.gama.base.utils.GamaUtil;

public class GamaVfcodeSwitchRequestTask extends GamaBaseRestRequestTask {

    private static final String TAG = GamaVfcodeSwitchRequestTask.class.getSimpleName();
    private Context mContext;
    private SSdkBaseRequestBean reqeustBean;

    public GamaVfcodeSwitchRequestTask(Context context) {
        this.mContext = context;
        //指明使用Get请求，以及需要拼写参数
        setGetMethod(true, false);

    }

    @Override
    public BaseReqeustBean createRequestBean() {
        //https://config.gamesword.com/gottw/base/V1/config.json
        reqeustBean = new SSdkBaseRequestBean(mContext);
        reqeustBean.setCompleteUrl(String.format(ResConfig.getVfCodeSwitchUrl(mContext), reqeustBean.getGameCode()));
        return reqeustBean;
    }


}

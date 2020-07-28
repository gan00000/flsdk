package com.flyfun.base.excute;

import android.content.Context;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.GamaTimeUtil;
import com.core.base.utils.PL;
import com.flyfun.base.cfg.ResConfig;
import com.flyfun.base.constant.RequestDomain;
import com.flyfun.base.bean.restful.GamaRestfulRequestBean;
import com.flyfun.base.utils.GamaUtil;
import com.google.gson.Gson;

import java.util.Map;

public class GamaRoleInfoRequestTask extends GamaBaseRestRequestTask {

    private static final String TAG = GamaRoleInfoRequestTask.class.getSimpleName();
    private Context context;
    private GamaRestfulRequestBean requestBean;
    private String url = "userId/gameCode/packageName/beijingTime/localTime?";

    //https://ads.gamamobi.com/separateServer/{userId}/{gameCode}/{packageName}/{beijingTime}/{localTime}?loingServerSignature
    //separateServer/{userId}/{gameCode}/{packageName}/{beijingTime}/{localTime}?logingServerSignature=xxx

    public GamaRoleInfoRequestTask(Context context, Map<String, Object> map) {
        if (map == null) {
            PL.e(TAG, "role info is null");
            return;
        }
        this.context = context;
        //指明使用Get请求
        setGetMethod(true, true);

        requestBean = new GamaRestfulRequestBean(context);

        String paramsJson = new Gson().toJson(map);
        String userId = GamaUtil.getUid(context);
        String gameCode = requestBean.getGameCode();
        String packageName = context.getPackageName();
        String beijingTime = GamaTimeUtil.getBeiJingTime(context);
        String localTime = GamaTimeUtil.getDisplayTime(context);
        String signature = GamaUtil.getSdkAccessToken(context);

        String requestDomain = ResConfig.getAdsPreferredUrl(context) + RequestDomain.SEPARATE_SERVER;
        url = requestDomain + url;
        url = url.replace("userId", userId)
                .replace("gameCode", gameCode)
                .replace("packageName", packageName)
                .replace("beijingTime", encode2Utf8(beijingTime))
                .replace("localTime", encode2Utf8(localTime));

        PL.i(TAG, "completeUrl = " + url);
        requestBean.setCompleteUrl(url);
        requestBean.setLogingServerSignature(signature);
        requestBean.setJson(paramsJson);
    }

    @Override
    public BaseReqeustBean createRequestBean() {
        return requestBean;
    }


}

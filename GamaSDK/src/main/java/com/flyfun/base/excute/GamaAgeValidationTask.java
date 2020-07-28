package com.flyfun.base.excute;

import android.content.Context;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.flyfun.base.cfg.ResConfig;
import com.flyfun.base.constant.RequestDomain;
import com.flyfun.base.bean.restful.GamaRestfulRequestBean;
import com.flyfun.base.utils.GamaUtil;

public class GamaAgeValidationTask extends GamaBaseRestRequestTask {

    private static final String TAG = GamaAgeValidationTask.class.getSimpleName();
    private Context context;
    private GamaRestfulRequestBean requestBean;
    private String url = "userId/gameCode/packageName/timestamp/signature?";

    //:validation/ageValidation/{userId}/{gameCode}/{packageName}/{timestamp}/{signature}?loingServerSignature=test&gameLanguage=ja-JP

    public GamaAgeValidationTask(Context context, GamaRestfulRequestBean bean) {
        if (bean == null) {
            PL.e(TAG, "requestbean is null");
            return;
        }
        this.context = context;
        this.requestBean = bean;
        //指明使用Get请求
        setGetMethod(true, true);

        requestBean = new GamaRestfulRequestBean(context);

        String userId = GamaUtil.getUid(context);
        String gameCode = requestBean.getGameCode();
        String packageName = context.getPackageName();
        String timeStamp = System.currentTimeMillis() + "";
        //(AppKey+userId + gameCode + timestamp)md5小写
        String signature = SStringUtil.toMd5(ResConfig.getAppKey(context) + userId + gameCode + timeStamp);

        String requestDomain = "";
        if (GamaUtil.isInterfaceSurfixWithApp(context)) {
            requestDomain = ResConfig.getPayPreferredUrl(context) + RequestDomain.AGE_VALIDATION_APP;
        } else {
            requestDomain = ResConfig.getPayPreferredUrl(context) + RequestDomain.AGE_VALIDATION;
        }
        url = requestDomain + url;
        url = url.replace("userId", userId)
                .replace("gameCode", gameCode)
                .replace("packageName", packageName)
                .replace("timestamp", timeStamp)
                .replace("signature", signature);

        PL.i(TAG, "completeUrl = " + url);
        requestBean.setCompleteUrl(url);
        requestBean.setLogingServerSignature(signature);

    }

    @Override
    public BaseReqeustBean createRequestBean() {
        return requestBean;
    }


}

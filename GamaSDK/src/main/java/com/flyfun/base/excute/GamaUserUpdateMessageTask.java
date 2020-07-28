package com.flyfun.base.excute;

import android.content.Context;
import android.text.TextUtils;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.flyfun.base.cfg.ResConfig;
import com.flyfun.base.constant.RequestDomain;
import com.flyfun.base.bean.restful.GamaRestfulRequestBean;
import com.flyfun.base.utils.GamaUtil;

public class GamaUserUpdateMessageTask extends GamaBaseRestRequestTask {

    private static final String TAG = GamaUserUpdateMessageTask.class.getSimpleName();
    private Context context;
    private GamaRestfulRequestBean requestBean;
    private String url = "userId/gameCode/packageName/timestamp/signature?";

    //userUpdateMessage/updateMessage/{userId}/{gameCode}/{packageName}/{timestamp}/{signature}?loingServerSignature=登陆成功后的签名验证&gameLanguage=语言

    public GamaUserUpdateMessageTask(Context context, GamaRestfulRequestBean bean) {
        if (bean == null || TextUtils.isEmpty(bean.getAge())) {
            PL.e(TAG, "requestbean is null or age not set");
            return;
        }
        this.context = context;
        this.requestBean = bean;
        //指明使用Get请求
        setGetMethod(true, true);

        String userId = GamaUtil.getUid(context);
        String gameCode = requestBean.getGameCode();
        String packageName = context.getPackageName();
        String timeStamp = System.currentTimeMillis() + "";
        //(AppKey+userId + gameCode + timestamp)md5小写
        String signature = SStringUtil.toMd5(ResConfig.getAppKey(context) + userId + gameCode + timeStamp);

        String requestDomain = "";
        if (GamaUtil.isInterfaceSurfixWithApp(context)) {
            requestDomain = ResConfig.getLoginPreferredUrl(context) + RequestDomain.USER_UPDATE_MESSAGE_APP;
        } else {
            requestDomain = ResConfig.getLoginPreferredUrl(context) + RequestDomain.USER_UPDATE_MESSAGE;
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

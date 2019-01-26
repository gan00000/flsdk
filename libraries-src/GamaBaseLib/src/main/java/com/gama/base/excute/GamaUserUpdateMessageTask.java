package com.gama.base.excute;

import android.content.Context;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.gama.base.bean.restful.GamaRestfulRequestBean;
import com.gama.base.cfg.ResConfig;
import com.gama.base.constant.RequestDomain;
import com.gama.base.utils.GamaUtil;

public class GamaUserUpdateMessageTask extends GamaBaseRestRequestTask {

    private static final String TAG = GamaUserUpdateMessageTask.class.getSimpleName();
    private Context context;
    private GamaRestfulRequestBean requestBean;
    private String url = "userId/gameCode/packageName/timestamp/signature?";

    //userUpdateMessage/updateMessage/{userId}/{gameCode}/{packageName}/{timestamp}/{signature}?loingServerSignature=登陆成功后的签名验证&gameLanguage=语言

    public GamaUserUpdateMessageTask(Context context, GamaRestfulRequestBean bean) {
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

        String requestDomain = ResConfig.getAdsPreferredUrl(context) + RequestDomain.USER_UPDATE_MESSAGE;
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

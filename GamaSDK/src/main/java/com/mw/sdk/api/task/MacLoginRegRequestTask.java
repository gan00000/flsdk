package com.mw.sdk.api.task;

import android.content.Context;
import android.text.TextUtils;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.mw.sdk.constant.SLoginType;
import com.mw.sdk.utils.ResConfig;
import com.mw.sdk.constant.ApiRequestMethod;
import com.mw.sdk.bean.req.MacLoginRegRequestBean;
import com.mw.sdk.utils.SdkUtil;

/**
 * <p>Title: MacLoginRegRequestTask</p> <p>Description: 新三方登陆&注册接口</p> <p>Company:GanYuanrong</p>
 *
 * @author GanYuanrong
 * @date 2014年9月16日
 */
public class MacLoginRegRequestTask extends BaseLoginRequestTask {

    MacLoginRegRequestBean macLoginRegRequestBean;
    public MacLoginRegRequestTask(Context context) {
        super(context);

        macLoginRegRequestBean = new MacLoginRegRequestBean(context);

        sdkBaseRequestBean = macLoginRegRequestBean;

        macLoginRegRequestBean.setRegistPlatform(SLoginType.LOGIN_TYPE_GUEST);
        //生成免注册登入账密
        String uniqueId = SdkUtil.getSdkUniqueId(context);
        if(TextUtils.isEmpty(uniqueId)){
            PL.d("uniqueId:" + uniqueId);
            return;
        }
        macLoginRegRequestBean.setUniqueId(uniqueId);

        macLoginRegRequestBean.setRequestMethod(ApiRequestMethod.GS_REQUEST_METHOD_FREE_LOGIN);
    }

    @Override
    public BaseReqeustBean createRequestBean() {
        super.createRequestBean();
        macLoginRegRequestBean.setSignature(SStringUtil.toMd5(ResConfig.getAppKey(context) + macLoginRegRequestBean.getTimestamp() +
                macLoginRegRequestBean.getUniqueId() + macLoginRegRequestBean.getGameCode()));
        return macLoginRegRequestBean;
    }
}
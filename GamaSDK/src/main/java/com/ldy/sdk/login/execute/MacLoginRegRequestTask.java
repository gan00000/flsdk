package com.ldy.sdk.login.execute;

import android.content.Context;
import android.text.TextUtils;

import com.mybase.bean.BaseReqeustBean;
import com.mybase.utils.PL;
import com.mybase.utils.SStringUtil;
import com.ldy.base.bean.SLoginType;
import com.ldy.base.cfg.ResConfig;
import com.ldy.sdk.constant.ApiRequestMethod;
import com.ldy.sdk.login.model.request.MacLoginRegRequestBean;
import com.ldy.base.utils.SdkUtil;

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

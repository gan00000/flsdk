package com.gama.data.login.execute2;

import android.content.Context;
import android.text.TextUtils;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.gama.base.bean.SLoginType;
import com.gama.base.utils.GamaUtil;
import com.gama.data.login.constant.GSRequestMethod;
import com.gama.data.login.constant.GamaRequestMethod;
import com.gama.data.login.execute.BaseLoginRequestTask;
import com.gama.data.login.request.ThirdAccountBindRequestBean;

/**
 * <p>Title: 三方綁定</p>
 *
 * @author HuangShaoGuang
 * @date 2019年11月21日
 */
public class ThirdAccountToBindRequestTaskV2 extends BaseLoginRequestTask {

    ThirdAccountBindRequestBean thirdAccountBindRequestBean;

    /**
     * 免注册绑Google
     */
    public ThirdAccountToBindRequestTaskV2(Context context,
                                           String thirdPlatId,
                                           String googleIdToken,
                                           String googleClientId,
                                           GSRequestMethod.GSRequestType requestMethod) {
        super(context);

        if(TextUtils.isEmpty(thirdPlatId)){
            PL.d("thirdPlatId:" + thirdPlatId);
            return;
        }

        thirdAccountBindRequestBean = new ThirdAccountBindRequestBean(context);

        sdkBaseRequestBean = thirdAccountBindRequestBean;

        thirdAccountBindRequestBean.setRegistPlatform(SLoginType.LOGIN_TYPE_GOOGLE);
        thirdAccountBindRequestBean.setThirdPlatId(thirdPlatId);
        thirdAccountBindRequestBean.setGoogleIdToken(googleIdToken);
        thirdAccountBindRequestBean.setGoogleClientId(googleClientId);

        thirdAccountBindRequestBean.setRequestMethod(GSRequestMethod.GS_REQUEST_METHOD_BIND_TO_THIRD);
    }

    @Override
    public BaseReqeustBean createRequestBean() {
        super.createRequestBean();

        //AppKey+ timestamp + gameCode + thirdPlatId + uniqueId
        thirdAccountBindRequestBean.setSignature(SStringUtil.toMd5(thirdAccountBindRequestBean.getAppKey()
                + thirdAccountBindRequestBean.getTimestamp()
                + thirdAccountBindRequestBean.getGameCode()
                + thirdAccountBindRequestBean.getThirdPlatId()
                + thirdAccountBindRequestBean.getUniqueId()));
        return thirdAccountBindRequestBean;
    }
}

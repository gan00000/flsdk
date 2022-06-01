package com.flyfun.data.login.execute;

import android.content.Context;
import android.text.TextUtils;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.flyfun.base.bean.SLoginType;
import com.flyfun.data.login.constant.GSRequestMethod;
import com.flyfun.data.login.constant.GamaRequestMethod;
import com.flyfun.data.login.request.MacLoginRegRequestBean;
import com.flyfun.base.utils.GamaUtil;

/**
 * <p>Title: MacLoginRegRequestTask</p> <p>Description: 新三方登陆&注册接口</p> <p>Company:GanYuanrong</p>
 *
 * @author GanYuanrong
 * @date 2014年9月16日
 */
public class MacLoginRegRequestTask extends BaseLoginRequestTask {

    MacLoginRegRequestBean macLoginRegRequestBean;
    public MacLoginRegRequestTask(Context context, GSRequestMethod.GSRequestType requestMethod) {
        super(context);

        macLoginRegRequestBean = new MacLoginRegRequestBean(context);

        sdkBaseRequestBean = macLoginRegRequestBean;

        macLoginRegRequestBean.setRegistPlatform(SLoginType.LOGIN_TYPE_UNIQUE);
        //生成免注册登入账密
        String uniqueId = GamaUtil.getGoogleAdid1AndroidId(context);
        if(TextUtils.isEmpty(uniqueId)){
            PL.d("uniqueId:" + uniqueId);
            return;
        }
        macLoginRegRequestBean.setUniqueId(uniqueId);

        if(requestMethod == GSRequestMethod.GSRequestType.GAMESWORD) {
            macLoginRegRequestBean.setRequestMethod(GSRequestMethod.GS_REQUEST_METHOD_FREE_LOGIN);
        } else if (requestMethod == GSRequestMethod.GSRequestType.GAMAMOBI) {
            macLoginRegRequestBean.setRequestMethod(GamaRequestMethod.GAMA_REQUEST_METHOD_FREE_LOGIN);
        }
    }

    @Override
    public BaseReqeustBean createRequestBean() {
        super.createRequestBean();
        macLoginRegRequestBean.setSignature(SStringUtil.toMd5(macLoginRegRequestBean.getAppKey() + macLoginRegRequestBean.getTimestamp() +
                macLoginRegRequestBean.getUniqueId() + macLoginRegRequestBean.getGameCode()));
        return macLoginRegRequestBean;
    }
}

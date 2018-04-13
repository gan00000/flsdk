package com.gama.data.login.execute;

import android.content.Context;
import android.text.TextUtils;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.gama.base.bean.SLoginType;
import com.gama.base.utils.StarPyUtil;
import com.gama.data.login.request.MacLoginRegRequestBean;

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

        macLoginRegRequestBean.setRegistPlatform(SLoginType.LOGIN_TYPE_UNIQUE);
        //生成免注册登入账密
        String uniqueId = StarPyUtil.getCustomizedUniqueId1AndroidId1Adid(context);
        if(TextUtils.isEmpty(uniqueId)){
            PL.d("uniqueId:" + uniqueId);
            return;
        }
        macLoginRegRequestBean.setUniqueId(uniqueId);

        macLoginRegRequestBean.setRequestMethod("freeRegister");


    }

    @Override
    public BaseReqeustBean createRequestBean() {
        super.createRequestBean();
        macLoginRegRequestBean.setSignature(SStringUtil.toMd5(macLoginRegRequestBean.getAppKey() + macLoginRegRequestBean.getTimestamp() +
                macLoginRegRequestBean.getUniqueId() + macLoginRegRequestBean.getGameCode()));
        return macLoginRegRequestBean;
    }
}

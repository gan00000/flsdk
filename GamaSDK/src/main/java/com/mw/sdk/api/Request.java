package com.mw.sdk.api;

import android.app.Activity;
import android.content.Context;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.bean.BaseResponseModel;
import com.core.base.callback.ISReqCallBack;
import com.core.base.callback.SFCallBack;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.mw.base.bean.SGameBaseRequestBean;
import com.mw.base.bean.SLoginType;
import com.mw.base.cfg.ResConfig;
import com.mw.base.utils.SdkUtil;
import com.mw.sdk.R;
import com.mw.sdk.login.constant.ApiRequestMethod;
import com.mw.sdk.login.execute.BaseLoginRequestTask;
import com.mw.sdk.login.execute.ThirdAccountBindRequestTaskV2;
import com.mw.sdk.login.model.request.AccountBindInGameRequestBean;
import com.mw.sdk.login.model.response.SLoginResponse;
import com.mw.sdk.out.ISdkCallBack;
import com.mw.sdk.utils.DialogUtil;

public class Request {

    public static void sendVfCode(Context context, boolean needDialog, String areaCode, String telephone, SFCallBack<BaseResponseModel> iSdkCallBack) {

        SGameBaseRequestBean sGameBaseRequestBean = new SGameBaseRequestBean(context);
        sGameBaseRequestBean.setPhone(telephone);
        sGameBaseRequestBean.setPhoneAreaCode(areaCode);
        sGameBaseRequestBean.setRequestMethod(ApiRequestMethod.api_sendMobileVcode);
        BaseLoginRequestTask baseLoginRequestTask = new BaseLoginRequestTask(context);
        baseLoginRequestTask.setSdkBaseRequestBean(sGameBaseRequestBean);

        if (needDialog) {
            baseLoginRequestTask.setLoadDialog(DialogUtil.createLoadingDialog(context, "Loading..."));
        }
        baseLoginRequestTask.setReqCallBack(new ISReqCallBack<BaseResponseModel>() {
            @Override
            public void success(BaseResponseModel responseModel, String rawResult) {
                if (responseModel != null) {
                    if (responseModel.isRequestSuccess()) {
                        if (iSdkCallBack != null){
                            iSdkCallBack.success(responseModel,rawResult);
                        }

                    }else{

//                        ToastUtils.toast(context, responseModel.getMessage() + "");
                        if (iSdkCallBack != null){
                            iSdkCallBack.fail(responseModel,rawResult);
                        }
                    }

                } else {
                    ToastUtils.toast(context, R.string.py_error_occur);
//                    if (iSdkCallBack != null){
//                        iSdkCallBack.fail(null,rawResult);
//                    }
                }
            }

            @Override
            public void timeout(String code) {
                ToastUtils.toast(context, R.string.py_error_occur);
//                if (iSdkCallBack != null){
//                    iSdkCallBack.fail(null,"");
//                }
            }

            @Override
            public void noData() {
                ToastUtils.toast(context, R.string.py_error_occur);
//                if (iSdkCallBack != null){
//                    iSdkCallBack.fail(null,"");
//                }
            }

            @Override
            public void cancel() {}

        });
        baseLoginRequestTask.excute(BaseResponseModel.class);
    }

    public static void bindPhone(Context context, boolean needDialog, String areaCode, String telephone,String vfCode, SFCallBack<SLoginResponse> sfCallBack) {

        SGameBaseRequestBean sGameBaseRequestBean = new SGameBaseRequestBean(context);
        sGameBaseRequestBean.setPhone(telephone);
        sGameBaseRequestBean.setPhoneAreaCode(areaCode);
        sGameBaseRequestBean.setvCode(vfCode);
        sGameBaseRequestBean.setRequestMethod(ApiRequestMethod.api_mobile_bind);
        BaseLoginRequestTask baseLoginRequestTask = new BaseLoginRequestTask(context);
        baseLoginRequestTask.setSdkBaseRequestBean(sGameBaseRequestBean);

        if (needDialog) {
            baseLoginRequestTask.setLoadDialog(DialogUtil.createLoadingDialog(context, "Loading..."));
        }
        baseLoginRequestTask.setReqCallBack(new ISReqCallBack<BaseResponseModel>() {
            @Override
            public void success(BaseResponseModel responseModel, String rawResult) {
                if (responseModel != null) {
                    if (responseModel.isRequestSuccess()) {

                        SLoginResponse localLoginResponse = SdkUtil.getCurrentUserLoginResponse(context);
                        localLoginResponse.getData().setTelephone(areaCode + "-" + telephone);
                        localLoginResponse.getData().setBindPhone(true);
                        SdkUtil.updateLoginData(context, localLoginResponse);

                        if (sfCallBack != null){
                            sfCallBack.success(localLoginResponse,rawResult);
                        }

                    }else{

//                        ToastUtils.toast(context, responseModel.getMessage() + "");
                        SLoginResponse tempLoginResponse = new SLoginResponse();
                        tempLoginResponse.setCode(responseModel.getCode());
                        tempLoginResponse.setRawResponse(responseModel.getRawResponse());
                        tempLoginResponse.setMessage(responseModel.getMessage());

                        if (sfCallBack != null){
                            sfCallBack.fail(tempLoginResponse,rawResult);
                        }
                    }

                } else {
                    ToastUtils.toast(context, R.string.py_error_occur);
//                    if (sfCallBack != null){
//                        sfCallBack.fail(null,rawResult);
//                    }
                }
            }

            @Override
            public void timeout(String code) {
                ToastUtils.toast(context, R.string.py_error_occur);
//                if (sfCallBack != null){
//                    sfCallBack.fail(null,"");
//                }
            }

            @Override
            public void noData() {
                ToastUtils.toast(context, R.string.py_error_occur);
//                if (sfCallBack != null){
//                    sfCallBack.fail(null,"");
//                }
            }

            @Override
            public void cancel() {}

        });
        baseLoginRequestTask.excute(BaseResponseModel.class);
    }

    public static void bindAcountInGame(Context context, boolean needDialog, String loginType,String name, String pwd, SFCallBack<SLoginResponse> sfCallBack) {

        AccountBindInGameRequestBean bindInGameRequestBean = new AccountBindInGameRequestBean(context);
        bindInGameRequestBean.setRegistPlatform(loginType);
        bindInGameRequestBean.setName(name.toLowerCase());
        bindInGameRequestBean.setPwd(SStringUtil.toMd5(pwd));

        bindInGameRequestBean.setRequestMethod(ApiRequestMethod.GS_REQUEST_METHOD_BIND);

        bindInGameRequestBean.setSignature(SStringUtil.toMd5(
                ResConfig.getAppKey(context)
                        + bindInGameRequestBean.getTimestamp()
                        + bindInGameRequestBean.getName()
                        + bindInGameRequestBean.getGameCode()));

        BaseLoginRequestTask baseLoginRequestTask = new BaseLoginRequestTask(context);
        baseLoginRequestTask.setSdkBaseRequestBean(bindInGameRequestBean);

        if (needDialog) {
            baseLoginRequestTask.setLoadDialog(DialogUtil.createLoadingDialog(context, "Loading..."));
        }
        baseLoginRequestTask.setReqCallBack(new ISReqCallBack<SLoginResponse>() {
            @Override
            public void success(SLoginResponse sLoginResponse, String rawResult) {
                if (sLoginResponse != null) {
                    if (sLoginResponse.isRequestSuccess()) {

                        sLoginResponse.getData().setLoginType(loginType);
//                        sLoginResponse.getData().setLoginId(name);

                        SdkUtil.updateLoginData(context, sLoginResponse);

                        SdkUtil.saveAccountModel(context, name, pwd,sLoginResponse.getData().getUserId(),sLoginResponse.getData().getToken(),
                                sLoginResponse.getData().getTimestamp(),true);//记住账号密码

                        if (sfCallBack != null){
                            sfCallBack.success(sLoginResponse,rawResult);
                        }

                    }else{

//                        ToastUtils.toast(context, sLoginResponse.getMessage() + "");
                        if (sfCallBack != null){
                            sfCallBack.fail(sLoginResponse,rawResult);
                        }
                    }

                } else {
                    ToastUtils.toast(context, R.string.py_error_occur);
//                    if (sfCallBack != null){
//                        sfCallBack.fail(null,rawResult);
//                    }
                }
            }

            @Override
            public void timeout(String code) {
                ToastUtils.toast(context, R.string.py_error_occur);
//                if (sfCallBack != null){
//                    sfCallBack.fail(null,"");
//                }
            }

            @Override
            public void noData() {
                ToastUtils.toast(context, R.string.py_error_occur);
//                if (sfCallBack != null){
//                    sfCallBack.fail(null,"");
//                }
            }

            @Override
            public void cancel() {}

        });
        baseLoginRequestTask.excute(SLoginResponse.class);
    }
}

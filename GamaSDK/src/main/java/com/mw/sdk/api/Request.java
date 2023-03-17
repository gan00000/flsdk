package com.mw.sdk.api;

import android.content.Context;

import com.core.base.bean.BaseResponseModel;
import com.core.base.callback.ISReqCallBack;
import com.core.base.callback.SFCallBack;
import com.core.base.request.SimpleHttpRequest;
import com.core.base.utils.PL;
import com.core.base.utils.SPUtil;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.mw.base.bean.SGameBaseRequestBean;
import com.mw.base.cfg.ResConfig;
import com.mw.base.utils.SdkUtil;
import com.mw.sdk.R;
import com.mw.sdk.ads.EventConstant;
import com.mw.sdk.ads.SdkEventLogger;
import com.mw.sdk.constant.ApiRequestMethod;
import com.mw.sdk.login.execute.BaseLoginRequestTask;
import com.mw.sdk.login.model.request.AccountBindInGameRequestBean;
import com.mw.sdk.login.model.response.SLoginResponse;
import com.mw.sdk.utils.DialogUtil;

import java.util.HashMap;
import java.util.Map;

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

                        String userId = sLoginResponse.getData().getUserId();
                        Map<String, Object> eventValue = new HashMap<String, Object>();
                        eventValue.put(EventConstant.ParameterName.USER_ID, userId);
                        String eventName = EventConstant.EventName.Upgrade_Account.name();
                        SdkEventLogger.sendEventToSever(context,eventName);
                        SdkEventLogger.trackingWithEventName(context,eventName,eventValue);

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

    public static void sendEventToServer(final Context context, String eventName) throws Exception {

        if (SStringUtil.isEmpty(eventName)){
            return;
        }

        String sp_key_event = "EVENT_KEY_" + eventName;

        if (SStringUtil.isNotEmpty(SPUtil.getSimpleString(context,SdkUtil.SDK_SP_FILE,sp_key_event))){
            PL.i("sendEventToServer exist eventname=" + eventName);
            return;
        }

        final SGameBaseRequestBean sGameBaseRequestBean = new SGameBaseRequestBean(context);

        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("eventName", eventName);
        requestParams.put("appTime", sGameBaseRequestBean.getTimestamp());
        requestParams.put("gameCode",sGameBaseRequestBean.getGameCode());
        requestParams.put("packageName",sGameBaseRequestBean.getPackageName());
        requestParams.put("uniqueId",sGameBaseRequestBean.getUniqueId());
        requestParams.put("system",sGameBaseRequestBean.getSystemVersion());
        requestParams.put("platform",sGameBaseRequestBean.getPlatform());
        requestParams.put("deviceType",sGameBaseRequestBean.getDeviceType());
        requestParams.put("gameLanguage",sGameBaseRequestBean.getGameLanguage());
        requestParams.put("versionCode",sGameBaseRequestBean.getVersionCode());
        requestParams.put("versionName",sGameBaseRequestBean.getVersionName());
        requestParams.put("androidId",sGameBaseRequestBean.getAndroidId());
        requestParams.put("adId",sGameBaseRequestBean.getAdvertisingId());

        sGameBaseRequestBean.setRequestParamsMap(requestParams);
        sGameBaseRequestBean.setRequestUrl(ResConfig.getLogPreferredUrl(context)); //日志记录
        sGameBaseRequestBean.setRequestMethod("sdk/event/log");

//        if (bean != null) {
//            adsRequestBean.setAppInstallTime(bean.getAppInstallTime());
//            adsRequestBean.setReferrerClickTime(bean.getReferrerClickTime());
//            adsRequestBean.setReferrer(bean.getReferrerUrl());
//        }
        SimpleHttpRequest simpleHttpRequest = new SimpleHttpRequest();
        simpleHttpRequest.setGetMethod(true,true);
        simpleHttpRequest.setBaseReqeustBean(sGameBaseRequestBean);
        simpleHttpRequest.setReqCallBack(new ISReqCallBack<BaseResponseModel>() {
            @Override
            public void success(BaseResponseModel responseModel, String rawResult) {
                PL.i("sendEventToServer success eventname=" + eventName);
                SPUtil.saveSimpleInfo(context, SdkUtil.SDK_SP_FILE, sp_key_event, eventName);

            }

            @Override
            public void timeout(String code) {
                PL.i("sendEventToServer timeout eventname=" + eventName);
            }

            @Override
            public void noData() {
                PL.i("sendEventToServer success eventname=" + eventName);
                SPUtil.saveSimpleInfo(context, SdkUtil.SDK_SP_FILE, sp_key_event, eventName);
            }

            @Override
            public void cancel() {
            }
        });
        simpleHttpRequest.excute();
    }
}

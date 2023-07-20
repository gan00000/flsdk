package com.ldy.sdk.api;

import android.content.Context;

import com.ldy.sdk.login.model.response.SLoginResult;
import com.mybase.bean.BaseResultModel;
import com.ldy.callback.ISReqCallBack;
import com.ldy.callback.SFCallBack;
import com.mybase.request.SimpleHttpRequest;
import com.mybase.utils.PL;
import com.mybase.utils.SPUtil;
import com.mybase.utils.SStringUtil;
import com.mybase.utils.ToastUtils;
import com.ldy.base.bean.SGameBaseRequestBean;
import com.ldy.base.cfg.ResConfig;
import com.ldy.base.utils.SdkUtil;
import com.ldy.sdk.R;
import com.ldy.sdk.ads.EventConstant;
import com.ldy.sdk.ads.SdkEventLogger;
import com.ldy.sdk.constant.ApiRequestMethod;
import com.ldy.sdk.utils.DataManager;
import com.ldy.sdk.login.execute.BaseLoginRequestTask;
import com.ldy.sdk.login.model.request.AccountBindInGameRequestBean;
import com.ldy.sdk.utils.DialogUtil;

import java.util.HashMap;
import java.util.Map;

public class Request {

    public static void sendVfCode(Context context, boolean needDialog, String areaCode, String telephone, SFCallBack<BaseResultModel> iSdkCallBack) {

        SGameBaseRequestBean sGameBaseRequestBean = new SGameBaseRequestBean(context);
        sGameBaseRequestBean.setPhone(telephone);
        sGameBaseRequestBean.setPhoneAreaCode(areaCode);
        sGameBaseRequestBean.setRequestMethod(ApiRequestMethod.api_sendMobileVcode);
        BaseLoginRequestTask baseLoginRequestTask = new BaseLoginRequestTask(context);
        baseLoginRequestTask.setSdkBaseRequestBean(sGameBaseRequestBean);

        if (needDialog) {
            baseLoginRequestTask.setLoadDialog(DialogUtil.createLoadingDialog(context, "Loading..."));
        }
        baseLoginRequestTask.setReqCallBack(new ISReqCallBack<BaseResultModel>() {
            @Override
            public void success(BaseResultModel responseModel, String rawResult) {
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
                    ToastUtils.toast(context, R.string.mstr_palltodayry_pulchrster);
                    if (iSdkCallBack != null){
                        BaseResultModel errorModel = new BaseResultModel();
                        errorModel.setCode("1001");
                        errorModel.setMessage(context.getString(R.string.mstr_shareee_octaire));
                        iSdkCallBack.fail(errorModel,errorModel.getWrapRawResponse());
                    }
                }
            }

            @Override
            public void timeout(String code) {
                ToastUtils.toast(context, R.string.mstr_palltodayry_pulchrster);
                if (iSdkCallBack != null){
                    BaseResultModel errorModel = new BaseResultModel();
                    errorModel.setCode("1001");
                    errorModel.setMessage(context.getString(R.string.mstr_palltodayry_pulchrster));
                    iSdkCallBack.fail(errorModel,errorModel.getWrapRawResponse());
                }
            }

            @Override
            public void noData() {
                ToastUtils.toast(context, R.string.mstr_palltodayry_pulchrster);
                if (iSdkCallBack != null){
                    BaseResultModel errorModel = new BaseResultModel();
                    errorModel.setCode("1001");
                    errorModel.setMessage(context.getString(R.string.mstr_palltodayry_pulchrster));
                    iSdkCallBack.fail(errorModel,errorModel.getWrapRawResponse());
                }
            }

            @Override
            public void cancel() {
                if (iSdkCallBack != null){
                    BaseResultModel errorModel = new BaseResultModel();
                    errorModel.setCode("1001");
                    errorModel.setMessage("user cancel");
                    iSdkCallBack.fail(errorModel,errorModel.getWrapRawResponse());
                }
            }

        });
        baseLoginRequestTask.excute(BaseResultModel.class);
    }

    public static void bindPhone(Context context, boolean needDialog, String areaCode, String telephone,String vfCode, SFCallBack<SLoginResult> sfCallBack) {

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
        baseLoginRequestTask.setReqCallBack(new ISReqCallBack<BaseResultModel>() {
            @Override
            public void success(BaseResultModel responseModel, String rawResult) {
                if (responseModel != null) {
                    if (responseModel.isRequestSuccess()) {

                        SLoginResult localLoginResponse = SdkUtil.getCurrentUserLoginResponse(context);
                        localLoginResponse.getData().setTelephone(areaCode + "-" + telephone);
                        localLoginResponse.getData().setBindPhone(true);
                        SdkUtil.updateLoginData(context, localLoginResponse);

                        if (sfCallBack != null){
                            sfCallBack.success(localLoginResponse,rawResult);
                        }

                    }else{

//                        ToastUtils.toast(context, responseModel.getMessage() + "");
                        SLoginResult tempLoginResponse = new SLoginResult();
                        tempLoginResponse.setCode(responseModel.getCode());
                        tempLoginResponse.setRawResponse(responseModel.getRawResponse());
                        tempLoginResponse.setMessage(responseModel.getMessage());

                        if (sfCallBack != null){
                            sfCallBack.fail(tempLoginResponse,rawResult);
                        }
                    }

                } else {
                    ToastUtils.toast(context, R.string.mstr_palltodayry_pulchrster);
//                    if (sfCallBack != null){
//                        sfCallBack.fail(null,rawResult);
//                    }
                }
            }

            @Override
            public void timeout(String code) {
                ToastUtils.toast(context, R.string.mstr_palltodayry_pulchrster);
//                if (sfCallBack != null){
//                    sfCallBack.fail(null,"");
//                }
            }

            @Override
            public void noData() {
                ToastUtils.toast(context, R.string.mstr_palltodayry_pulchrster);
//                if (sfCallBack != null){
//                    sfCallBack.fail(null,"");
//                }
            }

            @Override
            public void cancel() {}

        });
        baseLoginRequestTask.excute(BaseResultModel.class);
    }

    public static void bindAcountInGame(Context context, boolean needDialog, String loginType,String name, String pwd, SFCallBack<SLoginResult> sfCallBack) {

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
        baseLoginRequestTask.setReqCallBack(new ISReqCallBack<SLoginResult>() {
            @Override
            public void success(SLoginResult sLoginResponse, String rawResult) {
                if (sLoginResponse != null) {
                    if (sLoginResponse.isRequestSuccess()) {

                        sLoginResponse.getData().setLoginType(loginType);
//                        sLoginResponse.getData().setLoginId(name);

                        String userId = sLoginResponse.getData().getUserId();
                        Map<String, Object> eventValue = new HashMap<String, Object>();
                        eventValue.put(EventConstant.ParameterName.USER_ID, userId);
//                        eventValue.put(EventConstant.ParameterName.SERVER_TIME, SdkUtil.getSdkTimestamp(context) + "");
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
                    ToastUtils.toast(context, R.string.mstr_palltodayry_pulchrster);
//                    if (sfCallBack != null){
//                        sfCallBack.fail(null,rawResult);
//                    }
                }
            }

            @Override
            public void timeout(String code) {
                ToastUtils.toast(context, R.string.mstr_palltodayry_pulchrster);
//                if (sfCallBack != null){
//                    sfCallBack.fail(null,"");
//                }
            }

            @Override
            public void noData() {
                ToastUtils.toast(context, R.string.mstr_palltodayry_pulchrster);
//                if (sfCallBack != null){
//                    sfCallBack.fail(null,"");
//                }
            }

            @Override
            public void cancel() {}

        });
        baseLoginRequestTask.excute(SLoginResult.class);
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

        if (DataManager.getInstance().isLogin()) {
            requestParams.put("userId",sGameBaseRequestBean.getUserId());
        }

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
        simpleHttpRequest.setReqCallBack(new ISReqCallBack<BaseResultModel>() {
            @Override
            public void success(BaseResultModel responseModel, String rawResult) {
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

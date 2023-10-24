package com.mw.sdk.api;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import com.core.base.bean.BaseResponseModel;
import com.core.base.callback.ISReqCallBack;
import com.core.base.callback.SFCallBack;
import com.core.base.request.SimpleHttpRequest;
import com.core.base.utils.PL;
import com.core.base.utils.SPUtil;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.mw.sdk.bean.SGameBaseRequestBean;
import com.mw.sdk.bean.res.ActData;
import com.mw.sdk.bean.res.ConfigBean;
import com.mw.sdk.utils.ResConfig;
import com.mw.sdk.utils.SdkUtil;
import com.mw.sdk.MWBaseWebActivity;
import com.mw.sdk.R;
import com.mw.sdk.ads.EventConstant;
import com.mw.sdk.ads.SdkEventLogger;
import com.mw.sdk.constant.ApiRequestMethod;
import com.mw.sdk.bean.req.PayCreateOrderReqBean;
import com.mw.sdk.bean.res.TogglePayRes;
import com.mw.sdk.utils.PayHelper;
import com.mw.sdk.utils.DataManager;
import com.mw.sdk.api.task.BaseLoginRequestTask;
import com.mw.sdk.bean.req.AccountBindInGameRequestBean;
import com.mw.sdk.login.model.response.SLoginResponse;
import com.mw.sdk.utils.DialogUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

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
                    if (iSdkCallBack != null){
                        BaseResponseModel errorModel = new BaseResponseModel();
                        errorModel.setCode("1001");
                        errorModel.setMessage(context.getString(R.string.text_phone_not_empty));
                        iSdkCallBack.fail(errorModel,errorModel.getWrapRawResponse());
                    }
                }
            }

            @Override
            public void timeout(String code) {
                ToastUtils.toast(context, R.string.py_error_occur);
                if (iSdkCallBack != null){
                    BaseResponseModel errorModel = new BaseResponseModel();
                    errorModel.setCode("1001");
                    errorModel.setMessage(context.getString(R.string.py_error_occur));
                    iSdkCallBack.fail(errorModel,errorModel.getWrapRawResponse());
                }
            }

            @Override
            public void noData() {
                ToastUtils.toast(context, R.string.py_error_occur);
                if (iSdkCallBack != null){
                    BaseResponseModel errorModel = new BaseResponseModel();
                    errorModel.setCode("1001");
                    errorModel.setMessage(context.getString(R.string.py_error_occur));
                    iSdkCallBack.fail(errorModel,errorModel.getWrapRawResponse());
                }
            }

            @Override
            public void cancel() {
                if (iSdkCallBack != null){
                    BaseResponseModel errorModel = new BaseResponseModel();
                    errorModel.setCode("1001");
                    errorModel.setMessage("user cancel");
                    iSdkCallBack.fail(errorModel,errorModel.getWrapRawResponse());
                }
            }

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
        requestParams.put("appsflyerId",sGameBaseRequestBean.getAppsflyerId());

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

    public static void openCs(Activity activity, int requestCode){
        PL.i("sdk .. openCs");
        ConfigBean configBean = SdkUtil.getSdkCfg(activity.getApplicationContext());
        if (configBean != null && configBean.getUrl() != null && SStringUtil.isNotEmpty(configBean.getUrl().getCsUrl())) {

            SGameBaseRequestBean sGameBaseRequestBean = new SGameBaseRequestBean(activity);
            if (requestCode > 0){
                sGameBaseRequestBean.setRequest_code(requestCode);
            }
            sGameBaseRequestBean.setCompleteUrl(configBean.getUrl().getCsUrl());

            Intent csIntent = MWBaseWebActivity.create(activity,"", sGameBaseRequestBean.createPreRequestUrl());
            activity.startActivity(csIntent);
        }else {
            PL.i("获取不到客服地址");
        }

    }


    public static void togglePayRequest(Context context, PayCreateOrderReqBean checkPayTypeReqBean, SFCallBack<TogglePayRes> sfCallBack) {

        checkPayTypeReqBean.setRequestMethod(ApiRequestMethod.API_PAYMENT_CHANNEL);
        checkPayTypeReqBean.setRequestUrl(PayHelper.getPreferredUrl(context));
        SimpleHttpRequest simpleHttpRequest = new SimpleHttpRequest();
        simpleHttpRequest.setBaseReqeustBean(checkPayTypeReqBean);

        simpleHttpRequest.setLoadDialog(DialogUtil.createLoadingDialog(context, "Loading..."));
        simpleHttpRequest.setReqCallBack(new ISReqCallBack<TogglePayRes>() {
            @Override
            public void success(TogglePayRes togglePayRes, String rawResult) {

                if (togglePayRes != null && togglePayRes.isRequestSuccess()) {

                    if (sfCallBack != null){
                        sfCallBack.success(togglePayRes,rawResult);
                    }

                } else {
                    if (sfCallBack != null){
                        sfCallBack.fail(null,rawResult);
                    }
                }
            }

            @Override
            public void timeout(String code) {
                if (sfCallBack != null){
                    sfCallBack.fail(null,"");
                }
            }

            @Override
            public void noData() {
                if (sfCallBack != null){
                    sfCallBack.fail(null,"");
                }
            }

            @Override
            public void cancel() {}

        });
        simpleHttpRequest.excute(TogglePayRes.class);
    }

    public static void requestActData(Context context, SFCallBack<List<ActData>> sfCallBack) {

        String gameCode = ResConfig.getGameCode(context);
        SGameBaseRequestBean sGameBaseRequestBean = new SGameBaseRequestBean(context);
        Dialog dialog = DialogUtil.createLoadingDialog(context, "Loading...");
        dialog.show();
        RetrofitClient.instance().build(context,URLType.PLAT).create(MWApiService.class)
                .marketSwitch(sGameBaseRequestBean.fieldValueToMap())
                .flatMap(new Function<BaseResponseModel, ObservableSource<List<ActData>>>() {
                    @Override
                    public ObservableSource<List<ActData>> apply(BaseResponseModel baseResponseModel) throws Throwable {
                        PL.i("flatMap apply...");
                        if (baseResponseModel.isRequestSuccess()){
                            return RetrofitClient.instance().build(context,URLType.CDN).create(MWApiService.class)
                                    .getMarketData(gameCode, SdkUtil.getSdkTimestamp(context));
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ActData>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        PL.i("subscribe onSubscribe...");
                    }

                    @Override
                    public void onNext(@NonNull List<ActData> actData) {
                        PL.i("subscribe onNext...");
                        if (dialog != null  ) {
                            dialog.dismiss();
                        }

                        if (sfCallBack != null) {
                            sfCallBack.success(actData, "success");
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        PL.i("subscribe onError...");
                        if (dialog != null  ) {
                            dialog.dismiss();
                        }
                        if (sfCallBack != null) {
                            sfCallBack.fail(null, context.getString(R.string.py_error_occur));
                        }
                    }

                    @Override
                    public void onComplete() {
                        PL.i("subscribe onComplete...");
                        if (dialog != null  ) {
                            dialog.dismiss();
                        }
                    }
                });
    }
}

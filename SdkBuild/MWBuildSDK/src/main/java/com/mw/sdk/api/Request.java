package com.mw.sdk.api;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.core.base.bean.BaseResponseModel;
import com.core.base.callback.ISReqCallBack;
import com.core.base.callback.SFCallBack;
import com.core.base.request.SimpleHttpRequest;
import com.core.base.utils.PL;
import com.core.base.utils.SPUtil;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.mw.sdk.MWBaseWebActivity;
import com.mw.sdk.R;
import com.mw.sdk.ads.EventConstant;
import com.mw.sdk.ads.SdkEventLogger;
import com.mw.sdk.ads.TrackEventHelper;
import com.mw.sdk.api.task.BaseLoginRequestTask;
import com.mw.sdk.bean.SGameBaseRequestBean;
import com.mw.sdk.bean.req.AccountBindInGameRequestBean;
import com.mw.sdk.bean.req.ChangePwdRequestBean;
import com.mw.sdk.bean.req.DeleteAccountRequestBean;
import com.mw.sdk.bean.req.PayCreateOrderReqBean;
import com.mw.sdk.bean.req.PayEventReqBean;
import com.mw.sdk.bean.res.ActDataModel;
import com.mw.sdk.bean.res.ConfigBean;
import com.mw.sdk.bean.res.EventRes;
import com.mw.sdk.bean.res.RedDotRes;
import com.mw.sdk.bean.res.ToggleResult;
import com.mw.sdk.constant.ApiRequestMethod;
import com.mw.sdk.constant.RequestCode;
import com.mw.sdk.constant.SLoginType;
import com.mw.sdk.login.model.response.SLoginResponse;
import com.mw.sdk.utils.DataManager;
import com.mw.sdk.utils.DialogUtil;
import com.mw.sdk.utils.PayHelper;
import com.mw.sdk.utils.ResConfig;
import com.mw.sdk.utils.SdkUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

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
                        if (localLoginResponse != null) {
                            localLoginResponse.getData().setTelephone(areaCode + "-" + telephone);
                            localLoginResponse.getData().setBindPhone(true);
                            SdkUtil.updateLoginData(context, localLoginResponse);
                        }

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
                        String eventName = EventConstant.EventName.Upgrade_Account_Success.name();
                        //SdkEventLogger.sendEventToSever(context,eventName);
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

//    public static void sendEventToServer(final Context context, String eventName) throws Exception {
//        sendEventToServer(context, eventName, true, false);
//    }

    /**
     *
     * @param context
     * @param eventName
     * @param isDeviceOnce  是否对设备id报一次
     * @param isUserOnce 是否对用户uid报一次
     * @throws Exception
     */
    public static void sendEventToServer(final Context context, String eventName, Map<String, String> otherParams, boolean isDeviceOnce, boolean isUserOnce) throws Exception {

        if (SStringUtil.isEmpty(eventName)){
            return;
        }

        String sp_key_event = "EVENT_KEY_" + eventName;
        String is_server_event_once = context.getResources().getString(R.string.mw_event_isDeviceOnce);
        if (isDeviceOnce || "true".equals(is_server_event_once)){//对设备一次

            if (SStringUtil.isNotEmpty(SPUtil.getSimpleString(context,SdkUtil.SDK_SP_FILE, sp_key_event))){
                PL.i("sendEventToServer exist for device eventname=" + eventName);
                return;
            }
        }

        final SGameBaseRequestBean sGameBaseRequestBean = new SGameBaseRequestBean(context);

        if (isUserOnce && SdkUtil.isLogin(context)){//对登录的用户一次

            sp_key_event = "EVENT_KEY_" + eventName + "_" + sGameBaseRequestBean.getUserId();

            if (SStringUtil.isNotEmpty(SPUtil.getSimpleString(context,SdkUtil.SDK_SP_FILE,sp_key_event))){
                PL.i("sendEventToServer exist for uid eventname=" + eventName);
                return;
            }
        }

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

        if (DataManager.getInstance().isLogin() || SdkUtil.isLogin(context)) {
            requestParams.put("userId",sGameBaseRequestBean.getUserId());
        }
        if (otherParams != null && !otherParams.isEmpty()){
            requestParams.putAll(otherParams);
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

        String finalSp_key_event = sp_key_event;
        PL.i("-----track event send to sever start name=" + eventName);
        simpleHttpRequest.setReqCallBack(new ISReqCallBack<BaseResponseModel>() {
            @Override
            public void success(BaseResponseModel responseModel, String rawResult) {
                PL.i("-----track event send to sever success name=" + eventName);
                SPUtil.saveSimpleInfo(context, SdkUtil.SDK_SP_FILE, finalSp_key_event, eventName);

            }

            @Override
            public void timeout(String code) {
                PL.i("-----track event send to sever timeout name=" + eventName);
            }

            @Override
            public void noData() {
                PL.i("-----track event send to sever success name=" + eventName);
                SPUtil.saveSimpleInfo(context, SdkUtil.SDK_SP_FILE, finalSp_key_event, eventName);
            }

            @Override
            public void cancel() {
            }
        });
        simpleHttpRequest.excute();
    }

    public static void openCs(Activity activity, int requestCode){
        PL.i("sdk .. openCs");

        String csUrl = activity.getString(R.string.mw_sdk_customer_url);
        ConfigBean configBean = SdkUtil.getSdkCfg(activity.getApplicationContext());
        if (configBean != null && configBean.getUrl() != null && SStringUtil.isNotEmpty(configBean.getUrl().getCsUrl())) {
            csUrl = configBean.getUrl().getCsUrl();
        }else {
            PL.i("获取不到server客服地址");
            //ToastUtils.toast(activity, "cs url empty");
        }

        SGameBaseRequestBean sGameBaseRequestBean = new SGameBaseRequestBean(activity);
//        if (requestCode > 0){
//            sGameBaseRequestBean.setRequest_code(requestCode);
//        }
        if (SdkUtil.isLogin(activity) && SStringUtil.isNotEmpty(SdkUtil.getRoleId(activity))){
            sGameBaseRequestBean.setRequest_code(0);
        }else {
            sGameBaseRequestBean.setRequest_code(RequestCode.RequestCode_CS_LOGIN);
        }
        sGameBaseRequestBean.setCompleteUrl(csUrl);

        Intent csIntent = MWBaseWebActivity.create(activity,"", sGameBaseRequestBean.createPreRequestUrl());
        activity.startActivity(csIntent);

    }


    public static void togglePayRequest(Context context, boolean isChannelV2, PayCreateOrderReqBean checkPayTypeReqBean, SFCallBack<ToggleResult> sfCallBack) {

        //是否使用新版第三方页面接口
        if (isChannelV2){
            checkPayTypeReqBean.setRequestMethod(ApiRequestMethod.API_PAYMENT_CHANNEL_V2);
        }else {
            checkPayTypeReqBean.setRequestMethod(ApiRequestMethod.API_PAYMENT_CHANNEL);
        }
        checkPayTypeReqBean.setRequestUrl(PayHelper.getPreferredUrl(context));
        SimpleHttpRequest simpleHttpRequest = new SimpleHttpRequest();
        simpleHttpRequest.setBaseReqeustBean(checkPayTypeReqBean);

        simpleHttpRequest.setLoadDialog(DialogUtil.createLoadingDialog(context, "Loading..."));
        simpleHttpRequest.setReqCallBack(new ISReqCallBack<ToggleResult>() {
            @Override
            public void success(ToggleResult toggleResult, String rawResult) {

                if (toggleResult != null && toggleResult.isRequestSuccess()) {

                    if (sfCallBack != null){
                        sfCallBack.success(toggleResult,rawResult);
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
        simpleHttpRequest.excute(ToggleResult.class);
    }

    public static void requestActData(Context context, SFCallBack<ActDataModel> sfCallBack) {

        String gameCode = ResConfig.getGameCode(context);
        SGameBaseRequestBean sGameBaseRequestBean = new SGameBaseRequestBean(context);
        Dialog dialog = DialogUtil.createLoadingDialog(context, "Loading...");
        dialog.show();
        RetrofitClient.instance().build(context,URLType.PLAT).create(MWApiService.class)
                .marketSwitch(sGameBaseRequestBean.fieldValueToMap())
                .flatMap(new Function<ToggleResult, ObservableSource<ActDataModel>>() {
                    @Override
                    public ObservableSource<ActDataModel> apply(ToggleResult toggleResult) throws Throwable {
                        PL.i("flatMap apply...");
                        if (toggleResult != null && toggleResult.isRequestSuccess() && toggleResult.getData() != null && toggleResult.getData().isShowMarketButton()){
//                            return RetrofitClient.instance().build(context,URLType.CDN).create(MWApiService.class)
//                                    .getMarketData(gameCode, SdkUtil.getSdkTimestamp(context));
                            return RetrofitClient.instance().build(context,URLType.PLAT).create(MWApiService.class)
                                    .getMarketData(sGameBaseRequestBean.fieldValueToMap());
                        }
//                        return RetrofitClient.instance().build(context,URLType.PLAT).create(MWApiService.class)
//                                .getMarketData(sGameBaseRequestBean.fieldValueToMap());
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ActDataModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        PL.i("subscribe onSubscribe...");
                    }

                    @Override
                    public void onNext(@NonNull ActDataModel actDataModel) {
                        PL.i("subscribe onNext...");
                        if (dialog != null  ) {
                            dialog.dismiss();
                        }

                        if (sfCallBack != null) {
                            sfCallBack.success(actDataModel, "success");
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

    public static void requestMarketSwitch(Context context, SFCallBack<ToggleResult> sfCallBack) {

        String gameCode = ResConfig.getGameCode(context);
        SGameBaseRequestBean sGameBaseRequestBean = new SGameBaseRequestBean(context);
        RetrofitClient.instance().build(context,URLType.PLAT).create(MWApiService.class)
                .marketSwitch(sGameBaseRequestBean.fieldValueToMap())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ToggleResult>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        PL.i("subscribe onSubscribe...");
                    }

                    @Override
                    public void onNext(@NonNull ToggleResult actDataModel) {
                        PL.i("subscribe onNext...");

                        if (sfCallBack != null) {
                            sfCallBack.success(actDataModel, "success");
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        PL.i("subscribe onError...");
                    }

                    @Override
                    public void onComplete() {
                        PL.i("subscribe onComplete...");
                    }
                });
    }

    public static void requestFloatConfigData(Context context, SFCallBack<String> sfCallBack) {
/*
        String gameCode = ResConfig.getGameCode(context);
        RetrofitClient.instance().build(context,URLType.CDN).create(MWApiService.class)
                .getFloatConfigData(gameCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ResponseBody responseBody) {
                        try {
                            String floatCfgData = responseBody.string();
                            PL.i("floatCfgData=" + floatCfgData);
                            SdkUtil.saveFloatCfgData(context, floatCfgData);
                        } catch (IOException e) {
                            SdkUtil.saveFloatCfgData(context, "");
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        PL.i("getFloatConfigData onError...");
                        //SdkUtil.saveFloatCfgData(context, "");
                    }

                    @Override
                    public void onComplete() {

                    }
                });*/
    }

    public static void requestFloatMenus(Context context, SFCallBack<String> sfCallBack) {

//        String gameCode = ResConfig.getGameCode(context);
        SGameBaseRequestBean sGameBaseRequestBean = new SGameBaseRequestBean(context);

        if (SStringUtil.isEmpty(sGameBaseRequestBean.getRoleId()) || SStringUtil.isEmpty(sGameBaseRequestBean.getServerCode())){
            PL.e("requestFloatMenus params error...");
            return;
        }

        RetrofitClient.instance().build(context,URLType.PLAT).create(MWApiService.class)
                .getFloatMenus_V2(sGameBaseRequestBean.fieldValueToMap())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ResponseBody xResponseBody) {
                        try {
                            String xData = xResponseBody.string();
                            PL.i("getFloatMenus=" + xData);
                            SdkUtil.saveFloatMenuResData(context, xData);
                            if (sfCallBack != null){
                                sfCallBack.success(xData, "");
                            }
                        } catch (IOException e) {
                            SdkUtil.saveFloatMenuResData(context, "");
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        PL.i("getFloatConfigData onError...");
                        //SdkUtil.saveFloatSwitchData(context, "");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    public static void getFloatBtnRedDot(Context context, SFCallBack<RedDotRes> sfCallBack) {

//        String gameCode = ResConfig.getGameCode(context);
        SGameBaseRequestBean sGameBaseRequestBean = new SGameBaseRequestBean(context);
        RetrofitClient.instance().build(context,URLType.PLAT).create(MWApiService.class)
                .getFloatBtnRedDot(sGameBaseRequestBean.fieldValueToMap())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RedDotRes>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull RedDotRes redDotRes) {
                        if (sfCallBack!= null && redDotRes != null){
//                            redDotRes.getData().setCs(true);//test
                            sfCallBack.success(redDotRes, "success");
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        PL.i("getFloatBtnRedDot onError...");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //删除红点
    public static void deleteFloatBtnRedDot(Context context, SFCallBack<RedDotRes> sfCallBack) {

//        String gameCode = ResConfig.getGameCode(context);
        SGameBaseRequestBean sGameBaseRequestBean = new SGameBaseRequestBean(context);
        RetrofitClient.instance().build(context,URLType.PLAT).create(MWApiService.class)
                .deleteFloatBtnRedDot(sGameBaseRequestBean.fieldValueToMap())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RedDotRes>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull RedDotRes redDotRes) {
                        if (sfCallBack!= null && redDotRes != null){
                            sfCallBack.success(redDotRes, "success");
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        PL.i("getFloatBtnRedDot onError...");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }



    //修改密码，与 ChangePwdRequestTask的一致
    public static void changePwd(Context context, String account, String oldPwd, String newPwd, SFCallBack<SLoginResponse> sfCallBack) {

        if (SStringUtil.isEmpty(account) || SStringUtil.isEmpty(oldPwd) || SStringUtil.isEmpty(newPwd)){
            return;
        }

        ChangePwdRequestBean  changePwdRequestBean = new ChangePwdRequestBean(context);
        changePwdRequestBean.setName(account);
        changePwdRequestBean.setOldPwd(SStringUtil.toMd5(oldPwd.trim()));
        changePwdRequestBean.setNewPwd(SStringUtil.toMd5(newPwd.trim()));
        changePwdRequestBean.setSignature(SStringUtil.toMd5(ResConfig.getAppKey(context) + changePwdRequestBean.getTimestamp() +
                changePwdRequestBean.getName() + changePwdRequestBean.getGameCode()));

        Dialog dialog = DialogUtil.createLoadingDialog(context, "Loading...");
        dialog.show();
        RetrofitClient.instance().build(context,URLType.LOGIN).create(MWApiService.class)
                .changePassword(changePwdRequestBean.fieldValueToMap())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SLoginResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull SLoginResponse sLoginResponse) {
                        if (dialog != null  ) {
                            dialog.dismiss();
                        }
                        if (sLoginResponse != null){
                            if (sLoginResponse.isRequestSuccess()) {

                                ToastUtils.toast(context, R.string.text_account_change_pwd_success2);

                                sLoginResponse.getData().setLoginType(SLoginType.LOGIN_TYPE_MG);
                                SdkUtil.updateLoginData(context, sLoginResponse);

                                SdkUtil.saveAccountModel(context, account, newPwd, sLoginResponse.getData().getUserId(),sLoginResponse.getData().getToken(),
                                        sLoginResponse.getData().getTimestamp(),true);//记住账号密码

                                if (sfCallBack != null){
                                    sfCallBack.success(sLoginResponse, "success");
                                }
                            }else{
                                ToastUtils.toast(context, sLoginResponse.getMessage() + "");
                                if (sfCallBack != null){
                                    sfCallBack.fail(sLoginResponse, "fail");
                                }
                            }
                        }else {
                            ToastUtils.toast(context, R.string.py_error_occur);
                            if (sfCallBack != null){
                                sfCallBack.fail(null, "fail");
                            }
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        ToastUtils.toast(context, R.string.py_error_occur);
                        if (dialog != null  ) {
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (dialog != null  ) {
                            dialog.dismiss();
                        }
                    }
                });
    }


    public static void deleteAccout(Context context, String userId, String loginMode, String thirdLoginId,String loginAccessToken, String loginTimestamp, SFCallBack<SLoginResponse> sfCallBack) {

        if (SStringUtil.isEmpty(userId)){
            return;
        }

        DeleteAccountRequestBean requestBean = new DeleteAccountRequestBean(context, userId,loginMode,thirdLoginId);
        requestBean.setLoginAccessToken(loginAccessToken);
        requestBean.setLoginTimestamp(loginTimestamp);
        requestBean.setSignature(SStringUtil.toMd5(ResConfig.getAppKey(context) + requestBean.getTimestamp() +
                requestBean.getUserId() + requestBean.getGameCode()));

        Dialog dialog = DialogUtil.createLoadingDialog(context, "Loading...");
        dialog.show();
        RetrofitClient.instance().build(context,URLType.LOGIN).create(MWApiService.class)
                .deleteAccout(requestBean.fieldValueToMap())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SLoginResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull SLoginResponse sLoginResponse) {
                        if (dialog != null  ) {
                            dialog.dismiss();
                        }
                        if (sLoginResponse != null) {
                            if (sLoginResponse.isRequestSuccess()) {
                                if (SStringUtil.isNotEmpty(sLoginResponse.getMessage())){
                                    ToastUtils.toast(context, sLoginResponse.getMessage() + "");
                                }
                                SdkUtil.removeAccountModelByUserId(context,userId);
                                if (sfCallBack != null){
                                    sfCallBack.success(sLoginResponse,"success");
                                }

                            }else{

                                ToastUtils.toast(context, sLoginResponse.getMessage() + "");
                                if (sfCallBack != null){
                                    sfCallBack.fail(sLoginResponse,"fail");
                                }
                            }

                        } else {
                            ToastUtils.toast(context, R.string.py_error_occur);
                            if (sfCallBack != null){
                                sfCallBack.fail(null, "fail");
                            }
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        ToastUtils.toast(context, R.string.py_error_occur);
                        if (dialog != null  ) {
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (dialog != null  ) {
                            dialog.dismiss();
                        }
                    }
                });
    }


    //服务端不判断成功
    public static void requestEventsData(Context context, String orderId, double amount, String productId, SFCallBack<EventRes> sfCallBack) {

        if (context == null || SStringUtil.isEmpty(orderId) || amount <= 0){
            return;
        }
        PL.i("requestEventsData start");
        PayEventReqBean payEventReqBean = new PayEventReqBean(context);
        payEventReqBean.setOrderId(orderId);
        payEventReqBean.setAmount(amount);
//        Dialog dialog = DialogUtil.createLoadingDialog(context, "Loading...");
//        dialog.show();
        RetrofitClient.instance().build(context,URLType.PLAT).create(MWApiService.class)
                .getRechargeEvent(payEventReqBean.fieldValueToMap())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<EventRes>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        PL.i("subscribe onSubscribe...");
                    }

                    @Override
                    public void onNext(@NonNull EventRes eventRes) {
                        PL.i("subscribe onNext...");
//                        if (dialog != null  ) {
//                            dialog.dismiss();
//                        }
                        PL.i("requestEventsData finish");
                        if (sfCallBack != null) {
                            sfCallBack.success(eventRes, "success");
                        }

                        if (eventRes != null && eventRes.getData() != null && !eventRes.getData().isEmpty() && eventRes.isRequestSuccess()){

                            //SGameBaseRequestBean sGameBaseRequestBean = new SGameBaseRequestBean(context);
                            String channel_platform = ResConfig.getChannelPlatform(context);

                            //遍历事件开始上报
                            for (EventRes.EventData eventData : eventRes.getData()) {
                               String eventName = eventData.getEventName();
                               if (SStringUtil.isNotEmpty(eventName) && eventData.getData() != null && !eventData.getData().isEmpty()){
                                   //遍历需要上报的平台和值
                                   for (EventRes.EventPlatData eventPlatData : eventData.getData()) {
                                        if ("fb".equals(eventPlatData.getPlatform())){

                                            TrackEventHelper.trackRevenueFB(context,false, eventName, eventPlatData.getValue(),eventPlatData.getCurrency(), payEventReqBean.getUserId(), payEventReqBean.getRoleId(), productId, orderId, channel_platform, null);

                                        }else if ("firebase".equals(eventPlatData.getPlatform())){

                                            TrackEventHelper.trackRevenueFirebase(context, eventName, eventPlatData.getValue(),eventPlatData.getCurrency(), payEventReqBean.getUserId(), payEventReqBean.getRoleId(), productId, orderId, channel_platform, null);

                                       }else if ("appsflyer".equals(eventPlatData.getPlatform())){
                                            TrackEventHelper.trackRevenueAF(context, eventName, eventPlatData.getValue(),eventPlatData.getCurrency(), payEventReqBean.getUserId(), payEventReqBean.getRoleId(), productId, orderId, channel_platform, null);
                                       }else if ("tiktok".equals(eventPlatData.getPlatform())){
                                            TrackEventHelper.trackRevenueTT(context, eventName, eventPlatData.getValue(),eventPlatData.getCurrency(), payEventReqBean.getUserId(), payEventReqBean.getRoleId(), productId, orderId, channel_platform, null);
                                        }else if ("singular".equals(eventPlatData.getPlatform())){
                                            TrackEventHelper.trackRevenueSingular(context, eventName, eventPlatData.getValue(),eventPlatData.getCurrency(), payEventReqBean.getUserId(), payEventReqBean.getRoleId(), productId, orderId, channel_platform, null);
                                        }
                                   }
                               }
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        PL.i("subscribe onError...");
//                        if (dialog != null  ) {
//                            dialog.dismiss();
//                        }
                        if (sfCallBack != null) {
                            sfCallBack.fail(null, context.getString(R.string.py_error_occur));
                        }
                    }

                    @Override
                    public void onComplete() {
                        PL.i("subscribe onComplete...");
//                        if (dialog != null  ) {
//                            dialog.dismiss();
//                        }
                    }
                });
    }
}

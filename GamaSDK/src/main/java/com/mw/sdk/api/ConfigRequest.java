package com.mw.sdk.api;

import android.content.Context;
import android.text.TextUtils;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.callback.ISReqCallBack;
import com.core.base.request.CfgFileRequest;
import com.core.base.utils.PL;
import com.mw.sdk.R;
import com.mw.sdk.bean.res.ActDataModel;
import com.mw.sdk.bean.res.ToggleResult;
import com.mw.sdk.utils.ResConfig;
import com.mw.sdk.utils.SdkUtil;

import java.io.IOException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by gan on 2017/2/14.
 */

public class ConfigRequest{

//    域名/android/ifm/s_sdk_config.txt
//    域名/android/nonifm/s_sdk_config.txt
//    域名/ios/ifm/s_sdk_config.txt.txt
//    域名/ios/nonifm/s_sdk_config.txt.txt∂

    public static void requestBaseCfg(final Context context){

      /*  CfgFileRequest cfgFileRequest = new CfgFileRequest(context);
        BaseReqeustBean baseReqeustBean = new BaseReqeustBean(context);
        final String gameCode = ResConfig.getGameCode(context.getApplicationContext());

        String configUrl = ResConfig.getCdnPreferredUrl(context) + "sdk/config/" + gameCode + "/v1/version.json?v=" + System.currentTimeMillis();
        baseReqeustBean.setCompleteUrl(configUrl);

        cfgFileRequest.setBaseReqeustBean(baseReqeustBean);
        cfgFileRequest.setReqCallBack(new ISReqCallBack() {
            @Override
            public void success(Object o, String rawResult) {

                PL.i("requestBaseCfg:" + rawResult);

                if (TextUtils.isEmpty(rawResult)){
                    return;
                }
                SdkUtil.saveSdkCfg(context,rawResult);
            }

            @Override
            public void timeout(String code) {

            }

            @Override
            public void noData() {

            }

            @Override
            public void cancel() {

            }
        });
        cfgFileRequest.excute();*/

        final String gameCode = ResConfig.getGameCode(context.getApplicationContext());
        RetrofitClient.instance().build(context,URLType.CDN).create(MWApiService.class)
                .getBaseConfig(gameCode, System.currentTimeMillis() + "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ResponseBody responseBody) {

                        try {
                            String rawResult = responseBody.string();
                            PL.i("getBaseConfig finish > " + rawResult);
                            if (TextUtils.isEmpty(rawResult)){
                                return;
                            }
                            SdkUtil.saveSdkCfg(context, rawResult);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    public static void requestAreaCodeInfo(final Context context){

        /*CfgFileRequest cfgFileRequest = new CfgFileRequest(context);
        BaseReqeustBean baseReqeustBean = new BaseReqeustBean(context);

        String configUrl = ResConfig.getCdnPreferredUrl(context) + "sdk/config/areaCode/areaInfo.json?v=" + System.currentTimeMillis();
        baseReqeustBean.setCompleteUrl(configUrl);

        cfgFileRequest.setBaseReqeustBean(baseReqeustBean);
        cfgFileRequest.setReqCallBack(new ISReqCallBack() {
            @Override
            public void success(Object o, String rawResult) {

                PL.i("requestAreaCodeInfo:" + rawResult);

                if (TextUtils.isEmpty(rawResult)){
                    return;
                }
                SdkUtil.saveAreaCodeInfo(context,rawResult);
            }

            @Override
            public void timeout(String code) {

            }

            @Override
            public void noData() {

            }

            @Override
            public void cancel() {

            }
        });
        cfgFileRequest.excute();*/

        RetrofitClient.instance().build(context,URLType.CDN).create(MWApiService.class)
                .getPhoneInfo(System.currentTimeMillis() + "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ResponseBody responseBody) {

                        try {
                            String rawResult = responseBody.string();
                            PL.i("requestAreaCodeInfo finish");

                            if (TextUtils.isEmpty(rawResult)){
                                return;
                            }
                            SdkUtil.saveAreaCodeInfo(context,rawResult);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}

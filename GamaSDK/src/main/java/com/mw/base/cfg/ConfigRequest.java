package com.mw.base.cfg;

import android.content.Context;
import android.text.TextUtils;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.callback.ISReqCallBack;
import com.core.base.request.CfgFileRequest;
import com.core.base.utils.PL;
import com.mw.base.utils.SdkUtil;

/**
 * Created by gan on 2017/2/14.
 */

public class ConfigRequest{

//    域名/android/ifm/s_sdk_config.txt
//    域名/android/nonifm/s_sdk_config.txt
//    域名/ios/ifm/s_sdk_config.txt.txt
//    域名/ios/nonifm/s_sdk_config.txt.txt∂

    public static void requestBaseCfg(final Context context){

        CfgFileRequest cfgFileRequest = new CfgFileRequest(context);
        BaseReqeustBean baseReqeustBean = new BaseReqeustBean(context);
        final String gameCode = ResConfig.getGameCode(context.getApplicationContext());
//        String mUrl = null;
//        if (!ResConfig.isInfringement(context)) {
//            mUrl = ResConfig.getCdnPreferredUrl(context) + "android/nonifm/s_sdk_config.txt";
//        }else{
//            mUrl =  + "android/ifm/s_sdk_config.txt";
//        }

//        https://www.meowplayer.com/sdk/config/jjcs/v1/version.json

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
        cfgFileRequest.excute();
    }

    public static void requestAreaCodeInfo(final Context context){

        CfgFileRequest cfgFileRequest = new CfgFileRequest(context);
        BaseReqeustBean baseReqeustBean = new BaseReqeustBean(context);

        String configUrl = ResConfig.getCdnPreferredUrl(context) + "config/areaInfo.json?v=" + System.currentTimeMillis();
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
        cfgFileRequest.excute();
    }

}

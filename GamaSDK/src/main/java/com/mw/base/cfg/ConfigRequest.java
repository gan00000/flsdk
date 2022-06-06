//package com.gama.base.cfg;
//
//import android.content.Context;
//import android.text.TextUtils;
//
//import com.core.base.callback.ISReqCallBack;
//import com.core.base.request.CfgFileRequest;
//import com.core.base.bean.BaseReqeustBean;
//import com.core.base.utils.JsonUtil;
//import com.core.base.utils.PL;
//import com.core.base.utils.SStringUtil;
//import com.gama.base.bean.SGameLanguage;
//import com.gama.base.utils.GamaUtil;
//
//import org.json.JSONArray;
//
///**
// * Created by gan on 2017/2/14.
// */
//
//public class ConfigRequest{
//
////    域名/android/ifm/s_sdk_config.txt
////    域名/android/nonifm/s_sdk_config.txt
////    域名/ios/ifm/s_sdk_config.txt.txt
////    域名/ios/nonifm/s_sdk_config.txt.txt∂
//
//    public static void requestBaseCfg(final Context context){
//
//        CfgFileRequest cfgFileRequest = new CfgFileRequest(context);
//        BaseReqeustBean baseReqeustBean = new BaseReqeustBean(context);
//        final String gameCode = ResConfig.getGameCode(context.getApplicationContext());
//        String mUrl = null;
//        if (!ResConfig.isInfringement(context)) {
//            mUrl = ResConfig.getCdnPreferredUrl(context) + "android/nonifm/s_sdk_config.txt";
//        }else{
//            mUrl = ResConfig.getCdnPreferredUrl(context) + "android/ifm/s_sdk_config.txt";
//        }
//
//        baseReqeustBean.setCompleteUrl(mUrl);
//
//        cfgFileRequest.setBaseReqeustBean(baseReqeustBean);
//        cfgFileRequest.setReqCallBack(new ISReqCallBack() {
//            @Override
//            public void success(Object o, String rawResult) {
//                if (TextUtils.isEmpty(rawResult)){
//                    return;
//                }
//                rawResult = GamaUtil.decryptDyUrl(context,rawResult);
//                PL.i("sdk cfg:" + rawResult);
//                JSONArray a = JsonUtil.getArrayObjByKey(rawResult,"S_Is_Reload_Cfg");
//                if (a != null && a.length() > 0){
//                    for (int i = 0; i < a.length(); i++) {
//                        String n = a.optString(i);
//                        if (SStringUtil.isEqual(gameCode,n)){
//                            requestGameCodeCfg(context);
//                            return;
//                        }
//                    }
//                }
//                GamaUtil.saveSdkCfg(context,rawResult);
//            }
//
//            @Override
//            public void timeout(String code) {
//
//            }
//
//            @Override
//            public void noData() {
//
//            }
//
//            @Override
//            public void cancel() {
//
//            }
//        });
//        cfgFileRequest.excute();
//    }
//
//
//    public static void requestGameCodeCfg(final Context context){
//
//        CfgFileRequest cfgFileRequest = new CfgFileRequest(context);
//        BaseReqeustBean baseReqeustBean = new BaseReqeustBean(context);
//
//        String mUrl = ResConfig.getCdnPreferredUrl(context) + "android/" + ResConfig.getGameCode(context) + "/s_sdk_config.txt";
//
//        baseReqeustBean.setCompleteUrl(mUrl);
//
//        cfgFileRequest.setBaseReqeustBean(baseReqeustBean);
//        cfgFileRequest.setReqCallBack(new ISReqCallBack() {
//            @Override
//            public void success(Object o, String rawResult) {
//                rawResult = GamaUtil.decryptDyUrl(context,rawResult);
//                PL.d("sdk cfg:" + rawResult);
//
//                if (!TextUtils.isEmpty(rawResult)) {
//                    GamaUtil.saveSdkCfg(context,rawResult);
//                }
//            }
//
//            @Override
//            public void timeout(String code) {
//
//            }
//
//            @Override
//            public void noData() {
//
//            }
//
//            @Override
//            public void cancel() {
//
//            }
//        });
//        cfgFileRequest.excute();
//    }
//
//    /**
//     * 服务条款配置
//     * @param context
//     */
//    public static void requestTermsCfg(final Context context){
//
//        CfgFileRequest cfgFileRequest = new CfgFileRequest(context);
//        BaseReqeustBean baseReqeustBean = new BaseReqeustBean(context);
//
//        String mUrl = null;
//        String lanuage = ResConfig.getGameLanguage(context);
//        if (SStringUtil.isEmpty(lanuage)){
//            lanuage = SGameLanguage.zh_TW.getLanguage();
//        }
////        if (!ResConfig.isInfringement(context)) {
////            mUrl = ResConfig.getCdnPreferredUrl(context) + "android/nonifm/" + lanuage + "/s_sdk_login_terms.txt";
////        }else{
////            mUrl = ResConfig.getCdnPreferredUrl(context) + "android/ifm/" + lanuage + "/s_sdk_login_terms.txt";
////        }
//        // TODO: 2018/4/10 用户条款地址
//        mUrl = "https://www.gamamobi.com/member/archive.html";
//
//        baseReqeustBean.setCompleteUrl(mUrl);
//        cfgFileRequest.setBaseReqeustBean(baseReqeustBean);
//        cfgFileRequest.setReqCallBack(new ISReqCallBack() {
//            @Override
//            public void success(Object o, String rawResult) {
//                PL.d("sdk cfg:" + rawResult);
//                GamaUtil.saveSdkLoginTerms(context,rawResult);
//            }
//
//            @Override
//            public void timeout(String code) {
//
//            }
//
//            @Override
//            public void noData() {
//
//            }
//
//            @Override
//            public void cancel() {
//
//            }
//        });
//        cfgFileRequest.excute();
//    }
//
//
//}

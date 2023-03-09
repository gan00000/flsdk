package com.mw.sdk.out;

import android.app.Activity;

import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.google.gson.Gson;
import com.mw.base.bean.SLoginType;
import com.mw.sdk.login.ILoginCallBack;
import com.mw.sdk.login.QooAppLoginView;
import com.mw.sdk.login.model.QooAppLoginModel;
import com.mw.sdk.login.model.request.ThirdLoginRegRequestBean;
import com.mw.sdk.login.p.LoginPresenterImpl;
import com.mw.sdk.pay.IPayCallBack;
import com.mw.sdk.pay.gp.bean.req.GooglePayCreateOrderIdReqBean;
import com.mw.sdk.pay.gp.bean.res.BasePayBean;
import com.qooapp.opensdk.QooAppOpenSDK;
import com.qooapp.opensdk.common.QooAppCallback;
import com.thirdlib.qooapp.QooappPayImpl;

public class MWSdkImpl extends BaseSdkImpl {

    private static final String TAG = MWSdkImpl.class.getSimpleName();

    QooAppLoginView qooAppLoginView;

    GooglePayCreateOrderIdReqBean createOrderIdReqBean;

    QooappPayImpl qooappPay;

    @Override
    public void onCreate(Activity activity) {
        super.onCreate(activity);

        // you can use this way to init QooAppOpenSDK.
        // you must provide params in AndroidManifest.xml
        QooAppOpenSDK.initialize(new QooAppCallback() {
            @Override
            public void onSuccess(String response) {
                PL.i("QooAppOpenSDK.initialize onSuccess:" + response);
                if (qooappPay == null) {
                    qooappPay = new QooappPayImpl(activity);
                }
            }

            @Override
            public void onError(String error) {
                PL.i("QooAppOpenSDK.initialize onError:" + error);
            }
        }, activity);

    }

    @Override
    public void registerRoleInfo(Activity activity, String roleId, String roleName, String roleLevel, String vipLevel, String severCode, String serverName) {
        super.registerRoleInfo(activity, roleId, roleName, roleLevel, vipLevel, severCode, serverName);

        if (qooappPay != null){
            qooappPay.getOwnedPurchases(activity);
        }
    }

    @Override
    public void login(Activity activity, ILoginCallBack iLoginCallBack) {

        PL.i("sdk login");
        this.activity = activity;
        qooAppLoginView = new QooAppLoginView();
        qooAppLoginView.setiLoginCallBack(iLoginCallBack);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                qooAppLogin(activity);
            }
        });

    }

    private void qooAppLogin(Activity activity){

        QooAppOpenSDK.getInstance().login(new QooAppCallback() {
            @Override
            public void onSuccess(String response) {
                PL.i("QooAppOpenSDK.login onSuccess:" + response);
                if (SStringUtil.isEmpty(response)){
                    return;
                }
                Gson gson = new Gson();
                QooAppLoginModel qooAppLoginModel = gson.fromJson(response, QooAppLoginModel.class);
                if (qooAppLoginModel != null){
                    if (qooAppLoginModel.getData() != null){

                        LoginPresenterImpl loginPresenter = new LoginPresenterImpl();
                        loginPresenter.setBaseView(qooAppLoginView);

                        ThirdLoginRegRequestBean thirdLoginRegRequestBean = new ThirdLoginRegRequestBean(activity);
                        thirdLoginRegRequestBean.setThirdPlatId(qooAppLoginModel.getData().getUser_id());
                        thirdLoginRegRequestBean.setRegistPlatform(SLoginType.LOGIN_TYPE_QooApp);
                        thirdLoginRegRequestBean.setThirdAccount("");

                        loginPresenter.thirdPlatLogin(activity, thirdLoginRegRequestBean);
                    }
                }

            }

            @Override
            public void onError(String error) {
                PL.i("QooAppOpenSDK.login onError:" + error);
                ToastUtils.toast(activity, "" + error);
            }
        }, activity);
    }

    @Override
    public void switchLogin(Activity activity, ILoginCallBack iLoginCallBack) {

        QooAppOpenSDK.getInstance().logout(new QooAppCallback() {

            @Override
            public void onSuccess(String s) {
                PL.i("QooAppOpenSDK.logout onSuccess:" + s);
                login(activity, iLoginCallBack);
            }

            @Override
            public void onError(String s) {
                PL.i("QooAppOpenSDK.logout onError:" + s);
                ToastUtils.toast(activity,"QooApp logout error");
            }
        }, activity);
    }

    @Override
    protected void doQooAppPay(Activity activity, GooglePayCreateOrderIdReqBean createOrderIdReqBean) {
        super.doQooAppPay(activity, createOrderIdReqBean);

        this.createOrderIdReqBean = createOrderIdReqBean;

        if (qooappPay != null) {

            qooappPay.setPayCallBack(new IPayCallBack() {
                @Override
                public void success(BasePayBean basePayBean) {
                    if (iPayListener != null){
                        iPayListener.onPaySuccess(basePayBean.getProductId(), basePayBean.getCpOrderId());
                    }
                }

                @Override
                public void fail(BasePayBean basePayBean) {
                    if (iPayListener != null){
                        iPayListener.onPayFail();
                    }
                }

                @Override
                public void cancel(String msg) {
                    if (iPayListener != null){
                        iPayListener.onPayFail();
                    }
                }
            });
            qooappPay.startPay(activity, createOrderIdReqBean);
        }
    }


    @Override
    public void shareLine(Activity activity, String content, ISdkCallBack iSdkCallBack) {
    }

    @Override
    public void shareFacebook(Activity activity, String hashTag, String message, String shareLinkUrl, ISdkCallBack iSdkCallBack) {
    }

    @Override
    public void share(Activity activity, String hashTag, String message, String shareLinkUrl, ISdkCallBack iSdkCallBack) {
    }

    @Override
    public void share(Activity activity, ThirdPartyType type, String hashTag, String message, String shareLinkUrl, String picPath, ISdkCallBack iSdkCallBack) {
    }

}

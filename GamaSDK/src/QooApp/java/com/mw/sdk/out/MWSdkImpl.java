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
import com.qooapp.opensdk.QooAppOpenSDK;
import com.qooapp.opensdk.common.QooAppCallback;

public class MWSdkImpl extends BaseSdkImpl {

    private static final String TAG = MWSdkImpl.class.getSimpleName();

    QooAppLoginView qooAppLoginView;

    @Override
    public void onCreate(Activity activity) {
        super.onCreate(activity);

        // you can use this way to init QooAppOpenSDK.
        // you must provide params in AndroidManifest.xml
        QooAppOpenSDK.initialize(new QooAppCallback() {
            @Override
            public void onSuccess(String response) {
                PL.i("QooAppOpenSDK.initialize onSuccess:" + response);
            }

            @Override
            public void onError(String error) {
                PL.i("QooAppOpenSDK.initialize onError:" + error);
            }
        }, activity);

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
}

package com.gama.sdk.login.widget.v2.age.impl;

import android.content.Context;
import android.text.TextUtils;

import com.core.base.bean.BaseResponseModel;
import com.core.base.callback.ISReqCallBack;
import com.core.base.utils.PL;
import com.core.base.utils.ToastUtils;
import com.gama.base.bean.restful.GamaRestfulRequestBean;
import com.gama.base.excute.GamaAgeValidationTask;
import com.gama.base.excute.GamaUserUpdateMessageTask;
import com.gama.base.utils.GamaUtil;
import com.gama.sdk.R;
import com.gama.sdk.SBaseDialog;
import com.gama.sdk.login.widget.v2.GamaAgeStyleOne;
import com.gama.sdk.login.widget.v2.age.GamaAgeStyleThree;
import com.gama.sdk.login.widget.v2.age.GamaAgeStyleTwo;
import com.gama.sdk.login.widget.v2.age.IGamaAgePresenter;
import com.gama.sdk.login.widget.v2.age.callback.GamaAgeCallback;
import com.gama.sdk.utils.DialogUtil;

public class GamaAgeImpl implements IGamaAgePresenter {
    private int age = 0;
    private GamaAgeStyleOne styleOne;
    private GamaAgeStyleTwo styleTwo;
    private GamaAgeStyleThree styleThree;
    private GamaAgeCallback ageCallback;

    public GamaAgeImpl() {
    }

    @Override
    public void sendAgeRequest(final Context context, final SBaseDialog dialog) {
        GamaRestfulRequestBean requestBean = new GamaRestfulRequestBean(context);
        requestBean.setAge(getAge() + "");
        GamaUserUpdateMessageTask task = new GamaUserUpdateMessageTask(context, requestBean);
        task.setLoadDialog(DialogUtil.createLoadingDialog(context,"Loading..."));
        task.setReqCallBack(new ISReqCallBack<BaseResponseModel>() {
            @Override
            public void success(BaseResponseModel model, String rawResult) {
                PL.i("sendAge : " + model.getCode() + " - " + model.getMessage());
                if("1000".equals(model.getCode())) {
                    //保存年龄和时间
                    GamaUtil.saveAgeAndTime(context, getAge());
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    if(ageCallback != null) {
                        ageCallback.onSuccess();
                    }
                } else if(!TextUtils.isEmpty(model.getMessage())) {
                    ToastUtils.toast(context, model.getMessage());
                }
            }

            @Override
            public void timeout(String code) {
                ToastUtils.toast(context, R.string.py_error_time_out);
            }

            @Override
            public void noData() {
                ToastUtils.toast(context, R.string.py_error_occur);
            }

            @Override
            public void cancel() {

            }
        });
        task.excute(BaseResponseModel.class);
    }

    @Override
    public void requestAgeLimit(final Context context) {
        GamaAgeValidationTask task = new GamaAgeValidationTask(context, new GamaRestfulRequestBean(context));
        task.setLoadDialog(DialogUtil.createLoadingDialog(context,"Loading..."));
        task.setReqCallBack(new ISReqCallBack<BaseResponseModel>() {
            @Override
            public void success(BaseResponseModel model, String rawResult) {
                PL.i("requestAgeLimit : " + model.getCode() + " - " + model.getMessage());
                if("1000".equals(model.getCode())) {
                    if(ageCallback != null) {
                        ageCallback.canBuy();
                    }
                } else {
                    goAgeStyleOne(context);
                }
            }

            @Override
            public void timeout(String code) {
                ToastUtils.toast(context, R.string.py_error_time_out);
                if(ageCallback != null) {
                    ageCallback.onFailure();
                }
            }

            @Override
            public void noData() {
                ToastUtils.toast(context, R.string.py_error_occur);
                if(ageCallback != null) {
                    ageCallback.onFailure();
                }
            }

            @Override
            public void cancel() {
                if(ageCallback != null) {
                    ageCallback.onFailure();
                }
            }
        });
        task.excute(BaseResponseModel.class);
    }

    @Override
    public void goAgeStyleOne(Context context) {
        if (styleOne == null) {
            styleOne = new GamaAgeStyleOne(context, this);
        }
        styleOne.show();
    }

    @Override
    public void goAgeStyleTwo(Context context) {
        if (styleTwo == null) {
            styleTwo = new GamaAgeStyleTwo(context, this);
        }
        styleTwo.show();
    }

    @Override
    public void goAgeStyleThree(Context context) {
        if (styleThree == null) {
            styleThree = new GamaAgeStyleThree(context, this);
        }
        styleThree.show();
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public GamaAgeCallback getAgeCallback() {
        return ageCallback;
    }

    public void setAgeCallback(GamaAgeCallback ageCallback) {
        this.ageCallback = ageCallback;
    }
}

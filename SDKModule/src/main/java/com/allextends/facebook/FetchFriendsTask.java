package com.allextends.facebook;

import android.content.Context;

import com.mybase.bean.BaseReqModel;
import com.mybase.request.AbsHttpRequest;
import com.mybase.utils.PL;
import com.mybase.utils.ToastUtils;

public class FetchFriendsTask extends AbsHttpRequest {

    private String url;
    private Context context;

    public FetchFriendsTask(Context context, String url) {
        this.context = context;
        this.url = url;

        setGetMethod(true, false);
    }


    @Override
    public BaseReqModel createRequestBean() {

        BaseReqModel baseReqeustBean = new BaseReqModel();
        baseReqeustBean.setCompleteUrl(url);

        return baseReqeustBean;
    }

    @Override
    public <T> void onHttpSucceess(T responseModel) {

    }

    @Override
    public void onTimeout(String result) {
        PL.i("onTimeout");
        ToastUtils.toast(context, "connect timeout, please try again");
    }

    @Override
    public void onNoData(String result) {
        PL.i("onNoData");
    }
}

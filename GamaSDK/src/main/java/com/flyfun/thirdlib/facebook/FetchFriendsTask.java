package com.flyfun.thirdlib.facebook;

import android.content.Context;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.request.AbsHttpRequest;
import com.core.base.utils.PL;
import com.core.base.utils.ToastUtils;

public class FetchFriendsTask extends AbsHttpRequest {

    private String url;
    private Context context;

    public FetchFriendsTask(Context context, String url) {
        this.context = context;
        this.url = url;

        setGetMethod(true, false);
    }


    @Override
    public BaseReqeustBean createRequestBean() {

        BaseReqeustBean baseReqeustBean = new BaseReqeustBean();
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

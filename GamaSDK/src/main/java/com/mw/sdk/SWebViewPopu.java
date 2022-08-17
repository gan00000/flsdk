package com.mw.sdk;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.core.base.BaseWebChromeClient;
import com.core.base.BaseWebViewClient;
import com.mw.base.widget.SWebView;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ScreenHelper;
import com.core.base.utils.ToastUtils;

/**
 * Created by gan on 2017/3/31.
 */

public class SWebViewPopu extends SBasePopu {

    Activity activity;
    private ProgressBar progressBar;
    private SWebView sWebView;
    RelativeLayout relativeLayout;
//    String webUrl = "https://www.baidu.com";

    public SWebViewPopu(Context context) {
        super(context);
        activity = (Activity)context;
        initPop();
    }

    private void initPop(){

//        ScreenHelper screenHelper = new ScreenHelper(activity);

        this.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        relativeLayout = new RelativeLayout(activity);

    }

    public void showPop(String webUrl) {

        PL.i("SWebViewActivity url:" + webUrl);

        if (TextUtils.isEmpty(webUrl)){
            ToastUtils.toast(activity,"url error");
            PL.i("webUrl is empty");
            return;
        }
//        this.showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER,0,0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.showAsDropDown(activity.getWindow().getDecorView(),

                    (activity.getWindow().getDecorView().getWidth() - this.getWidth())/2,

                    this.getHeight() + (activity.getWindow().getDecorView().getHeight() - this.getHeight())/2
            );
        }else{
            this.showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER,0,0);
        }
        sWebView.loadUrl(webUrl);

    }




}

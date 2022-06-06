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

        ScreenHelper screenHelper = new ScreenHelper(activity);

        this.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        relativeLayout = new RelativeLayout(activity);


        int closeeRadius = getCloseRadius();
        View contentView = activity.getLayoutInflater().inflate(R.layout.s_web_view_with_title_layout,null);
        RelativeLayout.LayoutParams webviewLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        webviewLp.setMargins(0,closeeRadius,closeeRadius,0);
        relativeLayout.addView(contentView,webviewLp);

        ImageView closeImageView = new ImageView(activity);
        RelativeLayout.LayoutParams colseLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        colseLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        relativeLayout.addView(closeImageView,colseLp);
        closeImageView.setBackgroundResource(R.drawable.com_star_close);
        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        this.setContentView(relativeLayout);

        this.setWidth(screenHelper.getScreenWidth() - 100);
        this.setHeight(screenHelper.getScreenHeight()-80);

        this.setFocusable(true);
        this.setOutsideTouchable(true);

//        if (TextUtils.isEmpty(webUrl)){
//            ToastUtils.toast(activity,"url error");
//            PL.i("webUrl is empty");
//            dismiss();
//            return;
//        }

        initTitle("");

        progressBar = (ProgressBar) contentView.findViewById(R.id.s_webview_pager_loading_percent);
        sWebView = (SWebView) contentView.findViewById(R.id.s_webview_id);
        sWebView.setBaseWebChromeClient(new BaseWebChromeClient(progressBar, activity));
        sWebView.setWebViewClient(new BaseWebViewClient(activity));

        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                try {
                    sWebView.clearHistory();
                    sWebView.clearCache(true);
                    sWebView.destroy();
                    sWebView = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

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

    private void initTitle(String webTitle) {

        View titleLayout = relativeLayout.findViewById(R.id.py_title_layout_id);
        if (SStringUtil.isNotEmpty(webTitle)){
            titleLayout.setVisibility(View.VISIBLE);
            titleLayout.setBackgroundResource(R.drawable.gama_title_sdk_bg);
        }else{
            titleLayout.setVisibility(View.GONE);
            return;
        }

        relativeLayout.findViewById(R.id.py_back_button).setVisibility(View.GONE);
        TextView titleTextView = (TextView) relativeLayout.findViewById(R.id.py_title_id);
        titleTextView.setText(webTitle);
        View rightCloseView = relativeLayout.findViewById(R.id.py_title_right_button);
        rightCloseView.setVisibility(View.VISIBLE);
        rightCloseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }


    private int getCloseRadius(){
        return activity.getResources().getDrawable(R.drawable.com_star_close).getIntrinsicWidth()/2;
    }


}

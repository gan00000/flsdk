package com.ldy.sdk;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.mybase.BaseWebChromeClient;
import com.mybase.BaseWebViewClient;
import com.ldy.base.widget.SWebView;
import com.mybase.utils.SStringUtil;

/**
 * SWebViewDialog的内容布局
 * Created by gan on 2017/4/7.
 */

public class SWebViewLayout extends SBaseRelativeLayout {


    private Activity activity;
    private ProgressBar progressBar;

    private SWebView sWebView;
    private ImageView closeImageView;
    private ImageView backImageView;
    private TextView titleTv;
    private View rl_sdk_sweb_header;

    public SWebView getsWebView() {
        return sWebView;
    }

    public ImageView getCloseImageView() {
        return closeImageView;
    }

    public ImageView getBackImageView() {
        return backImageView;
    }

    public SWebViewLayout(Context context) {
        super(context);
        activity = (Activity)context;
        initView();
    }

    public SWebViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        activity = (Activity)context;
        initView();
    }

    public SWebViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        activity = (Activity)context;
        initView();
    }

    public SWebViewLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        activity = (Activity)context;
        initView();
    }

    public View getTitleHeaderView(){
        return rl_sdk_sweb_header;
    }

    private void initView(){

//        int closeeRadius = getCloseRadius();
        View contentView = activity.getLayoutInflater().inflate(R.layout.sady_gutturably6305,null);
        RelativeLayout.LayoutParams webviewLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//        webviewLp.setMargins(closeeRadius,closeeRadius,closeeRadius,closeeRadius);
        this.addView(contentView,webviewLp);

//        closeImageView = new ImageView(activity);
//        RelativeLayout.LayoutParams colseLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        colseLp.addRule(ALIGN_PARENT_RIGHT);
//        this.addView(closeImageView,colseLp);
////        closeImageView.setBackgroundResource(R.drawable.sady_thousand5305);
//        closeImageView.setImageDrawable(getResources().getDrawable(R.drawable.sady_thousand5305));

        closeImageView = contentView.findViewById(R.id.mId_odorlike_fandit);
        titleTv = contentView.findViewById(R.id.mId_exterain_nameial);
        backImageView = contentView.findViewById(R.id.mId_carcin_variousature);

        rl_sdk_sweb_header = contentView.findViewById(R.id.mId_symose_cystid);
        initTitle("");

        progressBar = (ProgressBar) contentView.findViewById(R.id.mId_tentage_design);
        sWebView = (SWebView) contentView.findViewById(R.id.mId_tradeon_butacle);

        sWebView.setBaseWebChromeClient(new MyWebChromeClient(progressBar, activity));
        sWebView.setWebViewClient(new BaseWebViewClient(activity));


        backImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sWebView != null && sWebView.canGoBack()){
                    sWebView.goBack();
                }
            }
        });
    }

    private int getCloseRadius(){
        return activity.getResources().getDrawable(R.drawable.sady_thousand5305).getIntrinsicWidth()/3;
    }

    private void initTitle(String webTitle) {

    }

    public class MyWebChromeClient extends BaseWebChromeClient{

        public MyWebChromeClient(ProgressBar progressBar, Activity activity) {
            super(progressBar, activity);
        }

        public MyWebChromeClient(ProgressBar progressBar, Fragment fragment) {
            super(progressBar, fragment);
        }

        public MyWebChromeClient(Activity activity) {
            super(activity);
        }

        public MyWebChromeClient() {
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (SStringUtil.isNotEmpty(title)){
                titleTv.setText(title.trim());
            }
        }
    }

//    public class MyWebViewClient extends BaseWebViewClient {
//
//        public MyWebViewClient(Activity activity) {
//            super(activity);
//        }
//
//        @Override
//        public void onPageFinished(WebView view, String url) {
//            super.onPageFinished(view, url);
//
//            if (view.canGoBack()){
//                backImageView.setVisibility(VISIBLE);
//            }else {
//                backImageView.setVisibility(GONE);
//            }
//        }
//    }
}

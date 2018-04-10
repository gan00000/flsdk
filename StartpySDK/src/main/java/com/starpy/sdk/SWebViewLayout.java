package com.starpy.sdk;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.core.base.BaseWebChromeClient;
import com.core.base.BaseWebViewClient;
import com.core.base.SWebView;
import com.core.base.utils.SStringUtil;

/**
 * Created by gan on 2017/4/7.
 */

public class SWebViewLayout extends SBaseRelativeLayout {


    private Activity activity;
    private ProgressBar progressBar;

    private SWebView sWebView;
    private ImageView closeImageView;

    public SWebView getsWebView() {
        return sWebView;
    }

    public ImageView getCloseImageView() {
        return closeImageView;
    }


    public SWebViewLayout(Context context) {
        super(context);
        activity = (Activity)context;
        initView();
    }

    public SWebViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SWebViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void initView(){

        int closeeRadius = getCloseRadius();
        View contentView = activity.getLayoutInflater().inflate(R.layout.s_web_view_with_title_layout,null);
        RelativeLayout.LayoutParams webviewLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        webviewLp.setMargins(closeeRadius,closeeRadius,closeeRadius,closeeRadius);
        this.addView(contentView,webviewLp);

        closeImageView = new ImageView(activity);
        RelativeLayout.LayoutParams colseLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        colseLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        this.addView(closeImageView,colseLp);
//        closeImageView.setBackgroundResource(R.drawable.com_star_close);
        closeImageView.setImageDrawable(getResources().getDrawable(R.drawable.com_star_close));

        initTitle("");

        progressBar = (ProgressBar) contentView.findViewById(R.id.s_webview_pager_loading_percent);
        sWebView = (SWebView) contentView.findViewById(R.id.s_webview_id);

        sWebView.setBaseWebChromeClient(new BaseWebChromeClient(progressBar, activity));
        sWebView.setWebViewClient(new BaseWebViewClient(activity));

    }

    private int getCloseRadius(){
        return activity.getResources().getDrawable(R.drawable.com_star_close).getIntrinsicWidth()/3;
    }

    private void initTitle(String webTitle) {

        View titleLayout = this.findViewById(R.id.py_title_layout_id);
        if (SStringUtil.isNotEmpty(webTitle)){
            titleLayout.setVisibility(View.VISIBLE);
            titleLayout.setBackgroundResource(R.drawable.py_title_sdk_bg);
        }else{
            titleLayout.setVisibility(View.GONE);
            return;
        }

        this.findViewById(R.id.py_back_button).setVisibility(View.GONE);
        TextView titleTextView = (TextView) this.findViewById(R.id.py_title_id);
        titleTextView.setText(webTitle);
        View rightCloseView = this.findViewById(R.id.py_title_right_button);
        rightCloseView.setVisibility(View.VISIBLE);
        rightCloseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }
}

package com.starpy.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;

import com.core.base.SWebView;
import com.core.base.utils.PL;
import com.core.base.utils.ToastUtils;

/**
 * Created by gan on 2017/3/31.
 */

public class SWebViewDialog extends SBaseDialog {

    private Activity activity;
    private ProgressBar progressBar;
    private SWebView sWebView;

   private SWebViewLayout sWebViewLayout;

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    private String webUrl = "";

    public SWebViewDialog(@NonNull Context context) {
        super(context);
        this.activity = (Activity)context;
    }

    public SWebViewDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.activity = (Activity)context;
    }

    protected SWebViewDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.activity = (Activity)context;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        PL.i("Dialog onBackPressed");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PL.i("Dialog onCreate");

        PL.i("SWebViewActivity url:" + webUrl);
        if (TextUtils.isEmpty(webUrl)){
            ToastUtils.toast(getContext(),"url error");
            PL.i("webUrl is empty");
            dismiss();
            return;
        }

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (sWebView != null){
                    sWebView.clearCache(true);
                    sWebView.clearHistory();
                    sWebView.destroy();
                    sWebView = null;
                    PL.i("dialog destory webview");
                }
            }
        });

        sWebViewLayout = new SWebViewLayout(activity);
        this.setContentView(sWebViewLayout);
        sWebView = sWebViewLayout.getsWebView();
        sWebViewLayout.getCloseImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        if (sWebView != null) {
            sWebView.loadUrl(webUrl);
        }

        sWebViewLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                PL.d("onGlobalLayout...");
                possiblyResizeChildOfContent();
            }
        });

        viewGroupLayoutParams = (ViewGroup.LayoutParams) sWebViewLayout.getLayoutParams();
        originalHeight = viewGroupLayoutParams.height;
    }

    private int usableHeightPrevious;
    private ViewGroup.LayoutParams viewGroupLayoutParams;

    private int originalHeight;

    private void possiblyResizeChildOfContent() {//全屏状态下webView输入框被遮挡的问题，通过这个方法重设view高度勉强解决
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = sWebViewLayout.getRootView().getHeight();
            PL.d("usableHeightSansKeyboard:" + usableHeightSansKeyboard);
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard / 3)) {
                // keyboard probably just became visible
                viewGroupLayoutParams.height = usableHeightSansKeyboard - heightDifference;
            } else {
                // keyboard probably just became hidden
                viewGroupLayoutParams.height = originalHeight;
            }
            sWebViewLayout.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        sWebViewLayout.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);// 全屏模式下： return r.bottom
    }


    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        PL.i("Dialog onAttachedToWindow");
    }

    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (sWebView != null && isShowing()){
            sWebView.onActivityResult(requestCode, resultCode, data);
        }
    }

}

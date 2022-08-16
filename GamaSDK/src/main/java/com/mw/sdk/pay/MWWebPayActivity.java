package com.mw.sdk.pay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.gama.pay.IPay;
import com.gama.pay.IPayCallBack;
import com.gama.pay.IPayFactory;
import com.gama.pay.gp.bean.req.GooglePayCreateOrderIdReqBean;
import com.gama.pay.gp.bean.res.BasePayBean;
import com.mw.base.widget.SWebView;
import com.core.base.utils.PL;
import com.core.base.utils.ToastUtils;
import com.mw.sdk.R;
import com.mw.sdk.SBaseSdkActivity;
import com.mw.sdk.SWebViewLayout;
import com.mw.sdk.ads.SdkEventLogger;
import com.mw.sdk.out.BaseSdkImpl;

/**
 * Created by GanYuanrong on 2016/12/1.
 */

public class MWWebPayActivity extends SBaseSdkActivity {

    public static final String PLAT_WEBVIEW_URL = "PLAT_WEBVIEW_URL";
    public static final String PLAT_WEBVIEW_TITLE = "PLAT_WEBVIEW_TITLE";
    private String webUrl;
    private String webTitle;
//    private ProgressBar progressBar;

    private SWebViewLayout sWebViewLayout;
    private SWebView sWebView;
    private View backView;

    private WebPayJs webPayJs;

    private IPay iPay;
    private String productId;
    private String cpOrderId;
    private String extra;

    public static Intent create(Activity activity, String title, String url, String cpOrderId, String productId, String extra){

        Intent intent = new Intent(activity, MWWebPayActivity.class);
        intent.putExtra(PLAT_WEBVIEW_TITLE,title);
        intent.putExtra(PLAT_WEBVIEW_URL,url);
        intent.putExtra("mw_cpOrderId",cpOrderId);
        intent.putExtra("mw_productId",productId);
        intent.putExtra("mw_extra",extra);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.s_web_act_layout);

        if (getIntent() != null) {
            webUrl = getIntent().getStringExtra(PLAT_WEBVIEW_URL);
            webTitle = getIntent().getStringExtra(PLAT_WEBVIEW_TITLE);
            cpOrderId = getIntent().getStringExtra("mw_cpOrderId");
            productId = getIntent().getStringExtra("mw_productId");
            extra = getIntent().getStringExtra("mw_extra");
//            initTitle(webTitle);
        }

        sWebViewLayout = findViewById(R.id.sl_web_act);
        sWebView = sWebViewLayout.getsWebView();
        backView = sWebViewLayout.getBackImageView();

        sWebViewLayout.getCloseImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        webPayJs = new WebPayJs(this);
        sWebView.addJavascriptInterface(webPayJs,"SdkObj");
        if (TextUtils.isEmpty(webUrl)){
            ToastUtils.toast(getApplicationContext(),"url error");
            PL.i("webUrl is empty");
        }else{
            sWebView.loadUrl(webUrl);
//            sWebView.loadUrl("https://play.google.com/store/apps/details?id=tw.com.iwplay.tlbbhk&hl=zh-CN");
        }
        iPay = IPayFactory.create(IPayFactory.PAY_GOOGLE);
        iPay.onCreate(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (iPay != null){
            iPay.onResume(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (sWebView != null){
            sWebView.onActivityResult(requestCode, resultCode, data);
        }
        if (iPay != null){
            iPay.onActivityResult(this,requestCode,resultCode,data);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sWebView != null){
            sWebView.clearCache(true);
            sWebView.clearHistory();
            sWebView.destroy();
            sWebView = null;
            PL.i("dialog destory webview");
        }
        if (iPay != null){
            iPay.onDestroy(this);
        }
    }

    private void doGooglePay(Activity activity, GooglePayCreateOrderIdReqBean googlePayCreateOrderIdReqBean) {

        //设置Google储值的回调
        iPay.setIPayCallBack(new IPayCallBack() {
            @Override
            public void success(BasePayBean basePayBean) {
                PL.i("IPayCallBack success");
                ToastUtils.toast(activity,R.string.text_finish_pay);

                SdkEventLogger.trackinPayEvent(activity, basePayBean);

//                if (iPayListener != null) {
//                    iPayListener.onPaySuccess(basePayBean.getProductId(),basePayBean.getCpOrderId());
//                }
            }

            @Override
            public void fail(BasePayBean basePayBean) {
//                if (iPayListener != null) {
//                    iPayListener.onPayFail();
//                }
            }
        });

        iPay.startPay(activity, googlePayCreateOrderIdReqBean);
    }

    public void googlePay(String productId){

        GooglePayCreateOrderIdReqBean googlePayCreateOrderIdReqBean = new GooglePayCreateOrderIdReqBean(this);
        googlePayCreateOrderIdReqBean.setCpOrderId(cpOrderId);
        googlePayCreateOrderIdReqBean.setProductId(productId);
        googlePayCreateOrderIdReqBean.setExtra(extra);

        doGooglePay(this,googlePayCreateOrderIdReqBean);
    }

    public void payFinish(boolean success,String productId){

        if (backView != null){
            backView.setVisibility(View.GONE);
        }

        if (success){

        }else {
//            this.finish();
        }
    }
}

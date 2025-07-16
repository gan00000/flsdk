package com.mw.sdk.login.widget.v2;//package com.gama.sdk.login.widget.v2;
//
//import android.content.Context;
//import android.text.TextUtils;
//import android.util.AttributeSet;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.webkit.WebChromeClient;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//import android.widget.TextView;
//
//import com.gama.base.cfg.ResConfig;
//import com.gama.base.utils.GamaUtil;
//import com.mw.sdk.R;
//import com.gama.sdk.login.widget.SLoginBaseRelativeLayout;
//
///**
// * Created by GanYuanrong on 2017/2/6.
// */
//
//public class AccountRegisterTermsLayoutV2 extends SLoginBaseRelativeLayout {
//
//    private View contentView;
//    private WebView termsView;
//
//    public AccountRegisterTermsLayoutV2(Context context) {
//        super(context);
//    }
//
//    public AccountRegisterTermsLayoutV2(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public AccountRegisterTermsLayoutV2(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//
//    public View onCreateView(LayoutInflater inflater) {
//        contentView = inflater.inflate(R.layout.v2_gama_account_register_terms, null);
//        backView = contentView.findViewById(R.id.py_back_button);
//
//        termsView = (WebView) contentView.findViewById(R.id.gama_terms);
//        termsView.clearCache(true);
//        termsView.setWebChromeClient(new WebChromeClient());
//        termsView.setWebViewClient(new WebViewClient());
//        String termsUrl = ResConfig.getRegisterTermUrl(getContext());
//        termsView.loadUrl(termsUrl);
//
//        backView.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                switch (from){
//                    case 1:
//                        sLoginDialogv2.toRegisterView(0);
//                        break;
//                    case 2:
//                        sLoginDialogv2.toBindUniqueView();
//                        break;
//                    case 3:
//                        sLoginDialogv2.toBindFbView();
//                        break;
//                    default:
//                        sLoginDialogv2.toRegisterView(0);
//                        break;
//                }
//            }
//        });
//
////        String serverTermsContent = GamaUtil.getSdkLoginTerms(getContext());//优先设置服务器获取的配置
////        if (!TextUtils.isEmpty(serverTermsContent)){
////            termsTextView.setText(serverTermsContent);
////        }
//
//        return contentView;
//    }
//
//
//
//    @Override
//    protected View createView(Context context, LayoutInflater layoutInflater) {
//        return onCreateView(layoutInflater);
//    }
//
//}

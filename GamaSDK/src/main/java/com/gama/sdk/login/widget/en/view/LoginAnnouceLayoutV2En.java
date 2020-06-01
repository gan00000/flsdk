package com.gama.sdk.login.widget.en.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.gama.base.constant.GsCommonSwitchType;
import com.gama.base.utils.GamaUtil;
import com.gama.data.login.response.SLoginResponse;
import com.gama.sdk.R;
import com.gama.sdk.login.widget.GsAnnouceWebView;
import com.gama.sdk.login.widget.en.view.base.SLoginBaseRelativeLayoutEn;

/**
 * Created by GanYuanrong on 2017/2/6.
 */

public class LoginAnnouceLayoutV2En extends SLoginBaseRelativeLayoutEn implements View.OnClickListener {

    private View contentView;
    private GsAnnouceWebView annouceView;
    private Button closeBtn;
    private String annouceUrl;
    private SLoginResponse sLoginResponse;

    public LoginAnnouceLayoutV2En(Context context, SLoginResponse sLoginResponse) {
        super(context);
        this.sLoginResponse = sLoginResponse;
    }

    public LoginAnnouceLayoutV2En(Context context, AttributeSet attrs, SLoginResponse sLoginResponse) {
        super(context, attrs);
        this.sLoginResponse = sLoginResponse;
    }

    public LoginAnnouceLayoutV2En(Context context, AttributeSet attrs, int defStyleAttr, SLoginResponse sLoginResponse) {
        super(context, attrs, defStyleAttr);
        this.sLoginResponse = sLoginResponse;
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }

    private View onCreateView(LayoutInflater inflater) {
        contentView = inflater.inflate(R.layout.v2_gama_annouce, null);

        backView = contentView.findViewById(R.id.gama_annouce_close);
        backView.setOnClickListener(this);

        annouceView = contentView.findViewById(R.id.gama_annouce_webview);
//        annouceView.setLayerType(View.LAYER_TYPE_SOFTWARE, null); //cavas.clipPath不支持硬件加速，设置不使用硬件加速。不然效果出不来.
        annouceUrl = GamaUtil.getSwitchUrlWithType(getContext(), GsCommonSwitchType.ANNOUCE);
        if(!TextUtils.isEmpty(annouceUrl)) {
            annouceView.loadUrl(annouceUrl);
        }

        return contentView;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onClick(View v) {

        if (v == backView) {
            sLoginDialogv2.LoginSuccess(sLoginResponse);
        }

    }

}

package com.gama.sdk.login.widget.en.view.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gama.base.utils.Localization;
import com.gama.sdk.SBaseRelativeLayout;
import com.gama.sdk.login.SLoginDialogV2;
import com.gama.sdk.login.widget.en.SLoginDialogV2En;

/**
 * Created by gan on 2017/4/12.
 */

public abstract class SLoginBaseRelativeLayoutEn extends SBaseRelativeLayout {

    Context context;
    LayoutInflater inflater;

    protected SLoginDialogV2En sLoginDialogv2;

    protected View backView;
    protected TextView titleTextView;
    public int from;

    public SLoginBaseRelativeLayoutEn(Context context) {
        super(context);

        initView(context);
    }

    public SLoginBaseRelativeLayoutEn(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(context);
    }

    public SLoginBaseRelativeLayoutEn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SLoginBaseRelativeLayoutEn(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);

    }

    private void initView(Context context) {
        this.context = context;

        Localization.updateSGameLanguage(getActivity());

        inflater = LayoutInflater.from(context);
        View contentView = createView(this.context, inflater);

        if (contentView != null) {

            LayoutParams l = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            l.addRule(RelativeLayout.CENTER_IN_PARENT);
            addView(contentView, l);
        }
    }

    protected Context getActivity() {
        return getContext();
    }

    protected abstract View createView(Context context, LayoutInflater layoutInflater);

    public void setLoginDialogV2(SLoginDialogV2En sLoginDialog) {
        this.sLoginDialogv2 = sLoginDialog;
    }

    /**
     * 各界面的返回按钮
     */
    public View getBackView() {
        return backView;
    }
}

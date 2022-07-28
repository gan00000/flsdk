package com.mw.sdk.login.widget.v2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.mw.base.cfg.ConfigBean;
import com.mw.base.utils.SdkUtil;
import com.mw.sdk.SBaseDialog;
import com.mw.sdk.login.constant.ViewType;
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;
import com.mw.sdk.R;
import com.mw.sdk.out.ISdkCallBack;

/**
 * Created by GanYuanrong on 2017/2/6.
 */

public class MainHomeLayout extends SLoginBaseRelativeLayout implements View.OnClickListener {

    private View contentView;

    private View layout_go_account_login, guestLoginView;
    private ImageView iv_login_google,iv_login_fb,iv_login_line;
    private CheckBox cb_agree_term;
    private View layout_go_term;
    private View layout_term;
    private ImageView iv_logo;

    public MainHomeLayout(Context context) {
        super(context);
    }

    public MainHomeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainHomeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }

    public View onCreateView(LayoutInflater inflater) {

        contentView = inflater.inflate(R.layout.mw_login_home, null);

        guestLoginView = contentView.findViewById(R.id.layout_guest_login);
        layout_go_account_login = contentView.findViewById(R.id.layout_go_account_login);
        iv_login_google = contentView.findViewById(R.id.iv_login_google);
        iv_login_fb = contentView.findViewById(R.id.iv_login_fb);
        iv_login_line = contentView.findViewById(R.id.iv_login_line);

        cb_agree_term = contentView.findViewById(R.id.cb_agree_term);
        layout_go_term = contentView.findViewById(R.id.layout_go_term);
        iv_logo = contentView.findViewById(R.id.iv_logo);
        layout_term = contentView.findViewById(R.id.layout_term);

        guestLoginView.setOnClickListener(this);
        layout_go_account_login.setOnClickListener(this);
        iv_login_google.setOnClickListener(this);
        iv_login_fb.setOnClickListener(this);
        iv_login_line.setOnClickListener(this);
        layout_go_term.setOnClickListener(this);

        cb_agree_term.setChecked(true);

//        String ssText = getContext().getString(R.string.gama_ui_term_port_read2);
//        SpannableString ss = new SpannableString(ssText);
//        ss.setSpan(new UnderlineSpan(), ssText.length() - 5, ssText.length(), Paint.UNDERLINE_TEXT_FLAG);
//        ss.setSpan(new ForegroundColorSpan(), ssText.length() - 5, ssText.length(), Paint.UNDERLINE_TEXT_FLAG);

        ConfigBean configBean = SdkUtil.getSdkCfg(getContext());
        if (configBean != null){
            ConfigBean.VersionData versionData = configBean.getSdkConfigLoginData(getContext());
            if (versionData != null){
                if(!versionData.isVisitorLogin()){//false隐藏图标
                    guestLoginView.setVisibility(View.GONE);
                }
                if(!versionData.isFbLogin()){
                    iv_login_fb.setVisibility(View.GONE);
                }
                if(!versionData.isGoogleLogin()){
                    iv_login_google.setVisibility(View.GONE);
                }
                if(!versionData.isLineLogin()){
                    iv_login_line.setVisibility(View.GONE);
                }
                if(!versionData.isShowContract()){
                    layout_term.setVisibility(View.GONE);
                }
                if (versionData.isShowLogo()){
                    iv_logo.setVisibility(View.VISIBLE);
                }
            }
        }
        return contentView;
    }



    @Override
    public void onClick(View v) {

        if (v == iv_login_fb){
            if (checkAgreeTerm()){
                sLoginDialogv2.getLoginPresenter().fbLogin(sLoginDialogv2.getActivity());
            }
        }else if (v == layout_go_account_login) {

            sLoginDialogv2.showLoginWithRegView(ViewType.HomeView);
        }else if(v == guestLoginView){
            if (checkAgreeTerm()){
                sLoginDialogv2.getLoginPresenter().guestLogin(sLoginDialogv2.getActivity());
            }
        }else if (v == iv_login_google){
            //google+登录
            if (checkAgreeTerm()){
                sLoginDialogv2.getLoginPresenter().googleLogin(sLoginDialogv2.getActivity());
            }

        }else if (v == iv_login_line){
            //google+登录
            if (checkAgreeTerm()){
                sLoginDialogv2.getLoginPresenter().lineLogin(sLoginDialogv2.getActivity());
            }

        }else if (v == layout_go_term){
//            sLoginDialogv2.showTermView(ViewType.HomeView);

            showTermDialog();
        }

    }

    private SBaseDialog termBaseDialog;
    private void showTermDialog() {

        if (termBaseDialog != null){
            termBaseDialog.show();
            return;
        }

        termBaseDialog = new SBaseDialog(getContext(), R.style.Sdk_Theme_AppCompat_Dialog_Notitle_Fullscreen);

        TermsViewV3 termsViewV3 = new TermsViewV3(getContext());
        termsViewV3.setiSdkCallBack(new ISdkCallBack() {
            @Override
            public void success() {
                cb_agree_term.setChecked(true);
                if (termBaseDialog != null) {
                    termBaseDialog.dismiss();
                }
            }

            @Override
            public void failure() {
                if (termBaseDialog != null) {
                    termBaseDialog.dismiss();
                }
            }
        });
        termBaseDialog.setContentView(termsViewV3);
        termBaseDialog.getWindow().setWindowAnimations(R.style.dialog_animation);
        termBaseDialog.show();
    }

    private boolean checkAgreeTerm(){
        if (cb_agree_term.isChecked()){
            return true;
        }
        toast(R.string.gama_ui_term_not_read);
        showTermDialog();
        return false;
    }
}

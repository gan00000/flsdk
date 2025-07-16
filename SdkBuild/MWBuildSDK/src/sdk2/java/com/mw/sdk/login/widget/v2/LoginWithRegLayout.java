package com.mw.sdk.login.widget.v2;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mw.sdk.bean.res.ConfigBean;
import com.mw.sdk.utils.SdkUtil;
import com.mw.sdk.login.SLoginDialogV2;
import com.mw.sdk.constant.SLoginType;
import com.mw.sdk.bean.AccountModel;
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;
import com.mw.sdk.R;

import java.util.List;

/**
 * Created by GanYuanrong on 2017/2/6.
 */

public class LoginWithRegLayout extends SLoginBaseRelativeLayout implements View.OnClickListener {

    private View contentView;
    private TextView loginTabView, regTabView;
    private View login_bottom_line,register_bottom_line,iv_login_reg_back;

    private LinearLayout ll_reg_login_title;

    public AccountLoginLayoutV2 getmAccountLoginV2() {
        return mAccountLoginV2;
    }
    private AccountLoginLayoutV2 mAccountLoginV2;
    private AccountRegisterLayoutV2 mAccountRegisterLayoutV2;

//    private ViewType fromViewType;
//
//    public void setFromViewType(ViewType fromViewType) {
//        this.fromViewType = fromViewType;
//    }

    Animation login_out_animation;
    Animation login_enter_animation;
    Animation reg_out_animation;
    Animation reg_enter_animation;

    public AccountRegisterLayoutV2 getmAccountRegisterLayoutV2() {
        return mAccountRegisterLayoutV2;
    }

    public LoginWithRegLayout(Context context) {
        super(context);
    }

    public LoginWithRegLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoginWithRegLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }

    public View onCreateView(LayoutInflater inflater) {

        contentView = inflater.inflate(R.layout.mw_login_reg, null);
        iv_login_reg_back = contentView.findViewById(R.id.iv_login_reg_back);
        loginTabView = contentView.findViewById(R.id.loginTabView);
        regTabView = contentView.findViewById(R.id.regTabView);
        login_bottom_line = contentView.findViewById(R.id.loginTabView_bottom_line);
        register_bottom_line = contentView.findViewById(R.id.regTabView_bottom_line);

        ll_reg_login_title = contentView.findViewById(R.id.ll_reg_login_title);
        mAccountLoginV2 = contentView.findViewById(R.id.pyAccountLoginV2Id);
        mAccountRegisterLayoutV2 = contentView.findViewById(R.id.accountRegisterLayoutV2Id);

        loginTabView.setOnClickListener(this);
        regTabView.setOnClickListener(this);
        iv_login_reg_back.setOnClickListener(this);

        login_out_animation = AnimationUtils.loadAnimation(getContext(),R.anim.mw_login_out);
        login_out_animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mAccountLoginV2.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        login_enter_animation = AnimationUtils.loadAnimation(getContext(),R.anim.mw_login_enter);
        login_enter_animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mAccountLoginV2.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        reg_enter_animation = AnimationUtils.loadAnimation(getContext(),R.anim.mw_reg_enter);
        reg_enter_animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mAccountRegisterLayoutV2.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        reg_out_animation = AnimationUtils.loadAnimation(getContext(),R.anim.mw_reg_out);
        reg_out_animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mAccountRegisterLayoutV2.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        ConfigBean configBean = SdkUtil.getSdkCfg(getContext());
        if (configBean != null){
            ConfigBean.VersionData versionData = configBean.getSdkConfigLoginData(getContext());
            if (versionData != null){

                if(versionData.isDeleteAccount()){

                }else{
//                    layout_delete_account.setVisibility(View.GONE);
                    LinearLayout.LayoutParams titleLayoutParams = (LinearLayout.LayoutParams) ll_reg_login_title.getLayoutParams();
                    titleLayoutParams.topMargin = titleLayoutParams.topMargin / 2 + titleLayoutParams.topMargin;
                    ll_reg_login_title.setLayoutParams(titleLayoutParams);
                }
            }
        }

        return contentView;
    }


    @Override
    public void setLoginDialogV2(SLoginDialogV2 sLoginDialog) {
        super.setLoginDialogV2(sLoginDialog);

        if (mAccountLoginV2 != null) {
            mAccountLoginV2.setLoginDialogV2(sLoginDialogv2);
        }
        if (mAccountRegisterLayoutV2 != null) {
            mAccountRegisterLayoutV2.setLoginDialogV2(sLoginDialogv2);
        }
    }

    @Override
    public void onClick(View v) {

        if (v == loginTabView){

            makeTabStatus(true);
            
        }else if (v == regTabView) {

            makeTabStatus(false);
        }else if (v == iv_login_reg_back) {

            List<AccountModel> accountModels = SdkUtil.getAccountModels(getActivity());
            if (accountModels == null || accountModels.isEmpty()){
                sLoginDialogv2.toMainHomeView();
            }else{

                if (this.fromView == ViewType.HomeView){
                    sLoginDialogv2.toMainHomeView();
                }else if (this.fromView == ViewType.WelcomeView){
                    sLoginDialogv2.toWelcomeBackView();
                }

            }
            sLoginDialogv2.distoryView(this);
        }
    }

    @Override
    public void onViewVisible() {
        super.onViewVisible();

        mAccountLoginV2.onViewVisible();
        mAccountRegisterLayoutV2.onViewVisible();

        if (this.fromView == ViewType.WelcomeView || this.fromView == null){
            iv_login_reg_back.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onViewGone() {
        super.onViewGone();
        mAccountLoginV2.onViewGone();
        mAccountRegisterLayoutV2.onViewGone();
    }

    @Override
    public void onViewRemove() {
        super.onViewRemove();
        mAccountLoginV2.onViewRemove();
        mAccountRegisterLayoutV2.onViewRemove();
    }

    private void makeTabStatus(boolean isLoginClick) {

        if (isLoginClick){

            if (mAccountLoginV2.getVisibility() == VISIBLE){
                return;
            }
            mAccountLoginV2.setVisibility(VISIBLE);
            mAccountRegisterLayoutV2.setVisibility(VISIBLE);

//            loginTabView.setBackgroundResource(R.drawable.login_tab_red_left_cons_bg);
//            regTabView.setBackgroundResource(R.drawable.login_tab_white_right_cons_bg);
            if (SdkUtil.isVersion2(getContext())){

                loginTabView.setTextColor(getContext().getResources().getColor(R.color.c_FF892E));
                regTabView.setTextColor(getContext().getResources().getColor(R.color.white_c));

            }else{
                loginTabView.setTextColor(Color.WHITE);
                regTabView.setTextColor(getContext().getResources().getColor(R.color.c_848484));
            }

            login_bottom_line.setVisibility(View.VISIBLE);
            register_bottom_line.setVisibility(View.INVISIBLE);

            mAccountRegisterLayoutV2.startAnimation(reg_out_animation);
            mAccountLoginV2.startAnimation(login_enter_animation);

        }else{

            if (mAccountRegisterLayoutV2.getVisibility() == VISIBLE){
                return;
            }

            mAccountLoginV2.setVisibility(VISIBLE);
            mAccountRegisterLayoutV2.setVisibility(VISIBLE);

//            loginTabView.setBackgroundResource(R.drawable.login_tab_white_left_cons_bg);
//            regTabView.setBackgroundResource(R.drawable.login_tab_red_right_cons_bg);


            if (SdkUtil.isVersion2(getContext())){

                loginTabView.setTextColor(getContext().getResources().getColor(R.color.white_c));
                regTabView.setTextColor(getContext().getResources().getColor(R.color.c_FF892E));

            }else{
                loginTabView.setTextColor(getContext().getResources().getColor(R.color.c_848484));
                regTabView.setTextColor(Color.WHITE);
            }

            login_bottom_line.setVisibility(View.INVISIBLE);
            register_bottom_line.setVisibility(View.VISIBLE);

            mAccountLoginV2.startAnimation(login_out_animation);
            mAccountRegisterLayoutV2.startAnimation(reg_enter_animation);

        }

    }


}

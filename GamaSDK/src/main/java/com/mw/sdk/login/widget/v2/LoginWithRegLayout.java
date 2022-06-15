package com.mw.sdk.login.widget.v2;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.core.base.utils.ToastUtils;
import com.mw.base.utils.SdkUtil;
import com.mw.sdk.login.SLoginDialogV2;
import com.mw.sdk.login.constant.ViewType;
import com.mw.sdk.login.model.AccountModel;
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

        mAccountLoginV2 = contentView.findViewById(R.id.pyAccountLoginV2Id);
        mAccountRegisterLayoutV2 = contentView.findViewById(R.id.accountRegisterLayoutV2Id);

        loginTabView.setOnClickListener(this);
        regTabView.setOnClickListener(this);
        iv_login_reg_back.setOnClickListener(this);

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
    public void refreshViewData() {
        super.refreshViewData();

        mAccountLoginV2.refreshViewData();
        mAccountRegisterLayoutV2.refreshViewData();
    }

    private void makeTabStatus(boolean isLoginClick) {

        if (isLoginClick){
            mAccountLoginV2.setVisibility(VISIBLE);
            mAccountRegisterLayoutV2.setVisibility(GONE);

//            loginTabView.setBackgroundResource(R.drawable.login_tab_red_left_cons_bg);
//            regTabView.setBackgroundResource(R.drawable.login_tab_white_right_cons_bg);

            loginTabView.setTextColor(Color.WHITE);
            regTabView.setTextColor(getContext().getResources().getColor(R.color.c_848484));

            login_bottom_line.setVisibility(View.VISIBLE);
            register_bottom_line.setVisibility(View.INVISIBLE);

        }else{
            mAccountLoginV2.setVisibility(GONE);
            mAccountRegisterLayoutV2.setVisibility(VISIBLE);

//            loginTabView.setBackgroundResource(R.drawable.login_tab_white_left_cons_bg);
//            regTabView.setBackgroundResource(R.drawable.login_tab_red_right_cons_bg);

            loginTabView.setTextColor(getContext().getResources().getColor(R.color.c_848484));
            regTabView.setTextColor(Color.WHITE);
            login_bottom_line.setVisibility(View.INVISIBLE);
            register_bottom_line.setVisibility(View.VISIBLE);
        }

    }


}

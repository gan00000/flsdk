package com.flyfun.sdk.login.widget.v2;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.flyfun.sdk.login.SLoginDialogV2;
import com.flyfun.sdk.login.widget.SLoginBaseRelativeLayout;
import com.gama.sdk.R;

/**
 * Created by GanYuanrong on 2017/2/6.
 */

public class MainLoginLayoutV3 extends SLoginBaseRelativeLayout implements View.OnClickListener {

    private View contentView;
    private TextView loginTabView, regTabView;

    private PyAccountLoginV2 mAccountLoginV2;
    private AccountRegisterLayoutV2 mAccountRegisterLayoutV2;

    public PyAccountLoginV2 getmAccountLoginV2() {
        return mAccountLoginV2;
    }

    public AccountRegisterLayoutV2 getmAccountRegisterLayoutV2() {
        return mAccountRegisterLayoutV2;
    }

    public MainLoginLayoutV3(Context context) {
        super(context);
    }

    public MainLoginLayoutV3(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainLoginLayoutV3(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }

    public View onCreateView(LayoutInflater inflater) {

        contentView = inflater.inflate(R.layout.v3_main_login_page_xm, null);
        loginTabView = contentView.findViewById(R.id.loginTabView);
        regTabView = contentView.findViewById(R.id.regTabView);

        mAccountLoginV2 = contentView.findViewById(R.id.pyAccountLoginV2Id);
        mAccountRegisterLayoutV2 = contentView.findViewById(R.id.accountRegisterLayoutV2Id);

        loginTabView.setOnClickListener(this);
        regTabView.setOnClickListener(this);

        return contentView;
    }


    @Override
    public void setLoginDialogV2(SLoginDialogV2 sLoginDialog) {
        super.setLoginDialogV2(sLoginDialog);

        mAccountLoginV2.setLoginDialogV2(sLoginDialogv2);
        mAccountRegisterLayoutV2.setLoginDialogV2(sLoginDialogv2);
    }

    @Override
    public void onClick(View v) {

        if (v == loginTabView){

            makeTabStatus(true);
            
        }else if (v == regTabView) {

            makeTabStatus(false);
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

            loginTabView.setBackgroundResource(R.drawable.login_tab_red_left_cons_bg);
            regTabView.setBackgroundResource(R.drawable.login_tab_white_right_cons_bg);

            loginTabView.setTextColor(Color.WHITE);
            regTabView.setTextColor(getContext().getResources().getColor(R.color.e_ff3a3b));

        }else{
            mAccountLoginV2.setVisibility(GONE);
            mAccountRegisterLayoutV2.setVisibility(VISIBLE);

            loginTabView.setBackgroundResource(R.drawable.login_tab_white_left_cons_bg);
            regTabView.setBackgroundResource(R.drawable.login_tab_red_right_cons_bg);

            loginTabView.setTextColor(getContext().getResources().getColor(R.color.e_ff3a3b));
            regTabView.setTextColor(Color.WHITE);

        }

    }


}

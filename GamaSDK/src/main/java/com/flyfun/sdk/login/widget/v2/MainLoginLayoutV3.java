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

        loginTabView.setOnClickListener(this);
        regTabView.setOnClickListener(this);

        return contentView;
    }


    @Override
    public void setLoginDialogV2(SLoginDialogV2 sLoginDialog) {
        super.setLoginDialogV2(sLoginDialog);

    }

    @Override
    public void onClick(View v) {

        if (v == loginTabView){

//            makeTabStatus(true);

            
        }else if (v == regTabView) {

//            makeTabStatus(false);
        }
    }

    @Override
    public void refreshViewData() {
        super.refreshViewData();

    }

    private void makeTabStatus(boolean isLoginClick) {

        if (isLoginClick){

            loginTabView.setBackgroundResource(R.drawable.login_tab_red_left_cons_bg);
            regTabView.setBackgroundResource(R.drawable.login_tab_white_right_cons_bg);

            loginTabView.setTextColor(Color.WHITE);
            regTabView.setTextColor(getContext().getResources().getColor(R.color.e_ff3a3b));

        }else{

            loginTabView.setBackgroundResource(R.drawable.login_tab_white_left_cons_bg);
            regTabView.setBackgroundResource(R.drawable.login_tab_red_right_cons_bg);

            loginTabView.setTextColor(getContext().getResources().getColor(R.color.e_ff3a3b));
            regTabView.setTextColor(Color.WHITE);

        }

    }


}

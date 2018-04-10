package com.starpy.sdk.login.widget.v2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.starpy.sdk.R;
import com.starpy.sdk.login.widget.SLoginBaseRelativeLayout;

/**
 * Created by GanYuanrong on 2017/2/6.
 */

public class MainLoginLayoutV2 extends SLoginBaseRelativeLayout implements View.OnClickListener {

    private View contentView;

    private View fbLoginView, starLoginView, macLoginView;
    private View starpyRegView;

    public MainLoginLayoutV2(Context context) {
        super(context);
    }

    public MainLoginLayoutV2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainLoginLayoutV2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }

    public View onCreateView(LayoutInflater inflater) {

        contentView = inflater.inflate(R.layout.v2_main_login_page, null);

        fbLoginView = contentView.findViewById(R.id.v2_login_fb_iv);
        starLoginView = contentView.findViewById(R.id.v2_login_py_iv);
        macLoginView = contentView.findViewById(R.id.v2_login_mac_iv);

        starpyRegView = contentView.findViewById(R.id.v2_login_reg_iv);

        fbLoginView.setOnClickListener(this);
        starLoginView.setOnClickListener(this);
        macLoginView.setOnClickListener(this);
        starpyRegView.setOnClickListener(this);

        return contentView;
    }



    @Override
    public void onClick(View v) {

        if (v == fbLoginView){
            sLoginDialogv2.getLoginPresenter().fbLogin(sLoginDialogv2.getActivity());
        }else if (v == starLoginView) {

            sLoginDialogv2.toAccountLoginView();
        }else if(v == macLoginView){
            sLoginDialogv2.getLoginPresenter().macLogin(sLoginDialogv2.getActivity());
        }else if (v == starpyRegView){
            sLoginDialogv2.toRegisterView(1);
        }
    }


}

package com.gama.sdk.login.widget.v2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gama.base.bean.SGameLanguage;
import com.gama.base.utils.Localization;
import com.gama.sdk.R;
import com.gama.sdk.login.widget.SLoginBaseRelativeLayout;

/**
 * Created by GanYuanrong on 2017/2/6.
 */

public class XMMainLoginLayoutJP extends SLoginBaseRelativeLayout implements View.OnClickListener {

    private View contentView;

    private ImageView fbLoginView, starLoginView, googleLoginView, twitterLoginView;
    private TextView macLoginView;
//    private View starpyRegView;

    public XMMainLoginLayoutJP(Context context) {
        super(context);
    }

    public XMMainLoginLayoutJP(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XMMainLoginLayoutJP(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }

    public View onCreateView(LayoutInflater inflater) {

        contentView = inflater.inflate(R.layout.v2_main_login_page_jp, null);

        twitterLoginView = contentView.findViewById(R.id.btn_xm_twitter_login_iv);
        fbLoginView = contentView.findViewById(R.id.btn_xm_facebook_login_iv);
        googleLoginView = contentView.findViewById(R.id.btn_xm_google_login_iv);
        starLoginView = contentView.findViewById(R.id.btn_xm_member_login_iv);
        macLoginView = contentView.findViewById(R.id.btn_xm_guest_login_iv);

        twitterLoginView.setOnClickListener(this);
        fbLoginView.setOnClickListener(this);
        googleLoginView.setOnClickListener(this);
        starLoginView.setOnClickListener(this);
        macLoginView.setOnClickListener(this);


        return contentView;
    }


    @Override
    public void onClick(View v) {

        if (v == fbLoginView) {
            sLoginDialogv2.getLoginPresenter().fbLogin(sLoginDialogv2.getActivity());
        } else if (v == starLoginView) {

            sLoginDialogv2.toAccountLoginView();
        } else if (v == macLoginView) {
            sLoginDialogv2.getLoginPresenter().macLogin(sLoginDialogv2.getActivity());
        } else if (v == googleLoginView) {
            //google+登录
            sLoginDialogv2.getLoginPresenter().googleLogin(sLoginDialogv2.getActivity());

        } else if (v == twitterLoginView) {
            sLoginDialogv2.getLoginPresenter().twitterLogin(sLoginDialogv2.getActivity());
        }
    }


}

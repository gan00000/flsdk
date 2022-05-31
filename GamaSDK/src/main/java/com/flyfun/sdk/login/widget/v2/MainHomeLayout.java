package com.flyfun.sdk.login.widget.v2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyfun.sdk.login.widget.SLoginBaseRelativeLayout;
import com.gama.sdk.R;

/**
 * Created by GanYuanrong on 2017/2/6.
 */

public class MainHomeLayout extends SLoginBaseRelativeLayout implements View.OnClickListener {

    private View contentView;

    private View fbLoginView, layout_go_account_login, guestLoginView, googleLoginView;
    private ImageView iv_login_google,iv_login_fb,iv_login_line;
    private CheckBox cb_agree_term;
    private TextView tv_go_term;
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
        tv_go_term = contentView.findViewById(R.id.tv_go_term);
        iv_logo = contentView.findViewById(R.id.iv_logo);

//        SGameLanguage sGameLanguage = Localization.getSGameLanguage(getContext());
//        if(SGameLanguage.ko_KR == sGameLanguage) {
//            starLoginView.setImageResource(R.drawable.btn_xm_member_login_kr);
//            macLoginView.setImageResource(R.drawable.gama_guest_login_kr);
//        } else if(SGameLanguage.zh_TW == sGameLanguage) {
//            starLoginView.setImageResource(R.drawable.btn_xm_member_login);
//            macLoginView.setImageResource(R.drawable.gama_guest_login);
//        }

        guestLoginView.setOnClickListener(this);
        layout_go_account_login.setOnClickListener(this);

//        if (Localization.getSGameLanguage(getActivity()) == SGameLanguage.en_US){
//            ((ImageView)macLoginView).setImageResource(R.drawable.btn_xm_guest_login_en);
//            ((ImageView)starLoginView).setImageResource(R.drawable.btn_xm_member_login_en);
//        }

        cb_agree_term.setChecked(true);
        return contentView;
    }



    @Override
    public void onClick(View v) {

        if (v == fbLoginView){
            sLoginDialogv2.getLoginPresenter().fbLogin(sLoginDialogv2.getActivity());
        }else if (v == layout_go_account_login) {

            sLoginDialogv2.showLoginWithRegView();
        }else if(v == guestLoginView){
            sLoginDialogv2.getLoginPresenter().macLogin(sLoginDialogv2.getActivity());
        }else if (v == googleLoginView){
            //google+登录
            sLoginDialogv2.getLoginPresenter().googleLogin(sLoginDialogv2.getActivity());

        }
    }


}

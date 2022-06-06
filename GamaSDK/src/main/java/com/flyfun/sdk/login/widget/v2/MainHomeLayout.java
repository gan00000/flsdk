package com.flyfun.sdk.login.widget.v2;

import android.content.Context;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyfun.sdk.login.widget.SLoginBaseRelativeLayout;
import com.mw.sdk.R;

/**
 * Created by GanYuanrong on 2017/2/6.
 */

public class MainHomeLayout extends SLoginBaseRelativeLayout implements View.OnClickListener {

    private View contentView;

    private View layout_go_account_login, guestLoginView;
    private ImageView iv_login_google,iv_login_fb,iv_login_line;
    private CheckBox cb_agree_term;
    private View layout_go_term;
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

        return contentView;
    }



    @Override
    public void onClick(View v) {

        if (v == iv_login_fb){
            if (checkAgreeTerm()){
                sLoginDialogv2.getLoginPresenter().fbLogin(sLoginDialogv2.getActivity());
            }
        }else if (v == layout_go_account_login) {

            sLoginDialogv2.showLoginWithRegView();
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
            sLoginDialogv2.showTermView();
        }

    }

    private boolean checkAgreeTerm(){
        if (cb_agree_term.isChecked()){
            return true;
        }
        toast(R.string.gama_ui_term_not_read);
        return false;
    }
}

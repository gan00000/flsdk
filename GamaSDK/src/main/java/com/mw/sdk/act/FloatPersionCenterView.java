package com.mw.sdk.act;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mw.sdk.R;
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;

public class FloatPersionCenterView extends SLoginBaseRelativeLayout {

    private ImageView persionIconImageView;
    private TextView gameNameTextView;
    private TextView setverNameTextView;
    private TextView roleNameTextView;
    private TextView uidTextView;
    private TextView accountTextView;

    public FloatPersionCenterView(Context context) {
        super(context);
    }

    public FloatPersionCenterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatPersionCenterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FloatPersionCenterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        View persionCenterView = layoutInflater.inflate(R.layout.float_personal_center, null);
        persionIconImageView = persionCenterView.findViewById(R.id.id_iv_game_icon);
        gameNameTextView = persionCenterView.findViewById(R.id.id_tv_game_name);
        uidTextView = persionCenterView.findViewById(R.id.id_tv_uid);
        setverNameTextView = persionCenterView.findViewById(R.id.id_tv_server_name);
        roleNameTextView = persionCenterView.findViewById(R.id.id_tv_role_name);
        accountTextView = persionCenterView.findViewById(R.id.id_tv_account);
        return persionCenterView;
    }
}

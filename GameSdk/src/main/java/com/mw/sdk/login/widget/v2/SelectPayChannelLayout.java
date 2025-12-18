package com.mw.sdk.login.widget.v2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.core.base.callback.SFCallBack;
import com.core.base.utils.PL;
import com.mw.sdk.R;
import com.mw.sdk.constant.ChannelPlatform;
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;
import com.mw.sdk.utils.ResConfig;
import com.mw.sdk.utils.SdkUtil;

/**
 * Created by GanYuanrong on 2017/2/6.
 */

public class SelectPayChannelLayout extends SLoginBaseRelativeLayout {

    private View contentView;

    protected View ggPayView;
    private Button otherPayBtn;
    //private  View ruPayBtn;

    protected View ruPayView;
    protected View xmPayView;//小米

    private ImageView iv_select_channel_rebate;
    private ImageView iv_select_channel_other_2;

    private SFCallBack<ChannelPlatform> sfCallBack;

    public void setSfCallBack(SFCallBack<ChannelPlatform> sfCallBack) {
        this.sfCallBack = sfCallBack;
    }

    public SelectPayChannelLayout(Context context) {
        super(context);

    }


    public SelectPayChannelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectPayChannelLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        PL.i("view onLayout");
    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }

    public View onCreateView(LayoutInflater inflater) {

        contentView = inflater.inflate(R.layout.mw_select_pay_channel, null);

        backView = contentView.findViewById(R.id.iv_select_channel_close);
        ggPayView = contentView.findViewById(R.id.iv_select_channel_gg);
        otherPayBtn = contentView.findViewById(R.id.bt_select_channel_other);
        ruPayView = contentView.findViewById(R.id.rl_select_channel_ru);
        //ruPayBtn = contentView.findViewById(R.id.bt_select_channel_ru);
        xmPayView = contentView.findViewById(R.id.rl_select_channel_xm);
        iv_select_channel_rebate = contentView.findViewById(R.id.iv_select_channel_rebate);

        View select_channel_title_layout = contentView.findViewById(R.id.select_channel_title_layout);
        iv_select_channel_other_2 = contentView.findViewById(R.id.iv_select_channel_other_2);
        View select_channel_other1_layout = contentView.findViewById(R.id.select_channel_other1_layout);

        backView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sBaseDialog != null){
                    sBaseDialog.dismiss();
                }
            }
        });

        ggPayView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sfCallBack != null){
                    sfCallBack.success(ChannelPlatform.GOOGLE,"");
                }
            }
        });

        otherPayBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sfCallBack != null){
                    sfCallBack.fail(ChannelPlatform.MEOW,"");
                }
            }
        });
        iv_select_channel_other_2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sfCallBack != null){
                    sfCallBack.fail(ChannelPlatform.MEOW,"");
                }
            }
        });

        ruPayView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sfCallBack != null){
                    sfCallBack.success(ChannelPlatform.VK,"");
                }
            }
        });

        xmPayView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sfCallBack != null){
                    sfCallBack.success(ChannelPlatform.Xiaomi,"");
                }
            }
        });

        String channel_platform = ResConfig.getChannelPlatform(getContext());
        if (ChannelPlatform.GOOGLE.getChannel_platform().equals(channel_platform)){
            ggPayView.setVisibility(View.VISIBLE);

            String payChannel = getContext().getResources().getString(R.string.mw_dialog_pay_add_type);
            if(payChannel.contains(ChannelPlatform.VK.getChannel_platform())){
                ruPayView.setVisibility(View.VISIBLE);
            }else {
                ruPayView.setVisibility(View.GONE);
            }

            if(payChannel.contains(ChannelPlatform.Xiaomi.getChannel_platform())){
                xmPayView.setVisibility(View.VISIBLE);
            }else {
                xmPayView.setVisibility(View.GONE);
            }

        }else {
            ggPayView.setVisibility(View.GONE);

            if (ChannelPlatform.VK.getChannel_platform().equals(channel_platform)){
                ruPayView.setVisibility(View.VISIBLE);
            }else {
                ruPayView.setVisibility(View.GONE);
            }

            if(ChannelPlatform.Xiaomi.getChannel_platform().equals(channel_platform)){
                xmPayView.setVisibility(View.VISIBLE);
            }else {
                xmPayView.setVisibility(View.GONE);
            }
        }


        boolean isShowOther2 = "true".equals(getContext().getString(R.string.mw_select_channel_other2));//添加多一个第三方，直接用图片显示，这情况隐藏title和另一个第三方
        if (isShowOther2){
            select_channel_title_layout.setVisibility(View.GONE);
            select_channel_other1_layout.setVisibility(View.GONE);
            iv_select_channel_other_2.setVisibility(View.VISIBLE);
        }else {
            select_channel_title_layout.setVisibility(View.VISIBLE);
            select_channel_other1_layout.setVisibility(View.VISIBLE);
            iv_select_channel_other_2.setVisibility(View.GONE);
        }

        try {

            String gameCode = ResConfig.getGameCode(getContext().getApplicationContext());
            String loginTimestamp = SdkUtil.getSdkTimestamp(getContext());

            String rebate_url = String.format("%simage/sdk/%s/rebate_%s.png?t=%s", ResConfig.getCdnPreferredUrl(getContext()), gameCode, channel_platform, loginTimestamp);

            PL.d("rebate_url=" + rebate_url);
//            rebate_url = "https://cdn-download.tthplay.com/image/sdk/mxwvn/rebate_google.png";//test
            Glide.with(this)
                    .load(rebate_url)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .placeholder(R.drawable.bg_pay_gg)
                    .into(iv_select_channel_rebate);

            String other_channel_url = String.format("%simage/sdk/%s/other_channel.png?t=%s", ResConfig.getCdnPreferredUrl(getContext()), gameCode, loginTimestamp);
            if (isShowOther2){
                Glide.with(this)
                        .load(other_channel_url)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .placeholder(R.mipmap.select_channel_other_bg2)
                        .into(iv_select_channel_other_2);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return contentView;
    }
}

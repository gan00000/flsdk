package com.mw.sdk.login.widget.v2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.core.base.callback.SFCallBack;
import com.core.base.utils.PL;
import com.mw.sdk.R;
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;
import com.mw.sdk.utils.ResConfig;
import com.mw.sdk.utils.SdkUtil;

/**
 * Created by GanYuanrong on 2017/2/6.
 */

public class SelectPayChannelLayout extends SLoginBaseRelativeLayout {

    private View contentView;

    protected View ggPayView, otherPayView;

    private ImageView iv_select_channel_rebate;

    private SFCallBack sfCallBack;

    public void setSfCallBack(SFCallBack sfCallBack) {
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
        otherPayView = contentView.findViewById(R.id.iv_select_channel_other);
        iv_select_channel_rebate = contentView.findViewById(R.id.iv_select_channel_rebate);

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
                    sfCallBack.success("","google");
                }
            }
        });

        otherPayView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sfCallBack != null){
                    sfCallBack.fail("","other");
                }
            }
        });

        try {
            String gameCode = ResConfig.getGameCode(getContext().getApplicationContext());
            String loginTimestamp = SdkUtil.getSdkTimestamp(getContext());
            String channel_platform = getContext().getResources().getString(R.string.channel_platform);
            String rebate_url = String.format("%simage/sdk/%s/rebate_%s.png?t=%s", ResConfig.getCdnPreferredUrl(getContext()), gameCode, channel_platform, loginTimestamp);

            PL.d("rebate_url=" + rebate_url);
//            rebate_url = "https://cdn-download.tthplay.com/image/sdk/mxwvn/rebate_google.png";//test
            Glide.with(this)
                    .load(rebate_url)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .placeholder(R.drawable.bg_pay_gg)
                    .into(iv_select_channel_rebate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contentView;
    }
}

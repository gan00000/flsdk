package com.mw.sdk.login.widget.v2;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.core.base.callback.SFCallBack;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.mw.sdk.R;
import com.mw.sdk.login.model.response.SLoginResponse;
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;
import com.mw.sdk.utils.ResConfig;
import com.mw.sdk.utils.SdkUtil;

/**
 * Created by GanYuanrong on 2017/2/6.
 */

public class MWBlockUserlLayout extends SLoginBaseRelativeLayout {

    private View contentView;

    protected TextView titleTextView, msgTextView;

    private SFCallBack sfCallBack;

    private SLoginResponse sLoginResponse;
    private int blockTime = 0;

    public void setSfCallBack(SFCallBack sfCallBack) {
        this.sfCallBack = sfCallBack;
    }

    public MWBlockUserlLayout(Context context, SLoginResponse sLoginResponse) {
        super(context, false,"1", "1", 1);
        this.sLoginResponse = sLoginResponse;
        this.blockTime = sLoginResponse.getData().getBlockTime();

        initViewManual(context);
    }

    public MWBlockUserlLayout(Context context) {
        super(context);
    }


    public MWBlockUserlLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MWBlockUserlLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        super.onLayout(changed, l, t, r, b);
//        PL.i("view onLayout");
//    }

    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }

    public View onCreateView(LayoutInflater inflater) {

        contentView = inflater.inflate(R.layout.mw_block_account_alert, null);

        backView = contentView.findViewById(R.id.sdk_bt_block_close);
        titleTextView = contentView.findViewById(R.id.sdk_tv_block_title);
        msgTextView = contentView.findViewById(R.id.sdk_tv_block_msg);

        backView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                blockTime = 0;
                if (sLoginResponse != null && sLoginResponse.getData().getBlockTime() == 0){
                    if (sfCallBack != null){
                        sfCallBack.fail(false, "fail");
                    }
                }else {
                    if (sfCallBack != null){
                        sfCallBack.success(true, "ok");
                    }
                }

            }
        });

        //SLoginResponse sLoginResponse = SdkUtil.getCurrentUserLoginResponse(getContext());
        if (sLoginResponse != null && sLoginResponse.getData() != null && sLoginResponse.getData().isBlock()){
            titleTextView.setText(secondToTime(this.blockTime));
            if (SStringUtil.isNotEmpty(sLoginResponse.getData().getBlockMsg())) {
                msgTextView.setText(sLoginResponse.getData().getBlockMsg());
            }else {
                if (this.blockTime == 0){
                    msgTextView.setText(R.string.text_blocked_msg);//已经被阻止进入游戏
                }else {
                    msgTextView.setText(R.string.text_block_msg);
                }
            }

            if (this.blockTime > 0) {
                Handler xHandler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {

                        if (blockTime < 1){
                            return;
                        }
                        blockTime = blockTime - 1;
                        String mTime = secondToTime(blockTime);
                        titleTextView.setText(mTime);
                        xHandler.postDelayed(this, 1000);
                    }
                };
                xHandler.postDelayed(runnable, 1000);
            }
        }

        return contentView;
    }

    private String secondToTime(int seconds){
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        int s = (seconds % 3600) % 60;

        String hstr = h + "";
        if (h < 10){
            hstr = "0" + hstr;
        }
        String mstr = m + "";
        if (m < 10){
            mstr = "0" + mstr;
        }
        String sstr = s + "";
        if (s < 10){
            sstr = "0" + sstr;
        }
        return hstr + ":" + mstr + ":" + sstr;
    }
}

package com.mw.sdk.login.widget.v2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.core.base.callback.SFCallBack;
import com.core.base.utils.AppUtil;
import com.core.base.utils.PL;
import com.mw.sdk.R;
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout;
import com.mw.sdk.utils.ResConfig;
import com.mw.sdk.utils.SdkUtil;

import java.util.regex.Pattern;

/**
 * Created by GanYuanrong on 2017/2/6.
 */

public class SdkLogLayout extends SLoginBaseRelativeLayout {

    private View contentView;

    protected View allLogView, eventLogView, copyLogView;
    private View cmsLogView;
    private TextView logTextView;

    private SFCallBack sfCallBack;

    public void setSfCallBack(SFCallBack sfCallBack) {
        this.sfCallBack = sfCallBack;
    }

    public SdkLogLayout(Context context) {
        super(context);

    }


    public SdkLogLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SdkLogLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected View createView(Context context, LayoutInflater layoutInflater) {
        return onCreateView(layoutInflater);
    }

    public View onCreateView(LayoutInflater inflater) {

        contentView = inflater.inflate(R.layout.sdk_log_content_layout, null);

        backView = contentView.findViewById(R.id.tv_log_close);
        allLogView = contentView.findViewById(R.id.tv_log_all);
        eventLogView = contentView.findViewById(R.id.tv_log_event);
        cmsLogView = contentView.findViewById(R.id.tv_log_cms_event);
        copyLogView = contentView.findViewById(R.id.tv_log_copy);
        logTextView = contentView.findViewById(R.id.tv_log_content);

        backView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sBaseDialog != null){
                    sBaseDialog.dismiss();
                }
            }
        });

        allLogView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (logTextView != null) {
                    logTextView.setText(PL.getLocalLog(getContext()));
                }
            }
        });

        eventLogView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (logTextView != null) {
                    String log = PL.getLocalLog(getContext());
                    String[] logSs = log.split("\n");
                    Pattern pattern = Pattern.compile("-----track event ");

                    StringBuilder eventLogSb = new StringBuilder();
                    // 遍历每一行并查找匹配的行
                    for (String aLog : logSs) {
                        if (pattern.matcher(aLog).find()) {
                            eventLogSb.append(aLog).append("\n");
                        }
                    }

                    logTextView.setText(eventLogSb.toString());
                }

            }
        });

        cmsLogView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (logTextView != null) {
                    String log = PL.getLocalLog(getContext());
                    String[] logSs = log.split("\n");
                    Pattern pattern = Pattern.compile("-----track event send to sever");

                    StringBuilder eventLogSb = new StringBuilder();
                    // 遍历每一行并查找匹配的行
                    for (String aLog : logSs) {
                        if (pattern.matcher(aLog).find()) {
                            eventLogSb.append(aLog).append("\n");
                        }
                    }

                    logTextView.setText(eventLogSb.toString());
                }

            }
        });

        copyLogView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (logTextView != null) {
                    AppUtil.copyText(getContext(), "wmLogKey", logTextView.getText().toString());
                }
            }
        });

        logTextView.setText(PL.getLocalLog(getContext()));
        return contentView;
    }
}

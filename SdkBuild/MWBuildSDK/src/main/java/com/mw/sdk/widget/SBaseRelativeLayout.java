package com.mw.sdk.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by gan on 2017/4/12.
 */

public class SBaseRelativeLayout extends RelativeLayout {


    public SBaseRelativeLayout(Context context) {
        super(context);
    }

    public SBaseRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SBaseRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SBaseRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public interface OperationCallback<T> {
        /**
         * 时间限制中
         */
        int TIME_LIMIT = 0;
        /**
         * 时间限制解除
         */
        int TIME_OUT = 1;

        /**
         * 游戏内手机绑定成功
         */
        int BIND_OK = 2;
        void statusCallback(int status);

        void dataCallback(T t);

        void alertTime(int remainTimeSeconds);
    }
}

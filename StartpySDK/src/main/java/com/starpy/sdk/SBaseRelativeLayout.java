package com.starpy.sdk;

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
}

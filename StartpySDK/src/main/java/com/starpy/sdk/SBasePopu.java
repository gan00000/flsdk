package com.starpy.sdk;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.PopupWindow;

/**
 * Created by gan on 2017/3/31.
 */

public class SBasePopu extends PopupWindow {

    public SBasePopu(Context context) {
        super(context);
    }

    public SBasePopu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SBasePopu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SBasePopu() {
    }

    public SBasePopu(View contentView) {
        super(contentView);
    }

    public SBasePopu(int width, int height) {
        super(width, height);
    }

    public SBasePopu(View contentView, int width, int height) {
        super(contentView, width, height);
    }

    public SBasePopu(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
    }


}

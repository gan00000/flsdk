package com.mw.sdk.utils;

import android.graphics.Rect;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.core.base.utils.PL;

public class SoftKeyBoardUtil {


    private int usableHeightPrevious;
//    private RelativeLayout.LayoutParams layoutParams;

    //键盘适配
    public void resizeView(View contentRootView, View resizeView) {

        int usableHeightNow = computeUsableHeight(contentRootView);
        PL.d("usableHeightNow = " + usableHeightNow);
        if (usableHeightNow <= 0){//未显示出来时会为负数
            return;
        }
        if (usableHeightPrevious <= 0) {
            usableHeightPrevious = usableHeightNow;
        }
        //根视图显示高度没有变化，可以看作软键盘显示／隐藏状态没有改变
        if (usableHeightPrevious == usableHeightNow) {
            return;
        }

        int inVisibleHeight = usableHeightPrevious - usableHeightNow;
        PL.d("inVisibleHeight=" + inVisibleHeight);

        int[] location = new int[2];
        resizeView.getLocationInWindow(location);
        int resizeView_x = location[0]; // view距离window 左边的距离（即x轴方向）
        int resizeView_y = location[1]; // view距离window 顶边的距离（即y轴方向）
        int resizeView_h = resizeView.getHeight();

        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) contentRootView.getLayoutParams();
        //根视图显示高度变小超过200，可以看作软键盘显示了
        if (inVisibleHeight > 200) {
            //键盘显示
            usableHeightPrevious = usableHeightNow;
            if (usableHeightNow - (resizeView_y + resizeView_h) > 0){//键盘未遮挡目标view(通常是输入框)
                return;
            }

            int occludeViewHeight = resizeView_y + resizeView_h - usableHeightNow;
            if (occludeViewHeight > 0){//被覆盖

//                layoutParams.topMargin = -occludeViewHeight;
                contentRootView.requestLayout();

                //rootViewVisibleHeight = inVisibleHeight;
            }
            return;
        }

        //根视图显示高度变大超过200，可以看作软键盘隐藏了
        if (-inVisibleHeight > 200) {
//            layoutParams.topMargin = 0;
            contentRootView.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }

//        if (usableHeightNow != usableHeightPrevious) {
//            int usableHeightSansKeyboard = mostTopRootView.getHeight();
//            PL.d("usableHeightSansKeyboard:" + usableHeightSansKeyboard);
//            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
//            if (heightDifference > (usableHeightSansKeyboard / 3)) {
//                // keyboard probably just became visible
//                viewGroupLayoutParams.height = usableHeightSansKeyboard - heightDifference;
//            } else {
//                // keyboard probably just became hidden
//                viewGroupLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
//            }
//            mostTopRootView.requestLayout();
//            contentFrameLayout.requestLayout();
//            usableHeightPrevious = usableHeightNow;
//        }
    }

    private int computeUsableHeight(View rootView) {
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        return r.height();
    }

}

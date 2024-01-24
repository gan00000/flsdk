//package com.mw.sdk.widget;
//
//import android.app.Activity;
//import android.content.Context;
//import android.graphics.Color;
//import android.text.TextUtils;
//import android.view.Gravity;
//import android.view.View;
//import android.view.ViewGroup.LayoutParams;
//import android.view.WindowManager;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.core.base.utils.PL;
//import com.core.base.utils.ScreenHelper;
//import com.efuntw.platform.R;
//import com.gama.floatview.window.bean.FloatItemBean;
//import com.gama.floatview.window.listener.FloatItemClickListener;
//
//import java.util.ArrayList;
//
///**
// * @author itxuxxey
// */
//public class EfunFuctionViewGroup {
//
//    private ArrayList<FloatItemBean> beans;
//
//    private Context context;
//
//    private int pixelsX;
//
//    public void setPixelsY(int pixelsY) {
//        this.pixelsY = pixelsY;
//    }
//
//    public void setPixelsX(int pixelsX) {
//        this.pixelsX = pixelsX;
//    }
//
//    private int pixelsY;// 屏幕的宽和高
//
//    private WindowManager.LayoutParams mWindowParams;
//
//    ScreenHelper screenHelper;
//
//    public EfunFuctionViewGroup(Context context,
//                                ArrayList<FloatItemBean> beans, int pixelsX, int pixelsY) {
//        this.context = context;
//        this.beans = beans;
//        this.pixelsX = pixelsX;
//        this.pixelsY = pixelsY;
//
//        screenHelper = new ScreenHelper((Activity) context);
//    }
//
//
//    public boolean isMustCreate() {
//        if (floatButtonLinearLayout == null || floatItemLinearLayout == null) {
//            return true;
//        }
//        return false;
//    }
//
//    LinearLayout floatButtonLinearLayout;
//    LinearLayout floatItemLinearLayout;
//
//    private int pointX;
//    private int pointY;
//
//    private FloatItemClickListener listener;
//
//    private int logoSize;
//
//
//    /**
//     * @return the floatButtonLinearLayout
//     */
//    public LinearLayout getFloatButtonLinearLayout() {
//        return floatButtonLinearLayout;
//    }
//
//    // 当为空时创建
//    /*public void createPop(int logoSize, FloatItemClickListener listener,
//                          WindowManager windowManager, int pointX, int pointY) {
//        PL.d("=======createPop========");
//        this.pointX = pointX;
//        this.pointY = pointY;
//        this.listener = listener;
//        this.logoSize = (int) (logoSize * 1.3);
//        initWidget();
//        addWidgetToWindow(windowManager, pointX, pointY);
//    }*/
//
//    private void initWidget() {
//        floatButtonLinearLayout = new LinearLayout(context);
//        floatItemLinearLayout = new LinearLayout(context);
//        floatItemLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
//        floatItemLinearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//        floatButtonLinearLayout.setBackgroundDrawable(null);
//        showView(floatItemLinearLayout, listener);
//
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, logoSize);
//        params.gravity = Gravity.CENTER_VERTICAL;
//        params.setMargins((int) (logoSize / 4), 0, 0, 0);
//        floatButtonLinearLayout.addView(floatItemLinearLayout, params);
//    }
//
//    public void reCreatePop(int pointX, int pointY, int statusBarHeight, boolean isfullScreen, boolean isHasNavigationBar) {
//        PL.d( "=====reCreatePop");
//        this.pointX = pointX;
//        this.pointY = pointY;
//
//        if (floatButtonLinearLayout != null) {
//            floatButtonLinearLayout.removeAllViewsInLayout();
//            floatItemLinearLayout = new LinearLayout(context);
//            floatItemLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
//            floatItemLinearLayout.setGravity(Gravity.CENTER_VERTICAL);
//            floatButtonLinearLayout.setBackgroundDrawable(null);
//            showView(floatItemLinearLayout, listener);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    LayoutParams.WRAP_CONTENT, logoSize);
//            params.gravity = Gravity.CENTER_VERTICAL;
//            if (pointX <= pixelsX / 2) {
//                params.setMargins((int) (logoSize / 4), 0, 0, 0);
//            } else {
//                if (isfullScreen) {
//                    params.setMargins(0, 0, (int) (logoSize / 4), 0);
//                } else {
//                    if (screenHelper.isPortrait()) {
//                        params.setMargins(0, 0, (int) (logoSize / 4), 0);//竖版
//                    } else {
//                        if (isHasNavigationBar) {
//                            params.setMargins(0, 0, (int) (logoSize / 4) + statusBarHeight, 0);//横版
//                        } else {
//                            params.setMargins(0, 0, (int) (logoSize / 4), 0);//横版
//                        }
//                    }
//                }
//            }
//            floatButtonLinearLayout.addView(floatItemLinearLayout, params);
//        }
//
//    }
//
//    private void addWidgetToWindow(WindowManager windowManager, int pointX, int pointY) {
//        mWindowParams = new WindowManager.LayoutParams();
//        mWindowParams.x = pointX;
//        mWindowParams.y = pointY;
//        mWindowParams.width = mWindowParams.WRAP_CONTENT;
//        mWindowParams.height = (int) (logoSize);
//        mWindowParams.format = 1;
//        mWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
////		mWindowParams.type = WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW + 2;
//        mWindowParams.type = WindowManager.LayoutParams.LAST_SUB_WINDOW;
//        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//        windowManager.addView(floatButtonLinearLayout, mWindowParams);
//        floatButtonLinearLayout.setVisibility(View.GONE);
//    }
//
//    private FloatImageView efunImage;
//
//    // 展示功能条
///*    public void showPop(FloatImageView efunIv, int pointX, int pointY,
//                        WindowManager windowManager) {
//        this.efunImage = efunIv;
//        // if (floatButtonLinearLayout.VISIBLE == View.GONE) {
//        floatButtonLinearLayout.setVisibility(View.VISIBLE);
//        // }
//        if (pointX <= pixelsX / 2) {
//            floatItemLinearLayout.setBackgroundResource(context.getResources().getIdentifier("efun_pd_floating_bg_right",
//                    "drawable", context.getPackageName()));
//        } else {
//            floatItemLinearLayout.setBackgroundResource(context.getResources().getIdentifier("efun_pd_floating_bg_right",
//                    "drawable", context.getPackageName()));
//        }
//        mWindowParams.x = pointX;
//        mWindowParams.y = pointY;
//        windowManager.updateViewLayout(floatButtonLinearLayout, mWindowParams);
//    }*/
//
//    // 是否需要重建
//    public boolean isCreatedAgain() {
//        return false;
//    }
//
//    private void showView(LinearLayout view,
//                          final FloatItemClickListener listener) {
//        // 添加内容控件
//        if (pointX <= pixelsX / 2) {
////            EfunLogUtil.logI(pointX + "left" + pixelsX);// 左边控件
//            final int itemCount = beans.size();
//
//            for (int i = 0; i < itemCount; i++) {
//                final FloatItemBean bean = beans.get(i);
//                if (bean == null) {
//                    continue;
//                }
//
//                LinearLayout itemLinearLayout = new LinearLayout(context);
//                itemLinearLayout.setOrientation(LinearLayout.VERTICAL);
//
//
//                ImageView iconImageView = new ImageView(context);
//
//                TextView itemTextView = new TextView(context);
//                itemTextView.setTextSize(8);
//                itemTextView.setTextColor(Color.BLACK);
//                itemTextView.setMaxLines(1);
//                itemTextView.setEllipsize(TextUtils.TruncateAt.END);
//                itemTextView.setText(bean.getItemName());
//                itemTextView.setGravity(Gravity.CENTER);
//
//                LinearLayout.LayoutParams iconImageViewParams = new LinearLayout.LayoutParams((int) (logoSize * 0.5), (int) (logoSize * 0.5));
//
////                LinearLayout.LayoutParams itemTextViewParams = new LinearLayout.LayoutParams(
////                        (int) (logoSize * 0.4), (int) (logoSize * 0.4));
//
//                if (bean.getItemType().equals("cs")) {
//                    iconImageView.setBackgroundResource(R.drawable.pf_tw_button_logo_cs);
//                } else if (bean.getItemType().equals("fhide")) {
//                    iconImageView.setBackgroundResource(R.drawable.pf_tw_button_logo_fhide);
//                } else if (bean.getItemType().equals("person")) {
//                    iconImageView.setBackgroundResource(R.drawable.pf_tw_button_logo_person);
//                } else if (bean.getItemType().equals("newsandactivity")) {
//
//                    iconImageView.setBackgroundResource(R.drawable.pf_tw_button_logo_newsandactivity);
//                } else if (bean.getItemType().equals("fb")) {
//                    iconImageView.setBackgroundResource(R.drawable.pf_tw_button_logo_fb);
//                } else if (bean.getItemType().equals("video")) {
//                    iconImageView.setBackgroundResource(R.drawable.pf_tw_button_logo_video);
//                }else {
//                    iconImageView.setBackgroundResource(R.drawable.pf_tw_button_logo_newsandactivity);
//                }
//
////                iconImageView.setBackgroundColor(Color.BLUE);
//
//                itemLinearLayout.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        efunImage.allowMove(true);
//                        if (floatButtonLinearLayout != null && isShowPop()) {
//                            popDismiss();
//                        }
//                        listener.itemClicked(bean);
//
//                    }
//                });
//
//                itemLinearLayout.addView(iconImageView, iconImageViewParams);
//                itemLinearLayout.addView(itemTextView);
//
//                LinearLayout.LayoutParams itemLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//
//                if (i == 0) {
//                    itemLayoutParams.setMargins(logoSize, 0, logoSize / 6, 0);
//                } else if (i == itemCount - 1) {
//                    itemLayoutParams.setMargins(0, 0, logoSize / 2, 0);
//                } else {
//                    itemLayoutParams.setMargins(0, 0, logoSize / 4, 0);
//                }
//
//                view.addView(itemLinearLayout, itemLayoutParams);
//            }
//
//        } else {
////            EfunLogUtil.logI(pointX + "right" + pixelsX);// 右边控件
//            final int itemCount = beans.size();
//            for (int i = itemCount - 1; i >= 0; i--) {
//
//                final FloatItemBean bean = beans.get(i);
//
//                if (bean == null) {
//                    continue;
//                }
//
//                LinearLayout itemLinearLayout = new LinearLayout(context);
//                itemLinearLayout.setOrientation(LinearLayout.VERTICAL);
//
//
//                ImageView iconImageView = new ImageView(context);
//
//                TextView itemTextView = new TextView(context);
//                itemTextView.setTextSize(8);
//                itemTextView.setTextColor(Color.BLACK);
//                itemTextView.setMaxLines(1);
//                itemTextView.setEllipsize(TextUtils.TruncateAt.END);
//                itemTextView.setText(bean.getItemName());
//                itemTextView.setGravity(Gravity.CENTER);
//
//                LinearLayout.LayoutParams iconImageViewParams = new LinearLayout.LayoutParams((int) (logoSize * 0.5), (int) (logoSize * 0.5));
//
////                LinearLayout.LayoutParams itemTextViewParams = new LinearLayout.LayoutParams(
////                        (int) (logoSize * 0.4), (int) (logoSize * 0.4));
//
//                if (bean.getItemType().equals("cs")) {
//                    iconImageView.setBackgroundResource(R.drawable.pf_tw_button_logo_cs);
//                } else if (bean.getItemType().equals("fhide")) {
//                    iconImageView.setBackgroundResource(R.drawable.pf_tw_button_logo_fhide);
//                } else if (bean.getItemType().equals("person")) {
//                    iconImageView.setBackgroundResource(R.drawable.pf_tw_button_logo_person);
//                } else if (bean.getItemType().equals("newsandactivity")) {
//
//                    iconImageView.setBackgroundResource(R.drawable.pf_tw_button_logo_newsandactivity);
//                } else if (bean.getItemType().equals("fb")) {
//                    iconImageView.setBackgroundResource(R.drawable.pf_tw_button_logo_fb);
//                } else if (bean.getItemType().equals("video")) {
//                    iconImageView.setBackgroundResource(R.drawable.pf_tw_button_logo_video);
//                }else {
//                    iconImageView.setBackgroundResource(R.drawable.pf_tw_button_logo_newsandactivity);
//                }
//
//
////                iconImageView.setBackgroundColor(Color.BLUE);
//
//                itemLinearLayout.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        efunImage.allowMove(true);
//                        if (floatButtonLinearLayout != null && isShowPop()) {
//                            popDismiss();
//                        }
//                        listener.itemClicked(bean);
//
//                    }
//                });
//
//                itemLinearLayout.addView(iconImageView, iconImageViewParams);
//                itemLinearLayout.addView(itemTextView);
//
//                LinearLayout.LayoutParams itemLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//
////                if (i == 0) {
////                    itemLayoutParams.setMargins(logoSize, logoSize / 10, logoSize / 6, logoSize / 10);
////                } else if (i == itemCount - 1) {
////                    itemLayoutParams.setMargins(0, logoSize / 10, logoSize / 2, logoSize / 10);
////                } else {
////                    itemLayoutParams.setMargins(0, logoSize / 10, logoSize / 6, logoSize / 10);
////                }
//
//                if (i == 0) {
//
//                    itemLayoutParams.setMargins(logoSize / 6, 0, logoSize, 0);
//
//                } else if (i == itemCount - 1) {
//
//                    itemLayoutParams.setMargins(logoSize / 2, 0, 0, 0);
//                } else {
//                    itemLayoutParams.setMargins(logoSize / 4, 0, 0, 0);
//                }
//
//                view.addView(itemLinearLayout, itemLayoutParams);
//
//            }
//        }
//    }
//
//    public void popDismiss() {
//        if (floatButtonLinearLayout != null){
//
//            floatButtonLinearLayout.setVisibility(View.GONE);
//        }
//
//    }
//
//    // 是否扩展条在显示
//    public boolean isShowPop() {
//        if (floatButtonLinearLayout != null) {
//            if (floatButtonLinearLayout.getVisibility() == View.VISIBLE) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//}

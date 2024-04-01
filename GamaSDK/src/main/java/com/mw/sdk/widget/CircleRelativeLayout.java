package com.mw.sdk.widget;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class CircleRelativeLayout extends RelativeLayout {


    private int mWidth;
    private int mHeight;
    private int mChildCount;
    private int mmRadius;


    public CircleRelativeLayout(Context context) {
        super(context);
    }

    public CircleRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CircleRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        mmRadius = mWidth / 2;

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        initDraw();
    }


    public void initDraw() {
        mChildCount = getChildCount();
        for (int i = 0; i < mChildCount; i++) {

            View child = getChildAt(i);

            int childWidthHalf = child.getWidth() / 2;
            int childHeightHalf = child.getHeight() / 2;

            int mChildRadius = childWidthHalf;

//            int mAngle = 360 / mChildCount * i;
            int mX = (int) (mWidth / 2 + Math.cos(2 * Math.PI / mChildCount * i + Math.PI / 2) * (mmRadius - mChildRadius - mChildRadius/4));
            int mY = (int) (mHeight / 2 - Math.sin(2 * Math.PI / mChildCount * i + Math.PI / 2) * (mmRadius - mChildRadius - mChildRadius/4));

//             * @param l Left position, relative to parent
//     * @param t Top position, relative to parent
//     * @param r Right position, relative to parent
//     * @param b Bottom position, relative to parent

            /*
             *@param l view 左边缘相对于父布局左边缘距离
             *@param t view 上边缘相对于父布局上边缘位置
             *@param r view 右边缘相对于父布局左边缘距离
             *@param b view 下边缘相对于父布局上边缘距离
             */

            child.layout(mX - childWidthHalf, mY - childHeightHalf, mX + childWidthHalf, mY + childHeightHalf);
        }


    }

    /**
     * 初始化环绕数量半径
     */
//    public void init(int count, int radius) {
//        mRadius = radius;
//        for (int i = 0; i < count + 1; i++) {
//            CircleImageView imageView = new CircleImageView(getContext());
//            if (i == 0) {
//                //i为0时为圆型头像
//                View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_header, null, true);
//                mCircleImageView = (CircleImageView) view.findViewById(R.id.iv_header);
//                addView(view);
//            } else {
//                addView(imageView, MeasureUtil.dip2px(15), MeasureUtil.dip2px(15));
//                mCircleImageViewList.add(imageView);
//            }
//        }
//    }
}

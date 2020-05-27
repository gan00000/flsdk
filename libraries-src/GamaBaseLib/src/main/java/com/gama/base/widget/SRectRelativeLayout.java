package com.gama.base.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class SRectRelativeLayout extends RelativeLayout {
    private Path path = new Path();
    private float[] radiusArray = {20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f};

    float width, height = 0;

    private int radius = 0;

    public SRectRelativeLayout(Context context) {
        super(context);
    }

    public SRectRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SRectRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }

    public void setRadius(int radius) {
        this.radius = radius;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        float y = 0;//因为webview是可滚动的，所以它的高度是变化的，每个height地方都需要加上滚动值。不加的话控件的高度不能变更，此圆角构成方法适用于其他的控件，如Imageview，此时无需加y.

        int r = 30;

        if (width > r && height > r) {

            Path path = new Path();

            path.moveTo(r, 0);

            path.lineTo(width - r, 0);

            path.quadTo(width, 0, width, r);

            path.lineTo(width, y + height - r);//1,r改为0

            path.quadTo(width, y + height, width - r, y + height); //2,r改为0

            path.lineTo(r, y + height);//3,r改为0

            path.quadTo(0, height, 0, y + height - r); //4,r改为0  这四处r改为0即可实现上左上右为圆角，否则四角皆为圆角

            path.lineTo(0, r);

            path.quadTo(0, 0, r, 0);

            if (r > 0) {

                canvas.clipPath(path);//将路径闭合构成控件的区域

            }

        }

//        int x = this.getScrollX();
//        int y = this.getScrollY();

//        path.addRoundRect(new RectF(0, 0, width, height), radiusArray, Path.Direction.CW);        // 使用半角的方式，性能比较好
//        canvas.clipPath(path);

        super.onDraw(canvas);

    }

    /**
     * 设置四个角的圆角半径
     */
    public void setRadius(float leftTop, float rightTop, float rightBottom, float leftBottom) {
        radiusArray[0] = leftTop;
        radiusArray[1] = leftTop;
        radiusArray[2] = rightTop;
        radiusArray[3] = rightTop;
        radiusArray[4] = rightBottom;
        radiusArray[5] = rightBottom;
        radiusArray[6] = leftBottom;
        radiusArray[7] = leftBottom;
    }

}

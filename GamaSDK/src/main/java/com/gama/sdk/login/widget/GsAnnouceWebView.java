package com.gama.sdk.login.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.core.base.utils.PL;
import com.gama.base.widget.SWebView;


public class GsAnnouceWebView extends SWebView {

    private Path path = new Path();
    private float[] radiusArray = {20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f};

    float width, height = 0;

    private int radius = 0;

    public GsAnnouceWebView(Context context) {
        super(context);
    }

    public GsAnnouceWebView(Context context, AttributeSet attrs) {
        super(context, attrs);//此处必需引用父控件的构造方法，以使用父控件的主题，否则会造成键盘的一些问题（具体的忘记了，大家可以试试）

    }

    public GsAnnouceWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);//此处必需引用父控件的构造方法，以使用父控件的主题，否则会造成键盘的一些问题（具体的忘记了，大家可以试试）
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

        float y = this.getScrollY();//因为webview是可滚动的，所以它的高度是变化的，每个height地方都需要加上滚动值。不加的话控件的高度不能变更，此圆角构成方法适用于其他的控件，如Imageview，此时无需加y.
        int r = 30;

        if (width > r && height > r) {

            Path path = new Path();

            //从右上角开始，因为右上角有关闭按钮看不到，这个画图有问题，最后包角的时候会显示直线

            path.moveTo(width, y + r);

            path.lineTo(width, y + height - r);

            path.quadTo(width, y + height, width -  r, y + height);

            path.lineTo(r, y + height);//1,r改为0

            path.quadTo(0, y + height, 0, y + height - r); //2,r改为0

            path.lineTo(0, y + r);//3,r改为0

            path.quadTo(0, y, r, y); //4,r改为0  这四处r改为0即可实现上左上右为圆角，否则四角皆为圆角

            path.lineTo(width - r, y);

            path.quadTo(width, y, width, y + r);

//            path.moveTo(r, y);
//
//            path.lineTo(width - r, y);
//
//            path.quadTo(width, y, width, r);
//
//            path.lineTo(width, y + height - r);//1,r改为0
//
//            path.quadTo(width, y + height, width - r, y + height); //2,r改为0
//
//            path.lineTo(r, y + height);//3,r改为0
//
//            path.quadTo(0, y + height, 0, y + height - r); //4,r改为0  这四处r改为0即可实现上左上右为圆角，否则四角皆为圆角
//
//            path.lineTo(0, r + y);
//
//            path.quadTo(0, r + y, r, y);

            if (r > 0) {
                canvas.clipPath(path);//将路径闭合构成控件的区域
            }

        }

//        int x = this.getScrollX();
//        int y = this.getScrollY();
//
//        path.addRoundRect(new RectF(0, y, x + width, y + height), radiusArray, Path.Direction.CW);        // 使用半角的方式，性能比较好
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


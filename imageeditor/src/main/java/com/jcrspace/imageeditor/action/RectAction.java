package com.jcrspace.imageeditor.action;
/**
 * Created by jiangchaoren on 2017/10/12.
 */

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.ColorInt;

/**
 * 矩形操作
 *
 * @author ShaoWeng
 */
public class RectAction implements BaseAction {
    private RectF rect;
    private int color;
    private int strokeWidth; //粗细
    private Paint paint;

    public RectAction(RectF rect, @ColorInt int color, int strokeWidth) {
        this.rect = rect;
        this.color = color;
        this.strokeWidth = strokeWidth;
        paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        paint.setColor(color);
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        paint.setStrokeWidth(strokeWidth);
    }

    public Paint getPaint() {
        return paint;
    }

    public RectF getRect() {
        return rect;
    }

    public void setRect(RectF rect) {
        this.rect = rect;
    }
}

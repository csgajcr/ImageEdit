package com.jcrspace.imageeditor.action;
/**
 * Created by jiangchaoren on 2017/10/11.
 */

import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;

/**
 * 划线操作
 *
 * @author ShaoWeng
 */
public class LineAction implements BaseAction {
    private int color;
    private float startX;
    private float endX;
    private float startY;
    private float endY;
    private int strokeWidth; //线的宽度
    private Paint paint;
    private boolean isSelect = false;
    private Rect[] anchorPoints;

    public LineAction(@ColorInt int color, int startX, int startY, int endX, int endY, int strokeWidth) {
        this.color = color;
        this.startX = startX;
        this.endX = endX;
        this.startY = startY;
        this.endY = endY;
        this.strokeWidth = strokeWidth;
        paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth);
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

    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getEndX() {
        return endX;
    }

    public void setEndX(float endX) {
        this.endX = endX;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public float getEndY() {
        return endY;
    }

    public void setEndY(float endY) {
        this.endY = endY;
    }

    @Override
    public boolean isSelect() {
        return isSelect;
    }

    @Override
    public void setSelect(boolean isSelect){
        this.isSelect = isSelect;
    }

    public Rect[] getAnchorPoints() {
        return anchorPoints;
    }

    public void setAnchorPoints(Rect[] anchorPoints) {
        this.anchorPoints = anchorPoints;
    }
}



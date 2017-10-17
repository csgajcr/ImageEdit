package com.jcrspace.imageeditor.action;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.text.TextPaint;

/**
 * Created by jiangchaoren on 2017/10/13.
 */

public class TextAction implements BaseAction {
    private String text;
    private float startX;
    private float startY;
    private TextPaint paint;
    private int color;
    private int fontSize;
    private Bitmap textBitmap;
    private boolean isSelect = false;

    public TextAction(String text, @ColorInt int color, float startX, float startY, int fontSize) {
        this.text = text;
        this.startX = startX;
        this.startY = startY;
        this.fontSize = fontSize;
        this.color = color;
        paint = new TextPaint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setTextSize(fontSize);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public TextPaint getPaint() {
        return paint;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        paint.setColor(color);
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
        paint.setTextSize(fontSize);
    }

    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    @Override
    public boolean isSelect() {
        return isSelect;
    }

    @Override
    public void setSelect(boolean isSelect){
        this.isSelect = isSelect;
    }

    public Bitmap getTextBitmap() {
        return textBitmap;
    }

    public void setTextBitmap(Bitmap textBitmap) {
        this.textBitmap = textBitmap;
    }
}

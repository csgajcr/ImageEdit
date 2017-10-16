package com.jcrspace.imageeditor.anchor;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.jcrspace.imageeditor.action.RectAction;
import com.jcrspace.imageeditor.utils.ScreenSizeUtil;

/**
 * Created by jiangchaoren on 2017/10/16.
 */

public class RectAnchorPoint implements AnchorPoint {

    private float cx;
    private float cy;
    private float radius;
    private Paint paint;


    public RectAnchorPoint(RectAction action) {
        RectF rectF = action.getRect();
        cx = rectF.right;
        cy = rectF.bottom;
        radius = ANCHOR_POINT_RADIUS;
        paint = new Paint();
    }


    @Override
    public void draw(Canvas canvas) {

    }
}

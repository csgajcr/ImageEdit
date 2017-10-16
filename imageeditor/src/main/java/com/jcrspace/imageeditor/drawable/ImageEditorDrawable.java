package com.jcrspace.imageeditor.drawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.text.Layout;
import android.text.StaticLayout;

import com.jcrspace.imageeditor.R;
import com.jcrspace.imageeditor.action.BaseAction;
import com.jcrspace.imageeditor.action.LineAction;
import com.jcrspace.imageeditor.action.RectAction;
import com.jcrspace.imageeditor.action.TextAction;
import com.jcrspace.imageeditor.anchor.RectAnchorPoint;
import com.jcrspace.imageeditor.utils.GraphUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangchaoren on 2017/10/12.
 */

public class ImageEditorDrawable extends BitmapDrawable {

    private List<BaseAction> actionList = new ArrayList<>();
    private Context context;

    public ImageEditorDrawable(BitmapDrawable drawable, Context context) {
        super(drawable.getBitmap());
        this.context = context;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (actionList.size() > 0) {
            for (BaseAction action : actionList) {
                if (action instanceof LineAction) {
                    drawLine(canvas, (LineAction) action);
                } else if (action instanceof RectAction) {
                    drawRect(canvas, (RectAction) action);
                } else if (action instanceof TextAction) {
                    drawText(canvas, (TextAction) action);
                }
            }
        }
    }

    private void drawLine(Canvas canvas, LineAction action) {
        canvas.drawLine(action.getStartX(), action.getStartY(), action.getEndX(), action.getEndY(), action.getPaint());
        if (action.isSelect()) {
            drawLineAnchorPoint(action, canvas);
        }
    }

    private void drawRect(Canvas canvas, RectAction action) {
        canvas.drawRect(action.getRect(), action.getPaint());
        if (action.isSelect()) {
            drawRectAnchorPoint(action, canvas);
        }
    }

    private void drawText(Canvas canvas, TextAction action) {
//        canvas.drawText(action.getText(),action.getStartX(),action.getStartY(),action.getPaint());
        canvas.save();
        canvas.translate(action.getStartX(), action.getStartY());
        StaticLayout myStaticLayout = new StaticLayout(action.getText(), action.getPaint(), canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        myStaticLayout.draw(canvas);
        canvas.restore();
    }


    public int addLine(LineAction lineAction) {
        actionList.add(lineAction);
        return actionList.size() - 1;
    }

    public int addRect(RectAction rectAction) {
        actionList.add(rectAction);
        return actionList.size() - 1;
    }

    public int addText(TextAction textAction) {
        actionList.add(textAction);
        return actionList.size() - 1;
    }

    public void updateLine(int index, LineAction lineAction) {
        actionList.set(index, lineAction);
    }

    public void updateRect(int index, RectAction rectAction) {
        actionList.set(index, rectAction);
    }

    public void updateText(int index, TextAction rectAction) {
        actionList.set(index, rectAction);
    }

    /**
     * 通过x，y坐标来选中元素
     *
     * @param x
     * @param y
     */
    public Object[] selectAction(float x, float y) {
        boolean isSelectFlag = false;
        BaseAction selectAction = null;
        int currentIndex = -1;
        for (int i = actionList.size() - 1; i >= 0; i--) {
            BaseAction action = actionList.get(i);
            if (isSelectFlag) {
                action.setSelect(false);
                continue;
            }
            if (action instanceof LineAction) {
                LineAction lineAction = (LineAction) action;
                lineAction.setSelect(false);
                if (isSelectInAction(x, y, lineAction)) {
                    lineAction.setSelect(true);
                    isSelectFlag = true;
                    selectAction = lineAction;
                    currentIndex = i;
                }

            } else if (action instanceof RectAction) {
                RectAction rectAction = (RectAction) action;
                if (isSelectInAction(x, y, rectAction)) {
                    rectAction.setSelect(true);
                    isSelectFlag = true;
                    selectAction = rectAction;
                    currentIndex = i;
                } else {
                    rectAction.setSelect(false);
                }
            } else if (action instanceof TextAction) {
                TextAction textAction = (TextAction) action;
                textAction.setSelect(false);
            }
        }
        Object[] objects = new Object[2];
        objects[0] = selectAction;
        objects[1] = currentIndex;
        return objects;
    }

    /**
     * 判断x，y是否在矩形的坐标中
     *
     * @param x
     * @param y
     * @param action
     * @return
     */
    private boolean isSelectInAction(float x, float y, RectAction action) {
        RectF rectF = action.getRect();
        return x >= rectF.left && x <= rectF.right && y >= rectF.top && y <= rectF.bottom;
    }

    /**
     * 判断x，y是否在线的坐标中
     *
     * @param x
     * @param y
     * @param action
     * @return
     */
    public boolean isSelectInAction(float x, float y, LineAction action) {
        //触摸点的判定范围,r为半径
        float r = 30;
        //直线斜率
        /**
         * 推导公式
         * ax + by + c=0;
         * aex + bey + c = 0
         * asx + bsy + c = 0
         * a(ex-sx) + b(ey-sy) = 0
         * a(ex-sx) = -b(ey-sy)
         * (ex-sx)/(ey-sy) = -b/a
         */
        float k = (action.getEndY() - action.getStartY()) / (action.getEndX() - action.getStartX());
        float c = action.getStartY() - k * action.getStartX();
        //直线方程 y=kx + c , ax + by + c = 0
        //即 kx + c + (-y) = 0。即a=k b=-1 c=c 将圆心(触摸点)带入
        //求点到直线的距离
        double result = Math.abs((k * x - y + c) / Math.sqrt((Math.pow(k, 2) + 1)));
        //判定是否半径之内
        if (result > r) {
            return false;
        } else {
            RectF rectF = new RectF();
            rectF.left = action.getStartX() > action.getEndX() ? action.getEndX() : action.getStartX();
            rectF.right = action.getStartX() > action.getEndX() ? action.getStartX() : action.getEndX();
            rectF.top = action.getStartY() > action.getEndY() ? action.getEndY() : action.getStartY();
            rectF.bottom = action.getStartY() > action.getEndY() ? action.getStartY() : action.getEndY();
            GraphUtil.zoomRect(rectF,r);
            return rectF.contains(x,y);
        }
    }

    /**
     * 为矩形添加锚点
     *
     * @param action
     */
    private void drawRectAnchorPoint(RectAction action, Canvas canvas) {
        GradientDrawable gradientDrawable = (GradientDrawable) context.getResources().getDrawable(R.drawable.anchor_point);
        RectF rectF = action.getRect();
        Rect anchorPoint = new Rect((int) rectF.right - 11, (int) rectF.bottom - 11, (int) rectF.right + 11, (int) rectF.bottom + 11);
        action.setAnchorPointRect(anchorPoint);
        gradientDrawable.setBounds(anchorPoint);
        gradientDrawable.draw(canvas);
    }

    /**
     * 为线添加锚点
     */
    private void drawLineAnchorPoint(LineAction action, Canvas canvas) {
        GradientDrawable gradientDrawable = (GradientDrawable) context.getResources().getDrawable(R.drawable.anchor_point);
        Rect[] anchorPoints = new Rect[2];
        anchorPoints[0] = new Rect((int) action.getStartX() - 11, (int) action.getStartY() - 11,
                (int) action.getStartX() + 11, (int) action.getStartY() + 11);
        anchorPoints[1] = new Rect((int) action.getEndX() - 11, (int) action.getEndY() - 11,
                (int) action.getEndX() + 11, (int) action.getEndY() + 11);
        action.setAnchorPoints(anchorPoints);
        gradientDrawable.setBounds(anchorPoints[0]);
        gradientDrawable.draw(canvas);
        gradientDrawable.setBounds(anchorPoints[1]);
        gradientDrawable.draw(canvas);
    }

    /**
     * 选中元素
     */
    public void selectAction(int index) {
        for (int i = 0; i < actionList.size(); i++) {
            if (i == index) {
                actionList.get(i).setSelect(true);
            } else {
                actionList.get(i).setSelect(false);
            }
        }
    }

}

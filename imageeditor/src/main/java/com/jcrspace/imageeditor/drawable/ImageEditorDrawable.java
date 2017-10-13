package com.jcrspace.imageeditor.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.ColorInt;
import android.text.Layout;
import android.text.StaticLayout;

import com.jcrspace.imageeditor.action.BaseAction;
import com.jcrspace.imageeditor.action.LineAction;
import com.jcrspace.imageeditor.action.RectAction;
import com.jcrspace.imageeditor.action.TextAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangchaoren on 2017/10/12.
 */

public class ImageEditorDrawable extends BitmapDrawable {

    private List<BaseAction> actionList = new ArrayList<>();

    public ImageEditorDrawable(BitmapDrawable drawable) {
        super(drawable.getBitmap());
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
    }

    private void drawRect(Canvas canvas, RectAction action) {
        canvas.drawRect(action.getRect(), action.getPaint());
    }

    private void drawText(Canvas canvas, TextAction action) {

//        canvas.drawText(action.getText(),action.getStartX(),action.getStartY(),action.getPaint());
        canvas.save();
        canvas.translate(action.getStartX(),action.getStartY());
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

    public void clear() {
        actionList.clear();
    }


}

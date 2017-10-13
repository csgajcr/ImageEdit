package com.jcrspace.imageeditor.view;
/**
 * Created by jiangchaoren on 2017/10/11.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

import com.jcrspace.imageeditor.action.BaseAction;
import com.jcrspace.imageeditor.action.LineAction;
import com.jcrspace.imageeditor.action.RectAction;
import com.jcrspace.imageeditor.action.TextAction;
import com.jcrspace.imageeditor.common.ImageEditorState;
import com.jcrspace.imageeditor.drawable.ImageEditorDrawable;

/**
 * 支持涂鸦的ImageView
 *
 * @author ShaoWeng
 */
public class ImageEditView extends ZoomImageView {

    private ImageEditorDrawable mImageEditorDrawable;
    private float startX;
    private float startY;
    private BaseAction currentEditAction; //当前编辑的线条
    private int currentEditActionId; //当前编辑的线条Id

    float[] matrix = new float[9]; // 当前的图像的矩阵信息

    private ImageEditorState currentState = ImageEditorState.IDLE;

    public ImageEditView(Context context) {
        this(context, null);
    }

    public ImageEditView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        if (drawable instanceof ImageEditorDrawable) {
            mImageEditorDrawable = (ImageEditorDrawable) drawable;
        } else if (drawable instanceof BitmapDrawable) {
            mImageEditorDrawable = new ImageEditorDrawable((BitmapDrawable) drawable);
        } else {
            mImageEditorDrawable = null;
            super.setImageDrawable(drawable);
            return;
        }
        super.setImageDrawable(mImageEditorDrawable);

    }

    /**
     * 设置划线模式
     *
     * @param color
     * @param strokeWidth
     */
    public void setLineMode(@ColorInt int color, int strokeWidth) {
        if (mImageEditorDrawable == null) {
            return;
        }
        currentState = ImageEditorState.LINE_EDITING;
        currentEditAction = new LineAction(color, 0, 0, 0, 0, strokeWidth);
    }

    /**
     * 设置框选模式
     *
     * @param color
     * @param strokeWidth
     */
    public void setRectSelectionMode(@ColorInt int color, int strokeWidth) {
        if (mImageEditorDrawable == null) {
            return;
        }
        currentState = ImageEditorState.RECT_EDITING;
        currentEditAction = new RectAction(new RectF(0, 0, 0, 0), color, strokeWidth);
    }

    /**
     * 设置文本编辑模式
     *
     * @param color
     * @param fontSize
     */
    public void setTextEditMode(@ColorInt int color, int fontSize) {
        if (mImageEditorDrawable == null) {
            return;
        }
        currentState = ImageEditorState.TEXT_EDITING;
        currentEditAction = new TextAction("", color, 0, 0, fontSize);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (canZoom()) {
            super.onTouch(view, motionEvent);
            return true;
        }
        if (mImageEditorDrawable == null) {
            return true;
        }
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                startX = calcRealX(x);
                startY = calcRealY(y);
                if (currentState == ImageEditorState.LINE_EDITING) {
                    LineAction currentLineEditAction = (LineAction) currentEditAction;
                    currentLineEditAction.setStartX(startX);
                    currentLineEditAction.setStartY(startY);
                    currentEditActionId = mImageEditorDrawable.addLine(currentLineEditAction);
                } else if (currentState == ImageEditorState.RECT_EDITING) {
                    RectF rect = new RectF(startX, startY, startX, startY);
                    RectAction currentRectEditAction = (RectAction) currentEditAction;
                    currentRectEditAction.setRect(rect);
                    currentEditActionId = mImageEditorDrawable.addRect(currentRectEditAction);
                } else if (currentState == ImageEditorState.TEXT_EDITING) {
                    showPopView(x, y);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (currentState == ImageEditorState.LINE_EDITING) {
                    LineAction currentLineEditAction = (LineAction) currentEditAction;
                    currentLineEditAction.setEndX(calcRealX(x));
                    currentLineEditAction.setEndY(calcRealY(y));
                    mImageEditorDrawable.updateLine(currentEditActionId, currentLineEditAction);
                    invalidate();
                } else if (currentState == ImageEditorState.RECT_EDITING) {
                    RectAction currentRectEditAction = (RectAction) currentEditAction;
                    rectDirectionTransform(currentRectEditAction.getRect(), calcRealX(x), calcRealY(y));
                    mImageEditorDrawable.updateRect(currentEditActionId, currentRectEditAction);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (currentState == ImageEditorState.LINE_EDITING) {
                    currentEditAction = null;
                    currentEditActionId = -1;
                } else if (currentState == ImageEditorState.RECT_EDITING) {
                    currentEditAction = null;
                    currentEditActionId = -1;
                }
                currentState = ImageEditorState.SELECTING;
                break;
        }

        return true;
    }

    private boolean canZoom() {
        return currentState == ImageEditorState.IDLE || currentState == ImageEditorState.SELECTING;
    }

    private float calcRealX(float x) {
        //matrix 为图形矩阵的信息
        getImageMatrix().getValues(matrix);
        float scale = matrix[0]; // 缩放比率
        float transX = matrix[2]; //X轴偏移
        float realX = (x - transX) / scale;
        float imageWidth = getDrawable().getMinimumWidth();
        if (realX < 0) {
            return 0;
        } else if (realX > imageWidth) {
            return imageWidth;
        } else {
            return realX;
        }
    }

    private float calcRealY(float y) {
        //matrix 为图形矩阵的信息
        getImageMatrix().getValues(matrix);
        float scale = matrix[0]; // 缩放比率
        float transY = matrix[5]; //Y轴偏移
        float realY = (y - transY) / scale;
        float imageHeight = getDrawable().getMinimumHeight();
        if (realY < 0) {
            return 0;
        } else if (realY > imageHeight) {
            return imageHeight;
        } else {
            return realY;
        }

    }

    /**
     * 根据坐标计算是否需要变换矩形方向
     *
     * @param rectF
     */
    private RectF rectDirectionTransform(RectF rectF, float x, float y) {
        if (x < startX && y > startY) { //三象限，重新设置矩形的点
            rectF.top = startY;
            rectF.right = startX;
            rectF.left = x;
            rectF.bottom = y;
        } else if (x < startX && y < startY) { //二象限
            rectF.right = startX;
            rectF.bottom = startY;
            rectF.left = x;
            rectF.top = y;
        } else if (x > startX && y < startY) { //一象限
            rectF.left = startX;
            rectF.top = y;
            rectF.right = x;
            rectF.bottom = startY;
        } else { //四象限
            rectF.left = startX;
            rectF.top = startY;
            rectF.right = x;
            rectF.bottom = y;
        }
        return rectF;
    }

    /**
     * 显示文字编辑框
     */
    private void showPopView(float x, float y) {
        final TextAction currentTextEditAction = (TextAction) currentEditAction;
        currentEditActionId = mImageEditorDrawable.addText(currentTextEditAction);
        final EditTextPopupView popupView = new EditTextPopupView(getContext());
        popupView.setTextColor(currentTextEditAction.getColor());
        popupView.setTextSize(currentTextEditAction.getFontSize());
        popupView.showAtLocation(this, Gravity.NO_GRAVITY, getLeft() + (int) x, getTop() + (int) y);
        popupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //编辑框消失，文本编辑结束
                renderText(popupView, currentTextEditAction);
            }
        });
    }

    /**
     * 渲染文字到Image上
     */
    private void renderText(EditTextPopupView popupView, TextAction textAction) {
        String inputText = popupView.getText().toString();
        textAction.setText(inputText);
        textAction.setStartX(startX);
        textAction.setStartY(startY);
        mImageEditorDrawable.updateText(currentEditActionId, textAction);
        invalidate();
    }
}

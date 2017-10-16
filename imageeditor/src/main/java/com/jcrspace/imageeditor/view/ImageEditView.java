package com.jcrspace.imageeditor.view;
/**
 * Created by jiangchaoren on 2017/10/11.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
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
import com.jcrspace.imageeditor.utils.GraphUtil;

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
            mImageEditorDrawable = new ImageEditorDrawable((BitmapDrawable) drawable, getContext());
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
        currentState = ImageEditorState.NEW_LINE;
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
        currentState = ImageEditorState.NEW_RECT;
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
                if (currentState == ImageEditorState.NEW_LINE) {
                    //画线状态
                    LineAction currentLineEditAction = (LineAction) currentEditAction;
                    currentLineEditAction.setStartX(startX);
                    currentLineEditAction.setStartY(startY);
                    currentEditActionId = mImageEditorDrawable.addLine(currentLineEditAction);
                    currentState = ImageEditorState.LINE_EDITING;
                } else if (currentState == ImageEditorState.NEW_RECT) {
                    //画矩形状态
                    RectF rect = new RectF(startX, startY, startX, startY);
                    RectAction currentRectEditAction = (RectAction) currentEditAction;
                    currentRectEditAction.setRect(rect);
                    currentEditActionId = mImageEditorDrawable.addRect(currentRectEditAction);
                    currentState = ImageEditorState.RECT_EDITING;
                } else if (currentState == ImageEditorState.TEXT_EDITING) {
                    //文本编辑状态
                    showPopView(x, y);
                } else if (currentState == ImageEditorState.IDLE) {
                    //空闲状态
                    selectAction();
                    invalidate();
                } else if (currentState == ImageEditorState.SELECTING) {
                    isActionControl(startX, startY);
                    invalidate();
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
                } else if (currentState == ImageEditorState.MOVING) {
                    moving(calcRealX(x), calcRealY(y));
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (currentState == ImageEditorState.LINE_EDITING) {
                    mImageEditorDrawable.selectAction(currentEditActionId);
                    currentState = ImageEditorState.SELECTING;
                } else if (currentState == ImageEditorState.RECT_EDITING) {
                    mImageEditorDrawable.selectAction(currentEditActionId);
                    currentState = ImageEditorState.SELECTING;
                } else if (currentState == ImageEditorState.MOVING) {
                    currentState = ImageEditorState.SELECTING;
                }
                invalidate();
                break;
        }

        return true;
    }

    private boolean canZoom() {
        return currentState == ImageEditorState.IDLE || currentState == ImageEditorState.SELECTING;
    }

    /**
     * 计算屏幕坐标在图像中坐标的位置
     *
     * @param x
     * @return
     */
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

    /**
     * 计算屏幕坐标在图像中坐标的位置
     *
     * @param y
     * @return
     */
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

    /**
     * 判断该坐标是否在锚点中
     *
     * @param x
     * @param y
     * @return
     */
    private void isActionControl(float x, float y) {
        if (currentEditAction instanceof LineAction) {
            LineAction lineAction = (LineAction) currentEditAction;
            lineActionControl(x, y);
        } else if (currentEditAction instanceof RectAction) {
            rectActionControl(x, y); // 判断点击矩形的什么地方，锚点还是矩形内部，如果都不是则看能不能选择到其他元素
        } else if (currentEditAction instanceof TextAction) {

        }
    }

    /**
     * 移动元素
     *
     * @param x
     * @param y
     */
    private void moving(float x, float y) {
        if (currentEditAction instanceof LineAction) {

        } else if (currentEditAction instanceof RectAction) {
            RectAction rectAction = (RectAction) currentEditAction;
            RectF rectF = rectAction.getRect();
            rectF.left += x - startX;
            rectF.right += x - startX;
            rectF.top += y - startY;
            rectF.bottom += y - startY;
            mImageEditorDrawable.updateRect(currentEditActionId, rectAction);
            startX = x;
            startY = y;
        } else if (currentEditAction instanceof TextAction) {

        }
    }

    /**
     * 选中元素
     */
    private void selectAction() {
        Object[] objects = mImageEditorDrawable.selectAction(startX, startY);
        BaseAction action = (BaseAction) objects[0];
        if (action != null) {
            currentState = ImageEditorState.SELECTING;
            currentEditAction = action;
            currentEditActionId = (int) objects[1];
        } else {
            currentState = ImageEditorState.IDLE;
            currentEditAction = null;
            currentEditActionId = -1;
        }
    }

    /**
     * 当选择矩形的情况，判断他点击的啥
     *
     * @param x
     * @param y
     */
    private void rectActionControl(float x, float y) {
        RectAction rectAction = (RectAction) currentEditAction;
        Rect anchorPoint = rectAction.getAnchorPointRect();
        GraphUtil.zoomRect(anchorPoint, 5);
        if (anchorPoint.contains((int) x, (int) y)) {
            startX = rectAction.getRect().left;
            startY = rectAction.getRect().top;
            currentState = ImageEditorState.RECT_EDITING;
            return;
        }
        if (rectAction.getRect().contains(x, y)) {
            currentState = ImageEditorState.MOVING;
            return;
        }
        selectAction();
    }

    /**
     * 当选择线条的情况，判断他点击的啥
     *
     * @param x
     * @param y
     */
    private void lineActionControl(float x, float y) {
        LineAction lineAction = (LineAction) currentEditAction;
        Rect[] anchorPoints = lineAction.getAnchorPoints();
        //[0]为Start锚点，[1]为End锚点
        GraphUtil.zoomRect(anchorPoints[0], 5);
        GraphUtil.zoomRect(anchorPoints[1], 5);
        if (anchorPoints[0].contains((int) x, (int) y)) {
            //鼠标指向了起点处的锚点
            startX = lineAction.getEndX();
            startY = lineAction.getEndY();
            lineAction.setStartX(lineAction.getEndX());
            lineAction.setStartY(lineAction.getEndY());
            currentState = ImageEditorState.LINE_EDITING;
            return;
        } else if (anchorPoints[1].contains((int) x, (int) y)) {
            //鼠标指向了终点处的锚点
            startX = lineAction.getStartX();
            startY = lineAction.getStartY();
            currentState = ImageEditorState.LINE_EDITING;
            return;
        }
        selectAction();
    }

}

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
import android.view.MotionEvent;
import android.view.View;

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
    private static int EXPAND_ANCHOR_POINT_TOUCH_SIZE = 9;//扩充锚点触摸的判定范围
    private EditTextView mEditTextView;

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
        currentState = ImageEditorState.NEW_TEXT;
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
                } else if (currentState == ImageEditorState.NEW_TEXT) {
                    //文本编辑状态
                    textPopViewProcessing(x, y);
                } else if (currentState == ImageEditorState.IDLE) {
                    //空闲状态
                    selectAction();
                    invalidate();
                } else if (currentState == ImageEditorState.SELECTING) {
                    isActionControl(startX, startY);
                    invalidate();
                } else if (currentState == ImageEditorState.TEXT_EDITING) {
                    textPopViewProcessing(x, y);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (currentState == ImageEditorState.LINE_EDITING) {
                    LineAction currentLineEditAction = (LineAction) currentEditAction;
                    currentLineEditAction.setEndX(calcRealX(x));
                    currentLineEditAction.setEndY(calcRealY(y));
                    currentEditAction.setSelect(false);
                    mImageEditorDrawable.updateLine(currentEditActionId, currentLineEditAction);
                    invalidate();
                } else if (currentState == ImageEditorState.RECT_EDITING) {
                    RectAction currentRectEditAction = (RectAction) currentEditAction;
                    currentEditAction.setSelect(false);
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
                    checkRectSize();
                } else if (currentState == ImageEditorState.MOVING) {
                    currentState = ImageEditorState.SELECTING;
                }
                invalidate();
                break;
        }

        return true;
    }

    private boolean canZoom() {
        return currentState == ImageEditorState.IDLE;
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
     * 通过图像额位置，计算屏幕坐标
     * calcRealX的逆运算
     *
     * @param realX
     * @return
     */
    private float calcScreenX(float realX) {
        getImageMatrix().getValues(matrix);
        float scale = matrix[0]; // 缩放比率
        float transX = matrix[2]; //X轴偏移
        float screenX = realX * scale + transX;
        return screenX;
    }

    /**
     * 通过图像额位置，计算屏幕坐标
     * calcRealY的逆运算
     *
     * @param realY
     * @return
     */
    private float calcScreenY(float realY) {
        getImageMatrix().getValues(matrix);
        float scale = matrix[0]; // 缩放比率
        float transY = matrix[5]; //Y轴偏移
        float screenY = realY * scale + transY;
        return screenY;
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
     * 文字弹框相关处理
     *
     * @param startX
     * @param startY
     */
    private void textPopViewProcessing(float startX, float startY) {
        TextAction currentTextEditAction = (TextAction) currentEditAction;
        if (mEditTextView.getVisibility() == VISIBLE) { //在已弹出的情况下，点击其他地方。
            //编辑框消失，文本编辑结束
            renderText(currentTextEditAction);
            mEditTextView.hide();
            return;
        }
        mEditTextView.setTextColor(currentTextEditAction.getColor());
        mEditTextView.setText(currentTextEditAction.getText());
        mEditTextView.setTextSize(currentTextEditAction.getFontSize(), matrix[0]); //字大小除缩放比
        if (currentState == ImageEditorState.NEW_TEXT) {
            mEditTextView.showAtLocation(startX, startY, calcScreenX(mImageEditorDrawable.getMinimumWidth()));
            currentEditActionId = mImageEditorDrawable.addText(currentTextEditAction);
            mEditTextView.setFocus();
            mEditTextView.hideAnchor();

        } else if (currentState == ImageEditorState.TEXT_EDITING) {
            mEditTextView.showAtLocation(startX, startY, calcScreenX(currentTextEditAction.getEndX()));
            mEditTextView.clearFocus();
            mEditTextView.showAnchor();
        }
    }

    /**
     * 渲染文字到Image上
     */
    private void renderText(TextAction textAction) {
        String inputText = mEditTextView.getText().toString();
        if ("".equals(inputText)) {
            return;
        }
        textAction.setText(inputText);
        textAction.setStartX(calcRealX(mEditTextView.getLeft()));
        textAction.setStartY(calcRealY(mEditTextView.getTop()));
        textAction.setEndX(calcRealX(mEditTextView.getRight()));
        textAction.setEndY(calcRealY(mEditTextView.getBottom()));
        textAction.setTextBitmap(mEditTextView.getTextBitmap());
        textAction.setSelect(false);
        mImageEditorDrawable.updateText(currentEditActionId, textAction);
        currentState = ImageEditorState.IDLE;
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
            lineActionControl(x, y);
        } else if (currentEditAction instanceof RectAction) {
            rectActionControl(x, y); // 判断点击矩形的什么地方，锚点还是矩形内部，如果都不是则看能不能选择到其他元素
        } else if (currentEditAction instanceof TextAction) {
//            textActionControl(x, y);
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
            LineAction lineAction = (LineAction) currentEditAction;
            lineAction.setStartX(lineAction.getStartX() + x - startX);
            lineAction.setEndX(lineAction.getEndX() + x - startX);
            lineAction.setStartY(lineAction.getStartY() + y - startY);
            lineAction.setEndY(lineAction.getEndY() + y - startY);
            mImageEditorDrawable.updateLine(currentEditActionId, lineAction);
            startX = x;
            startY = y;
        } else if (currentEditAction instanceof RectAction) {
            RectAction rectAction = (RectAction) currentEditAction;
            RectF rectF = rectAction.getRect();
            float imageHeight = getDrawable().getMinimumHeight();
            float imageWidth = getDrawable().getMinimumWidth();
            RectF temp = new RectF(rectF);
            rectF.left += x - startX;
            rectF.right += x - startX;
            rectF.top += y - startY;
            rectF.bottom += y - startY;
            startX = x;
            startY = y;
            //边缘判定
            if (rectF.left < 0) {
                rectF.left = 0;
            } else if (rectF.top < 0) {
                rectF.top = 0;
            } else if (rectF.right > imageWidth) {
                rectF.right = imageWidth;
            } else if (rectF.bottom > imageHeight) {
                rectF.bottom = imageHeight;
            } else {
                return;
            }
            rectF.set(temp);


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
            //TextAction需要特殊处理，Line和RectActionDrawable会绘制好锚点。而TextAction选中后
            //drawable会停止渲染text的bitmap，需要弹出popupView来进行展示
            if (action instanceof TextAction) {
                TextAction textAction = (TextAction) action;
                currentState = ImageEditorState.TEXT_EDITING;
                textPopViewProcessing(calcScreenX(textAction.getStartX()), calcScreenY(textAction.getStartY()));
                invalidate();
            }
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
        GraphUtil.zoomRect(anchorPoint, EXPAND_ANCHOR_POINT_TOUCH_SIZE);
        if (anchorPoint.contains((int) x, (int) y)) { //点击的锚点
            startX = rectAction.getRect().left;
            startY = rectAction.getRect().top;
            currentState = ImageEditorState.RECT_EDITING;
            return;
        }
        if (rectAction.getRect().contains(x, y)) { //点击的圆形内部
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
        //[0]为Start锚点，[1]为End锚点 ，增加判定范围
        GraphUtil.zoomRect(anchorPoints[0], EXPAND_ANCHOR_POINT_TOUCH_SIZE);
        GraphUtil.zoomRect(anchorPoints[1], EXPAND_ANCHOR_POINT_TOUCH_SIZE);
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
        } else if (mImageEditorDrawable.isSelectInAction(x, y, lineAction)) {
            currentState = ImageEditorState.MOVING;
            return;
        }
        selectAction();
    }

    /**
     * 当选择文字的情况，判断他点击的啥
     *
     * @param x
     * @param y
     */
    private void textActionControl(float x, float y) {


    }

    /**
     * 检测矩形的大小，如果太小，不添加
     */
    private void checkRectSize() {
        RectAction rectAction = (RectAction) currentEditAction;
        if (rectAction.getRect().width() < 20 && rectAction.getRect().height() < 20) {
            mImageEditorDrawable.removeAction(currentEditActionId);
            currentEditActionId = -1;
            currentEditAction = null;
            currentState = ImageEditorState.IDLE;
            return;
        }
        mImageEditorDrawable.selectAction(currentEditActionId);
        currentState = ImageEditorState.SELECTING;
    }

    /**
     * 设置文字编辑框
     *
     * @param editTextView
     */
    public void setEditTextView(EditTextView editTextView) {
        mEditTextView = editTextView;
        mEditTextView.setVisibility(GONE);
    }
}

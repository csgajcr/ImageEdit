package com.jcrspace.imageeditor.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.jcrspace.imageeditor.R;
import com.jcrspace.imageeditor.common.ImageEditorState;
import com.jcrspace.imageeditor.utils.ScreenSizeUtil;

/**
 * Created by jiangchaoren on 2017/10/19.
 */

public class EditTextView extends RelativeLayout {

    private Context context;
    private View contentView;
    private EditText mEditText;
    private float scale = 1f;
    private View leftAnchorView;
    private View rightAnchorView;
    private float maxX;
    private FrameLayout inputView;
    private boolean isMoving = false;
    private ImageEditorState currentState = ImageEditorState.IDLE;
    private float lastRelativeX;
    private float lastRelativeY;
    float lastX;
    float lastY;
    private View maskView;

    public EditTextView(Context context) {
        this(context, null);
    }

    public EditTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public EditTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        initConfig();
        initListener();
    }

    private void initConfig() {
        contentView = LayoutInflater.from(context).inflate(R.layout.popupview_edittext, this);
        contentView.setFocusable(true);
        contentView.setFocusableInTouchMode(true);
        mEditText = contentView.findViewById(R.id.et_text);
        mEditText.setDrawingCacheEnabled(true);
        inputView = contentView.findViewById(R.id.fl_input);
        leftAnchorView = contentView.findViewById(R.id.anchor_left);
        rightAnchorView = contentView.findViewById(R.id.anchor_right);
        maskView = findViewById(R.id.mask_view);
        setFocusable(true);
        setVisibility(GONE);
    }

    private void initListener() {
        mEditText.setOnTouchListener(new OnTouchListener() {
            float lastLeft;
            float lastTop;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getRawX();
                float y = event.getRawY();
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = x;
                        lastY = y;
                        lastLeft = getLeft();
                        lastTop = getTop();
                        if (mEditText.hasFocus()) { //有焦点代表编辑状态，否则代表移动状态
                            currentState = ImageEditorState.TEXT_EDITING;
                            return false;
                        } else {
                            currentState = ImageEditorState.MOVING;
                            return true;
                        }

                    case MotionEvent.ACTION_MOVE:
                        if (currentState == ImageEditorState.TEXT_EDITING) {
                            return true;
                        }
                        hideKey();
                        showAtLocation(getLeft() + x - lastX, getTop() + y - lastY, maxX + x - lastX);
                        lastX = x;
                        lastY = y;
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        //移动距离太小。当成点击事件处理。移动状态下，点击文本框，就转到编辑状态
                        if (Math.abs(lastLeft - getLeft()) < 3 && Math.abs(lastTop - getTop()) < 3 && currentState == ImageEditorState.MOVING) {
                            currentState = ImageEditorState.TEXT_EDITING;
                            setFocus();
                        }
                        break;
                }
                return false;
            }
        });
        mEditText.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

    }


    public void setTextColor(@ColorInt int color) {
        mEditText.setTextColor(color);
    }

    public void setTextSize(float size, float scale) {
        //设置文字大小时，进行适应性缩放。
        mEditText.setTextSize(size * scale);
        this.scale = scale;
    }

    public CharSequence getText() {
        return mEditText.getText();
    }

    public Bitmap getTextBitmap() {
        Matrix matrix = new Matrix();
        matrix.postScale(1 / scale, 1 / scale); //生成图片的时候进行反适应性缩放
        mEditText.setCursorVisible(false);
        mEditText.destroyDrawingCache();
        Bitmap textBitmap = mEditText.getDrawingCache();
        mEditText.setCursorVisible(true);
        Bitmap convertBitmap = Bitmap.createBitmap(textBitmap, 0, 0, textBitmap.getWidth(), textBitmap.getHeight(), matrix, true);
        return convertBitmap;
    }

    public void setText(String text) {
        mEditText.setText(text);
    }

    public void clearFocus() {
        currentState = ImageEditorState.MOVING;
        mEditText.clearFocus();
        contentView.requestFocus();
    }

    public boolean setFocus() {
        currentState = ImageEditorState.TEXT_EDITING;
        return mEditText.requestFocus();
    }

    public void hideAnchor() {
        leftAnchorView.setVisibility(View.INVISIBLE);
        rightAnchorView.setVisibility(View.INVISIBLE);
    }

    public void showAnchor() {
        leftAnchorView.setVisibility(View.VISIBLE);
        rightAnchorView.setVisibility(View.VISIBLE);
    }

    //弹出键盘
    protected void upKey() {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(mEditText, 0);
    }

    //关闭键盘
    protected void hideKey() {
        InputMethodManager imm = (InputMethodManager) mEditText.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (mEditText != null) {
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0); // 强制隐藏键盘
        }
    }

    /**
     * 在指定位置显示View
     *
     * @param startX
     * @param startY
     * @param maxX   图像最右边的x坐标
     */
    public void showAtLocation(float startX, float startY, float maxX) {
        setVisibility(VISIBLE);
        this.maxX = maxX;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        layoutParams.setMargins(Math.round(startX), Math.round(startY), 0, 0);
        layoutParams.width = Math.round(maxX - startX);
        setLayoutParams(layoutParams);
    }

    /**
     * 隐藏View
     */
    public void hide() {
        setVisibility(GONE);
    }
}

package com.jcrspace.imageeditor.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.ColorInt;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.jcrspace.imageeditor.R;
import com.jcrspace.imageeditor.utils.ScreenSizeUtil;

/**
 * Created by jiangchaoren on 2017/10/13.
 */

public class EditTextPopupView extends PopupWindow {

    private Context context;
    private View contentView;
    private EditText mEditText;
    private int displayX = 0;
    private int displayY = 0;
    private float scale = 1f;
    private View leftAnchorView;
    private View rightAnchorView;
    private View parentView;
    private boolean isMoving = false;
    private float lastRelativeX;
    private float lastRelativeY;
    float lastX;
    float lastY;

    public EditTextPopupView(Context context) {
        super(context);
        this.context = context;
        initConfig();
    }

    private void initConfig() {
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        contentView = LayoutInflater.from(context).inflate(R.layout.popupview_edittext, null);
        contentView.setFocusable(true);
        contentView.setFocusableInTouchMode(true);
        mEditText = contentView.findViewById(R.id.et_text);
        mEditText.setDrawingCacheEnabled(true);
        leftAnchorView = contentView.findViewById(R.id.anchor_left);
        rightAnchorView = contentView.findViewById(R.id.anchor_right);
        setContentView(contentView);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(0x00000000));
        setFocusable(true);
        this.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getRawX();
                float y = event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = x;
                        lastY = y;
                        //锚点判定区域
                        if (isInLeftAnchorLocation(event.getX(), event.getY()) ||
                                isInRightAnchorLocation(event.getX(), event.getY())) {
                            lastRelativeX = event.getX();
                            lastRelativeY = event.getY();
                            isMoving = false;
                            return true;
                        } else {
                            isMoving = true;
                        }
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        if (isMoving) {
                            hideKey();
                            displayX += Math.round(x - lastX);
                            displayY += Math.round(y - lastY);
                            checkOutScreen();
                            update(displayX, displayY, getWidth(), getHeight());
                        } else {
                            anchorControl(event);
                        }
                        lastX = x;
                        lastY = y;
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        upKey();
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 锚点控制
     *
     * @param event
     */
    private void anchorControl(MotionEvent event) {
        boolean isStretching = mEditText.getLineCount() > 1;
        if (isInLeftAnchorLocation(event.getX(), event.getY())) {
            if (isStretching && event.getRawX() < lastX) {
                //对左锚点往左拖动
            }
        } else if (isInRightAnchorLocation(event.getX(), event.getY())) {

        }
    }

    private boolean isInLeftAnchorLocation(float x, float y) {
        int[] contentViewSize = ScreenSizeUtil.measureSpec(contentView);
        return x < 20 && x > 0 && y > 10 && y < contentViewSize[1] - 10;
    }

    private boolean isInRightAnchorLocation(float x, float y) {
        int[] contentViewSize = ScreenSizeUtil.measureSpec(contentView);
        return x < contentViewSize[0] && x > contentViewSize[0] - 20 && y > 10 && y < contentViewSize[1] - 10;
    }

    /**
     * 判断是否超出屏幕外面
     *
     * @return
     */
    private void checkOutScreen() {
        int screenWidth = ScreenSizeUtil.getScreenWidth(context);
        int[] contentViewSize = ScreenSizeUtil.measureSpec(contentView);
        if (displayX + contentViewSize[0] > screenWidth) {
            displayX = screenWidth - contentViewSize[0];
        } else if (displayX < 0){
            displayX = 0;
        }
    }

    @Override
    public void update(int x, int y, int width, int height) {
        displayX = x;
        displayY = y;
        super.update(x, y, width, height);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        showAtLocation(parent, gravity, x, y, 1);
    }

    public void showAtLocation(View parent, int gravity, int x, int y, float scale) {
        displayX = x;
        displayY = y;
        parentView = parent;
        mEditText.setMaxWidth(ScreenSizeUtil.getScreenWidth(context) - x);
        super.showAtLocation(parent, gravity, x, y);
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
        Bitmap textBitmap = mEditText.getDrawingCache();
        Bitmap convertBitmap = Bitmap.createBitmap(textBitmap, 0, 0, textBitmap.getWidth(), textBitmap.getHeight(), matrix, true);
        return convertBitmap;
    }

    public void setText(String text) {
        mEditText.setText(text);
    }

    public void clearFocus() {
        mEditText.clearFocus();
        contentView.requestFocus();
    }

    public void hideAnchor() {
        leftAnchorView.setVisibility(View.INVISIBLE);
        rightAnchorView.setVisibility(View.INVISIBLE);
    }

    public void showAnchor() {
        leftAnchorView.setVisibility(View.VISIBLE);
        rightAnchorView.setVisibility(View.VISIBLE);
    }

    public int getDisplayX() {
        return displayX;
    }

    public int getDisplayY() {
        return displayY;
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

}

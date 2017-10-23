package com.jcrspace.imageeditor.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jcrspace.imageeditor.R;

/**
 * Created by jiangchaoren on 2017/10/17.
 */

public class BottomControlPanelView extends LinearLayout implements View.OnClickListener {

    private ImageView mRectModeButton;
    private ImageView mLineModeButton;
    private ImageView mTextModeButton;
    private ImageView mUndoButton;
    private ImageView mRedoButton;
    private ImageView mCropButton;
    private ImageEditView mImageEditView;

    public BottomControlPanelView(Context context) {
        this(context, null);
    }

    public BottomControlPanelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomControlPanelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public BottomControlPanelView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        LayoutInflater.from(context).inflate(R.layout.bottom_control_panel, this);
        initView();
        initListener();
    }

    private void initView() {
        mRectModeButton = findViewById(R.id.btn_rect_mode);
        mLineModeButton = findViewById(R.id.btn_line_mode);
        mTextModeButton = findViewById(R.id.btn_text_mode);
        mUndoButton = findViewById(R.id.btn_undo);
        mRedoButton = findViewById(R.id.btn_redo);
        mCropButton = findViewById(R.id.btn_crop);
    }

    private void initListener() {
        mRectModeButton.setOnClickListener(this);
        mLineModeButton.setOnClickListener(this);
        mTextModeButton.setOnClickListener(this);
        mUndoButton.setOnClickListener(this);
        mRedoButton.setOnClickListener(this);
        mCropButton.setOnClickListener(this);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (mImageEditView == null) {
            return;
        }
        int id = v.getId();
        if (id == R.id.btn_rect_mode) {
            toggleButtonColor(mRectModeButton);
            mImageEditView.setRectSelectionMode(Color.RED, 10);
        } else if (id == R.id.btn_line_mode) {
            toggleButtonColor(mLineModeButton);
            mImageEditView.setLineMode(Color.GREEN, 10);
        } else if (id == R.id.btn_text_mode) {
            toggleButtonColor(mTextModeButton);
            mImageEditView.setTextEditMode(Color.BLACK, 20);
        } else if (id == R.id.btn_undo) {

        } else if (id == R.id.btn_redo) {

        } else if (id == R.id.btn_crop) {

        }
    }

    /**
     * 设置按钮颜色
     *
     * @param imageView
     */
    private void toggleButtonColor(ImageView imageView) {
        int activeColor = getResources().getColor(R.color.toolbar_btn_selected);
        int normalColor = getResources().getColor(R.color.white);
        mRectModeButton.getDrawable().setTint(normalColor);
        mLineModeButton.getDrawable().setTint(normalColor);
        mTextModeButton.getDrawable().setTint(normalColor);
        imageView.getDrawable().setTint(activeColor);
    }

    /**
     * 与ImageEditView绑定
     *
     * @param imageEditView
     */
    public void bindImageEditView(ImageEditView imageEditView) {
        mImageEditView = imageEditView;
    }

}

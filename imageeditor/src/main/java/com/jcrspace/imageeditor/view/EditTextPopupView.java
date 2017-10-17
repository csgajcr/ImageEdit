package com.jcrspace.imageeditor.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.ColorInt;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.jcrspace.imageeditor.R;

/**
 * Created by jiangchaoren on 2017/10/13.
 */

public class EditTextPopupView extends PopupWindow {

    private Context context;
    private View contentView;
    private EditText mEditText;

    public EditTextPopupView(Context context) {
        super(context);
        this.context = context;
        initConfig();
    }

    private void initConfig(){
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        contentView = LayoutInflater.from(context).inflate(R.layout.popupview_edittext, null);
        mEditText = contentView.findViewById(R.id.et_text);
        mEditText.setDrawingCacheEnabled(true);
        setContentView(contentView);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(0x00000000));
        setFocusable(true);
        this.setTouchInterceptor(new View.OnTouchListener() {
            float lastX;
            float lastY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        lastX = x;
                        lastY = y;
                        break;
                    case MotionEvent.ACTION_MOVE:

                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:

                        break;
                }
                return false;
            }
        });
    }



    public void setTextColor(@ColorInt int color){
        mEditText.setTextColor(color);
    }

    public void setTextSize(int size){
        mEditText.setTextSize(size);
    }

    public CharSequence getText(){
        return mEditText.getText();
    }

    public Bitmap getTextBitmap(){
        return mEditText.getDrawingCache();
    }
}

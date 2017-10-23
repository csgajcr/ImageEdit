package com.jcrspace.imageeditor;
/**
 * Created by jiangchaoren on 2017/10/11.
 */

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;

import com.jcrspace.imageeditor.view.BottomControlPanelView;
import com.jcrspace.imageeditor.view.EditTextView;
import com.jcrspace.imageeditor.view.ImageEditView;

/**
 * 图片编辑
 *
 * @author ShaoWeng
 */
public class ImageEditActivity extends Activity{

    private ImageEditView imageEditView;
    private BottomControlPanelView bottomControlPanelView;
    private EditTextView editTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image_edit);
        imageEditView = (ImageEditView) findViewById(R.id.img_view);
        bottomControlPanelView = findViewById(R.id.bottom_control_view);
        bottomControlPanelView.bindImageEditView(imageEditView);
        editTextView = findViewById(R.id.ev_text);
        imageEditView.setEditTextView(editTextView);
    }
}

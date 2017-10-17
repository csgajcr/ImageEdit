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

import com.jcrspace.imageeditor.view.ImageEditView;

/**
 * 图片编辑
 *
 * @author ShaoWeng
 */
public class ImageEditActivity extends Activity{

    private ImageEditView imageEditView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image_edit);
        imageEditView = (ImageEditView) findViewById(R.id.img_view);
//        findViewById(R.id.btn_add_line).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                imageEditView.setLineMode(Color.RED, 10);
//            }
//        });
//        findViewById(R.id.btn_add_rect).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                imageEditView.setRectSelectionMode(Color.RED, 10);
//            }
//        });
//        findViewById(R.id.btn_add_text).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                imageEditView.setTextEditMode(Color.GREEN, 20);
//            }
//        });
    }
}

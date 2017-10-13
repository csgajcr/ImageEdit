package com.jcrspace.imageeditor.common;

/**
 * Created by jiangchaoren on 2017/10/12.
 */

/**
 * 图片编辑View的状态
 */
public enum ImageEditorState {
    //选中
    SELECTING,
    //划线编辑中
    LINE_EDITING,
    //框选编辑中
    RECT_EDITING,
    //文本编辑中
    TEXT_EDITING,
    //空闲
    IDLE
}

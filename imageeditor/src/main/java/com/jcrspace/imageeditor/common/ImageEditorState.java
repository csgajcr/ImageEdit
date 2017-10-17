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
    //新框选
    NEW_RECT,
    //新划线,
    NEW_LINE,
    //新文本,
    NEW_TEXT,
    //移动,
    MOVING,
    //空闲
    IDLE
}

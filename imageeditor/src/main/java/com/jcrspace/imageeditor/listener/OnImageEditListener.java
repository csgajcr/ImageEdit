package com.jcrspace.imageeditor.listener;

/**
 * Created by jiangchaoren on 2017/10/17.
 */

import com.jcrspace.imageeditor.action.BaseAction;

/**
 * 图片编辑监听器
 */
public interface OnImageEditListener {
    void onActionDown(BaseAction action, float x, float y);

    void onActionMove(BaseAction action, float x, float y);

    void onActionUp(BaseAction action, float x, float y);
}

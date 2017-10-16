package com.jcrspace.imageeditor.utils;

import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by jiangchaoren on 2017/10/16.
 */

public class GraphUtil {

    public static void zoomRect(Rect rect, int size) {
        rect.bottom += size;
        rect.right += size;
        rect.top += size;
        rect.left += size;
    }

    public static void zoomRect(RectF rect, float size) {
        rect.bottom += size;
        rect.right += size;
        rect.top += size;
        rect.left += size;
    }
}

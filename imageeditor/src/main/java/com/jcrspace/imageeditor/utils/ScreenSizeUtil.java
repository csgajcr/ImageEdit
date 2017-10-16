package com.jcrspace.imageeditor.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.math.BigDecimal;

public class ScreenSizeUtil {
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)(四舍五入)
     */
    public static int dip2Px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        float px = (dpValue * scale + 0.5f);

        BigDecimal b = new BigDecimal(px);
        int intPx = b.setScale(0, BigDecimal.ROUND_HALF_UP).intValue(); // 四舍五入高度
        return intPx;
    }

    public static double getTextHeight(float frontSize) {
        Paint paint = new Paint();
        paint.setTextSize(frontSize);
        Paint.FontMetrics fm = paint.getFontMetrics();
        return Math.ceil(fm.descent - fm.top) + 2;
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp (四舍五入)
     */
    public static int px2Dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        float dip = (pxValue / scale + 0.5f);

        BigDecimal b = new BigDecimal(dip);
        int intDip = b.setScale(0, BigDecimal.ROUND_HALF_UP).intValue(); // 四舍五入高度
        return intDip;
    }

    /**
     * 获取屏幕宽度
     */
    @SuppressWarnings("deprecation")
    public static int getScreenWidth(Activity aty) {
        return aty.getWindowManager().getDefaultDisplay().getWidth();
    }

    /**
     * 获取屏幕高度
     */
    @SuppressWarnings("deprecation")
    public static int getScreenHeight(Activity aty) {
        return aty.getWindowManager().getDefaultDisplay().getHeight();
    }


    /**
     * 获取屏幕宽度
     */
    @SuppressWarnings("deprecation")
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }

    /**
     * 获取屏幕高度
     */
    @SuppressWarnings("deprecation")
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        return height;
    }
    /**
     * 获取顶部状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier(
                "status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取底部菜单栏高度
     *
     * @param context
     * @return
     */
    public static int getBottomBarHeight(Context context) {

        int result = 0;
        boolean isShow = false;
        int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid > 0) {
            isShow = context.getResources().getBoolean(rid);
        }
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (isShow && resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 测量宽高，measure[0] = width，measure[1] = height
     */
    public static int[] measureSpec(View view) {
        int[] measure = new int[2];
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        view.measure(w, h);
        int height = view.getMeasuredHeight();
        int width = view.getMeasuredWidth();
        measure[0] = width;
        measure[1] = height;
        return measure;
    }

}

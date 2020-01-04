package com.dome.push.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;

/**
 * ProjectName:    TestJPushIM
 * Package:        com.dome.push.util
 * ClassName:      ScreenTool
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-9 上午9:21
 * UpdateUser:     更新者
 * UpdateDate:     19-12-9 上午9:21
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class ScreenTool {
    private static final String TAG = "Demo.ScreenUtil";

    private static double RATIO = 0.85;

    private static int screenWidth;
    private static int screenHeight;
    private static int screenMin;// 宽高中，小的一边
    private static int screenMax;// 宽高中，较大的值

    private static float density;
    private static float scaleDensity;
    private static float xdpi;
    private static float ydpi;
    private static int densityDpi;

    private static int dialogWidth;
    private static int statusbarheight;
    private static int navbarheight;

    static {
        init(ApplicationTool.getApp());
    }

    public static int dip2px(float dipValue) {
        return (int) (dipValue * density + 0.5f);
    }

    public static int px2dip(float pxValue) {
        return (int) (pxValue / density + 0.5f);
    }
    /**
     * 将px值转换为dp值
     */
    public static int px2dp(float pxValue) {
        final float scale = ApplicationTool.getApp().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int sp2px(float spValue) {
        return (int) (spValue * scaleDensity + 0.5f);
    }

    public static int getDialogWidth() {
        dialogWidth = (int) (screenMin * RATIO);
        return dialogWidth;
    }

    public static void init(Context context) {
        if (null == context) {
            return;
        }
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        screenMin = (screenWidth > screenHeight) ? screenHeight : screenWidth;
        density = dm.density;
        scaleDensity = dm.scaledDensity;
        xdpi = dm.xdpi;
        ydpi = dm.ydpi;
        densityDpi = dm.densityDpi;

        Log.d(TAG, "screenWidth=" + screenWidth + " screenHeight=" + screenHeight + " density=" + density);
    }

    public static int getDisplayWidth() {
        if (screenWidth == 0) {
            GetInfo(ApplicationTool.getApp());
        }
        return screenWidth;
    }

    public static int getDisplayHeight() {
        if (screenHeight == 0) {
            GetInfo(ApplicationTool.getApp());
        }
        return screenHeight;
    }

    public static void GetInfo(Context context) {
        if (null == context) {
            return;
        }
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        screenMin = (screenWidth > screenHeight) ? screenHeight : screenWidth;
        screenMax = (screenWidth < screenHeight) ? screenHeight : screenWidth;
        density = dm.density;
        scaleDensity = dm.scaledDensity;
        xdpi = dm.xdpi;
        ydpi = dm.ydpi;
        densityDpi = dm.densityDpi;
        statusbarheight = getStatusBarHeight(context);
        navbarheight = getNavBarHeight(context);
        Log.d(TAG, "screenWidth=" + screenWidth + " screenHeight=" + screenHeight + " density=" + density);
    }

    public static int getStatusBarHeight(Context context) {
        if (statusbarheight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusbarheight = context.getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (statusbarheight == 0) {
            statusbarheight = dip2px(25);
        }
        return statusbarheight;
    }

    public static int getNavBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    /**
     * Return the status bar's height.
     *
     * @return the status bar's height
     */
    public static int getStatusBarHeight() {
        Resources resources = ApplicationTool.getApp().getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    public static int dp2px(float dpValue) {
        float density = ApplicationTool.getApp().getResources().getDisplayMetrics().density;

        return (int) (density * dpValue + 0.5);
    }
    /**
     * Set full screen.
     *
     * @param activity The activity.
     */
    static void setFullScreen(@NonNull final Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static float getDensity() {
        return density;
    }
}

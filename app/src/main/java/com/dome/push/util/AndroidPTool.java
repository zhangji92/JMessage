package com.dome.push.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowInsets;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * ProjectName:    Bicycle
 * Package:        com.chetianxia.yundanche.utils
 * ClassName:      AndroidWorkaround
 * Description:    Android 兼容性问题
 * Author:         张继
 * CreateDate:     2019/4/24 21:56
 * UpdateUser:     更新者
 * UpdateDate:     2019/4/24 21:56
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public final class AndroidPTool {
    private static int Pheight = 0;
    private static final int VIVO_NOTCH = 0x00000020;//是否有刘海

    private AndroidPTool() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    static boolean isp(Context context) {
        //hasNotchScreen((Activity) context);
        return hasNotchScreen((Activity) context);

    }

    private static boolean hasNotchScreen(Activity activity) {
        return getInt("ro.miui.notch", activity) == 1 || hasNotchAtHuawei(activity) || hasNotchAtOPPO(activity)
                || hasNotchAtVivo(activity);
    }

    /**
     * 小米刘海屏判断.
     *
     * @return 0 if it is not notch ; return 1 means notch
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    private static int getInt(String key, Activity activity) {
        int result = 0;
        try {
            ClassLoader classLoader = activity.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = classLoader.loadClass("android.os.SystemProperties");
            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes = new Class[2];
            paramTypes[0] = String.class;
            paramTypes[1] = int.class;
            Method getInt = SystemProperties.getMethod("getInt", paramTypes);
            //参数
            Object[] params = new Object[2];
            params[0] = new String(key);
            params[1] = new Integer(0);
            result = (Integer) getInt.invoke(SystemProperties, params);

        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        if (result == 1) {
            Pheight = 1;
        }
        return result;
    }

    /**
     * 华为刘海屏判断
     *
     * @return true有刘海 false无刘海
     */
    private static boolean hasNotchAtHuawei(Context context) {
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class HwNotchSizeUtil = classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ret) {
                getNotchSize(context);
            }
            return ret;
        }
    }


    /**
     * VIVO刘海屏判断
     *
     * @return true有刘海 false无刘海
     */
    private static boolean hasNotchAtVivo(Context context) {
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class FtFeature = classLoader.loadClass("android.util.FtFeature");
            Method method = FtFeature.getMethod("isFeatureSupport", int.class);
            ret = (boolean) method.invoke(FtFeature, VIVO_NOTCH);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ret) {
                Pheight = 1;
            }
            return ret;
        }
    }

    /**
     * OPPO刘海屏判断
     *
     * @return true有刘海 false无刘海
     */
    private static boolean hasNotchAtOPPO(Context context) {
        if (context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism")) {
            getStatusBarHeight(context);
        }
        return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");

    }

    //华为高度获取
    private static int[] getNotchSize(Context context) {
        int[] ret = new int[]{0, 0};
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("getNotchSize");
            ret = (int[]) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Log.e("test", "getNotchSize ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e("test", "getNotchSize NoSuchMethodException");
        } catch (Exception e) {
            Log.e("test", "getNotchSize Exception");
        } finally {
            Pheight = ret[1];
            return ret;
        }
    }

    //oppo高度获取
    private static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        Pheight = statusBarHeight;
        return statusBarHeight;
    }


    /**
     * Android P 刘海屏判断
     * @param activity 活动
     * @return 返回
     */
    public static DisplayCutout isAndroidP(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        if (decorView != null && android.os.Build.VERSION.SDK_INT >= 28) {
            WindowInsets windowInsets = decorView.getRootWindowInsets();
            if (windowInsets != null)
                return null;
        }
        return null;
    }
}

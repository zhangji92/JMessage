package com.dome.push.util;

import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;


final class CameraUtils {
    /**
     * 摄像机闪光灯状态
     */
    static final String CAMERA_FLASH = "chat_camera_flash";
    /**
     * 摄像机前后置状态
     */
    static final String CAMERA_AROUND = "chat_camera_around";

    private CameraUtils() {
    }

    /**
     * 获取相机闪光灯状态
     *
     * @return 0为自动, 1为打开, 其他为关闭
     */
    static int getCameraFlash(Context context) {
        int anInt = SPTool.getInstance().getInt(CAMERA_AROUND, 0);
        return anInt;
    }

    /**
     * 设置相机闪光状态
     *
     * @param flash
     */
    static void setCameraFlash(Context context, int flash) {
        SPTool.getInstance().put(CAMERA_FLASH, flash);
    }

    /**
     * 获取摄像头是否为前置或后
     *
     * @param context
     * @return 0为后置, 1为前置
     */
    static int getCameraFacing(Context context, int defaultId) {

        int anInt = SPTool.getInstance().getInt(CAMERA_AROUND, defaultId);
        return anInt;
    }

    /**
     * 设置摄像头前置或后
     *
     * @param context
     * @param around
     */
    static void setCameraFacing(Context context, int around) {
        SPTool.getInstance().put(CAMERA_AROUND, around);
    }

    /**
     * 摄像机是否支持前置拍照
     *
     * @return
     */
    static boolean isSupportFrontCamera() {
        final int cameraCount = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否支持闪光
     *
     * @param context
     * @return
     */
    static boolean isSupportFlashCamera(Context context) {
        FeatureInfo[] features = context.getPackageManager().getSystemAvailableFeatures();
        for (FeatureInfo info : features) {
            if (PackageManager.FEATURE_CAMERA_FLASH.equals(info.name))
                return true;
        }
        return false;
    }

}

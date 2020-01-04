package com.dome.push.util;

import android.content.Context;

import com.dome.push.listener.IDeniedListener;
import com.dome.push.listener.IGrantedListener;

import java.util.List;

/**
 * ProjectName:    Stock
 * Package:        cn.com.yundanche.stock.utils
 * ClassName:      PermissionHelper
 * Description:    权限工具类
 * Author:         张继
 * CreateDate:     2019/4/28 22:30
 * UpdateUser:     更新者
 * UpdateDate:     2019/4/28 22:30
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public final class PermissionHelper {
    /**
     * 存储权限
     */
    public static void requestStorage(Context context, IGrantedListener grantedListener,
                                      IDeniedListener deniedListener) {
        request(context, grantedListener, deniedListener, PermissionConstants.STORAGE);
    }

    /**
     * 录音
     */
    public static void requestMicrophone(Context context, IGrantedListener grantedListener,
                                         IDeniedListener deniedListener) {
        request(context, grantedListener, deniedListener, PermissionConstants.MICROPHONE);
    }


    /**
     * 相机
     */
    public static void requestCamera(Context context, IGrantedListener grantedListener,
                                     IDeniedListener deniedListener) {
        request(context, grantedListener, deniedListener, PermissionConstants.CAMERA);
    }

    /**
     * 相机定位权限
     */
    public static void requestCameraLocation(Context context, IGrantedListener grantedListener,
                                             IDeniedListener deniedListener) {
        request(context, grantedListener, deniedListener, PermissionConstants.CAMERA, PermissionConstants.LOCATION);
    }


    /**
     * 手机权限
     */
    public static void requestPhone(Context context, IGrantedListener grantedListener,
                                    IDeniedListener deniedListener) {
        request(context, grantedListener, deniedListener, PermissionConstants.PHONE);
    }

    /**
     * 定位权限
     */
    public static void requestLocation(Context context, IGrantedListener grantedListener,
                                       IDeniedListener deniedListener) {
        request(context, grantedListener, deniedListener, PermissionConstants.LOCATION);
    }

    private static void request(Context context, IGrantedListener grantedListener,
                                @PermissionConstants.Permission String... permissions) {
        request(context, grantedListener, null, permissions);
    }

    /**
     * @param grantedListener 授权接口
     * @param deniedListener  拒绝接口
     * @param permissions     权限
     */
    private static void request(Context context, IGrantedListener grantedListener,
                                final IDeniedListener deniedListener,
                                @PermissionConstants.Permission String... permissions) {
        PermissionTool.permission(permissions)
                .rationale(shouldRequest -> {
                    shouldRequest.again(true);
                }).callback(new PermissionTool.FullCallback() {
            @Override
            public void onGranted(List<String> permissionsGranted) {
                grantedListener.onPermissionGranted();
            }

            @Override
            public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied, PermissionTool.OnRationaleListener.ShouldRequest request) {
                if (!permissionsDeniedForever.isEmpty()) {
                    DialogHelper.showOpenAppSettingDialog(context);
                    return;
                }
                deniedListener.onPermissionDenied(request);
            }
        }).theme(ScreenTool::setFullScreen).request();
    }

}

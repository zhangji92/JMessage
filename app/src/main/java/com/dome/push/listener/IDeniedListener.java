package com.dome.push.listener;


import com.dome.push.util.PermissionTool;

/**
 * ProjectName:    stock
 * Package:        cn.com.yundanche.stock.view
 * ClassName:      IDeniedListener
 * Description:    描述
 * Author:         张继
 * CreateDate:     2019/5/21 17:37
 * UpdateUser:     更新者
 * UpdateDate:     2019/5/21 17:37
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public interface IDeniedListener {
    void onPermissionDenied(PermissionTool.OnRationaleListener.ShouldRequest shouldRequest);
}

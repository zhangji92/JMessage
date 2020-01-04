package com.dome.push.listener;

/**
 * ProjectName:    Motion
 * Package:        com.dome.push.listener
 * ClassName:      OnResizeListener
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-23 下午1:57
 * UpdateUser:     更新者
 * UpdateDate:     19-12-23 下午1:57
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public interface OnResizeListener {
    /**
     * 软键盘弹起
     */
    void onSoftPop(int height);

    /**
     * 软键盘关闭
     */
    void onSoftClose();

}

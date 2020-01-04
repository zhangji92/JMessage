package com.dome.push.base;

import androidx.fragment.app.FragmentActivity;

import com.dome.push.exception.ExceptionTool;

/**
 * ProjectName:    Bicycle
 * Package:        com.chetianxia.yundanche.base
 * ClassName:      IBaseView
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-10-19 下午7:37
 * UpdateUser:     更新者
 * UpdateDate:     19-10-19 下午7:37
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public interface IBaseView {
    void showDialog();

    void onError(ExceptionTool exception);

    void hideDialog();


    FragmentActivity getBaseActivity();

    void showMsg(String msg, int shortOrLong);
}

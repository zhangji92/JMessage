package com.dome.push.mvp.view;

import android.widget.EditText;

import com.dome.push.base.IBaseView;

/**
 * ProjectName:    TestJPushIM
 * Package:        com.dome.push.mvp.view
 * ClassName:      ILoginView
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-12-13 上午9:22
 * UpdateUser:     更新者
 * UpdateDate:     19-12-13 上午9:22
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public interface ILoginView extends IBaseView {
    EditText name();

    EditText pwd();
}
